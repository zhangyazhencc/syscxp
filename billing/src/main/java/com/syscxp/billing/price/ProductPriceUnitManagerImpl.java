package com.syscxp.billing.price;

import com.syscxp.billing.header.price.*;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;

public class ProductPriceUnitManagerImpl extends AbstractService implements ProductPriceUnitManager,ApiMessageInterceptor {

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private EventFacade evtf;



    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateTunnelProductPriceUnitMsg){
            handle((APICreateTunnelProductPriceUnitMsg) msg);
        }else if(msg instanceof APICreateECPProductPriceUnitMsg){
            handle((APICreateECPProductPriceUnitMsg) msg);
        }else if(msg instanceof APIDeleteProductPriceUnitMsg){
            handle((APIDeleteProductPriceUnitMsg) msg);
        }else if(msg instanceof APIUpdateProductPriceUnitMsg){
            handle((APIUpdateProductPriceUnitMsg) msg);
        }else if(msg instanceof APIGetProductCategoryListMsg){
            handle((APIGetProductCategoryListMsg) msg);
        }else if(msg instanceof APIGetModifyProductPriceDiffMsg){
            handle((APIGetModifyProductPriceDiffMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetModifyProductPriceDiffMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<ProductPriceUnit> units = msg.getUnits();
        List<String> productPriceUnitUuids = new ArrayList<String>();
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
            query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, unit.getCategoryCode());
            query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, unit.getProductTypeCode());
            ProductCategoryVO productCategoryVO = query.find();
            if(productCategoryVO ==null){
                throw new IllegalArgumentException("can not find productType or category");
            }
            int times = 1;

            if(unit.getProductTypeCode().equals(ProductType.ECP) && unit.getCategoryCode().equals(Category.BANDWIDTH)){
                String configCode = unit.getConfigCode().replaceAll("\\D","");
                times = Integer.parseInt(configCode);
                unit.setConfigCode("1M");
            }
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryVO.getUuid());
            q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, unit.getAreaCode());
            q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, unit.getLineCode());
            q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, unit.getConfigCode());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            productPriceUnitUuids.add(productPriceUnitVO.getUuid());

            SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
            qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productPriceUnitVO.getProductCategoryUuid());
            qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDiscountVO accountDiscountVO = qDiscount.find();
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()*times));
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            discountPrice = discountPrice.add(currentDiscount);

        }
        Timestamp endTime = msg.getExpiredTime();
        long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);

        SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
        query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        RenewVO renewVO = query.find();
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal needPayMoney = discountPrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal needPayOriginMoney = originalPrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal subMoney = needPayMoney.subtract(remainMoney);

        APIGetModifyProductPriceDiffReply reply = new APIGetModifyProductPriceDiffReply();
        reply.setNeedPayMoney(needPayMoney);
        reply.setRemainMoney(remainMoney);
        reply.setNeedPayOriginMoney(needPayOriginMoney);
        reply.setSubMoney(subMoney);
        bus.reply(msg,reply);
    }

    private void handle(APIGetProductCategoryListMsg msg) {
        SimpleQuery<ProductCategoryVO> q = dbf.createQuery(ProductCategoryVO.class);
        q.groupBy(ProductCategoryVO_.productTypeCode);
        List<ProductCategoryVO> adVO = q.list();
        List<ProductDataDictionary> productTypes = new ArrayList<>();
        for(ProductCategoryVO eo: adVO){
            ProductDataDictionary dictionary = new ProductDataDictionary();
            dictionary.setCode(eo.getProductTypeCode().toString());
            dictionary.setName(eo.getProductTypeName());
            SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
            query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, eo.getProductTypeCode());
            List<ProductCategoryVO> pcVOs = query.list();
            dictionary.setCategories(ProductCategoryInventory.valueOf(pcVOs));
            productTypes.add(dictionary);
        }
        APIGetProductCategoryListReply reply = new APIGetProductCategoryListReply();
        reply.setInventories(productTypes);
        bus.reply(msg,reply);
    }

    private void handle(APIUpdateProductPriceUnitMsg msg) {
        ProductPriceUnitVO vo = dbf.findByUuid(msg.getUuid(),ProductPriceUnitVO.class);
        if(msg.getUnitPrice() > 0){
            vo.setUnitPrice(msg.getUnitPrice());
        }

        dbf.updateAndRefresh(vo);

        APIUpdateProductPriceUnitEvent evt = new APIUpdateProductPriceUnitEvent(msg.getId());
        evt.setInventory(ProductPriceUnitInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteProductPriceUnitMsg msg) {
        APIDeleteProductPriceUnitEvent evt = new APIDeleteProductPriceUnitEvent(msg.getId());
        if(!StringUtils.isEmpty(msg.getLineName())){

            UpdateQuery q = UpdateQuery.New(ProductPriceUnitVO.class);
            q.condAnd(ProductPriceUnitVO_.lineName, SimpleQuery.Op.EQ, msg.getLineName());
            q.condAnd(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, msg.getProductCategoryUuid());
            q.delete();

        }else if (!StringUtils.isEmpty(msg.getUuid())){
            ProductPriceUnitVO vo = dbf.findByUuid(msg.getUuid(),ProductPriceUnitVO.class);
            dbf.remove(vo);
            evt.setInventory(ProductPriceUnitInventory.valueOf(vo));
        }

        bus.publish(evt);

    }

    private void handle(APICreateECPProductPriceUnitMsg msg) {
        ProductPriceUnitVO vo = new ProductPriceUnitVO();

        vo.setUuid(Platform.getUuid());
        vo.setProductCategoryUuid(msg.getProductCategoryUuid());
        vo.setAreaCode(msg.getAreaCode());
        vo.setAreaName(msg.getAreaName());
        vo.setLineCode(msg.getLineName());
        vo.setLineName(msg.getLineName());
        vo.setConfigName(msg.getConfigName());
        vo.setConfigCode(msg.getConfigCode());
        vo.setUnitPrice(msg.getUnitPrice());

        dbf.persistAndRefresh(vo);

        APICreateVHostProductPriceUnitEvent evt = new APICreateVHostProductPriceUnitEvent(msg.getId());
        evt.setInventory(ProductPriceUnitInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateTunnelProductPriceUnitMsg msg) {
        List<ProductPriceUnitVO> productPriceUnitVOList = new ArrayList<ProductPriceUnitVO>();

        Map configPriceMap = msg.getConfigPrice();

        Iterator entries = configPriceMap.entrySet().iterator();

        while (entries.hasNext()) {
            ProductPriceUnitVO vo = new ProductPriceUnitVO();
            Map.Entry entry = (Map.Entry) entries.next();

            vo.setUuid(Platform.getUuid());
            vo.setProductCategoryUuid(msg.getProductCategoryUuid());
            vo.setAreaCode(msg.getAreaCode());
            vo.setAreaName(msg.getAreaName());
            vo.setLineCode(msg.getLineName());
            vo.setLineName(msg.getLineName());
            vo.setConfigCode((String) entry.getKey());
            vo.setConfigName((String) entry.getKey());
            vo.setUnitPrice((Integer) entry.getValue());
            productPriceUnitVOList.add(vo);

        }

        dbf.persistCollection(productPriceUnitVOList);
        APICreateTunnelProductPriceUnitEvent evt = new APICreateTunnelProductPriceUnitEvent(msg.getId());
        evt.setInventoryList(ProductPriceUnitInventory.valueOf(productPriceUnitVOList));
        bus.publish(evt);
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(ProductPriceUnitConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
