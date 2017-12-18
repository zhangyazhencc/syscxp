package com.syscxp.billing.price;

import com.syscxp.billing.header.balance.ExpenseGross;
import com.syscxp.billing.header.price.*;
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

import javax.persistence.Query;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ProductPriceUnitManagerImpl extends AbstractService implements ProductPriceUnitManager, ApiMessageInterceptor {

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
        if (msg instanceof APICreateTunnelProductPriceUnitMsg) {
            handle((APICreateTunnelProductPriceUnitMsg) msg);
        } else if (msg instanceof APICreateECPProductPriceUnitMsg) {
            handle((APICreateECPProductPriceUnitMsg) msg);
        } else if (msg instanceof APIDeleteProductPriceUnitMsg) {
            handle((APIDeleteProductPriceUnitMsg) msg);
        } else if (msg instanceof APIUpdateProductPriceUnitMsg) {
            handle((APIUpdateProductPriceUnitMsg) msg);
        } else if (msg instanceof APIGetProductCategoryListMsg) {
            handle((APIGetProductCategoryListMsg) msg);
        } else if (msg instanceof APIGetBroadPriceListMsg) {
            handle((APIGetBroadPriceListMsg) msg);
        } else if (msg instanceof APIUpdateBroadPriceMsg) {
            handle((APIUpdateBroadPriceMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIUpdateBroadPriceMsg msg) {
        ProductCategoryVO vo = findProductCategoryVO(msg.getProductType(), msg.getCategory());
        if (vo == null) {
            throw new IllegalArgumentException("can not find the product type or category");
        }
        SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
        q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, vo.getUuid());
        q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, msg.getAreaCode());
        q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, msg.getLineCode());
        q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, msg.getConfigCode());
        ProductPriceUnitVO productPriceUnitVO = q.find();
        if (productPriceUnitVO == null) {
            throw new IllegalArgumentException("can not find the product price in database");
        }
        productPriceUnitVO.setUnitPrice(msg.getPrice());
        dbf.updateAndRefresh(productPriceUnitVO);
        APIUpdateBroadPriceEvent event = new APIUpdateBroadPriceEvent(msg.getId());
        event.setInventory(productPriceUnitVO);
        bus.publish(event);
    }

    private void handle(APIGetBroadPriceListMsg msg) {

        ProductCategoryVO vo = findProductCategoryVO(msg.getProductType(), msg.getCategory());
        if (vo == null) {
            throw new IllegalArgumentException("can not find the product type or category");
        }
        String sql = "SELECT areaCode,lineCode,GROUP_CONCAT(CONCAT(CONCAT(configCode,'-'),unitPrice)) AS configMixPrice FROM `ProductPriceUnitVO` WHERE productCategoryUuid = :productCategoryUuid AND areaCode = :areaCode  GROUP BY lineCode";
        if (msg.getCategory().equals(Category.REGION)) {
            sql = "SELECT areaName as areaCode,lineCode,GROUP_CONCAT(CONCAT(CONCAT(configCode,'-'),unitPrice)) AS configMixPrice FROM `ProductPriceUnitVO` WHERE productCategoryUuid = :productCategoryUuid  GROUP BY areaCode";
        }

        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("productCategoryUuid", vo.getUuid());
        if (!msg.getCategory().equals(Category.REGION)) {
            q.setParameter("areaCode", msg.getAreaCode());
        }
        List<Object[]> objs = q.getResultList();
        List<PriceData> vos = objs.stream().map(PriceData::new).collect(Collectors.toList());
        vos.forEach(priceData -> {
            Arrays.stream(priceData.getConfigMixPrice().split(",")).forEach(e -> {
                String[] split = e.split("-");
                setPrice(split[0], new BigDecimal(split[1]), priceData);
            });
        });

        APIGetBroadPriceListReply reply = new APIGetBroadPriceListReply();
        reply.setInventories(vos);
        bus.reply(msg, reply);
    }

    private void setPrice(String configName, BigDecimal price, PriceData priceData) {
        Class clazz = priceData.getClass();
        try {
            Method m = clazz.getMethod("setConfig" + configName.toUpperCase() + "Price", new Class[]{BigDecimal.class});
            m.invoke(priceData, price);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    private ProductCategoryVO findProductCategoryVO(ProductType productType, Category category) {
        SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
        query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, productType);
        query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, category);
        return query.find();
    }


    private void handle(APIGetProductCategoryListMsg msg) {
        SimpleQuery<ProductCategoryVO> q = dbf.createQuery(ProductCategoryVO.class);
        q.groupBy(ProductCategoryVO_.productTypeCode);
        List<ProductCategoryVO> adVO = q.list();
        List<ProductDataDictionary> productTypes = new ArrayList<>();
        for (ProductCategoryVO eo : adVO) {
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
        bus.reply(msg, reply);
    }

    private void handle(APIUpdateProductPriceUnitMsg msg) {
        ProductPriceUnitVO vo = dbf.findByUuid(msg.getUuid(), ProductPriceUnitVO.class);
        if (msg.getUnitPrice() > 0) {
            vo.setUnitPrice(msg.getUnitPrice());
        }

        if (msg.getUnitPrice() == 0 && "SHARE".equals(vo.getConfigCode()) && "共享端口".equals(vo.getConfigName())) {
            vo.setUnitPrice(msg.getUnitPrice());
        }

        dbf.updateAndRefresh(vo);

        APIUpdateProductPriceUnitEvent evt = new APIUpdateProductPriceUnitEvent(msg.getId());
        evt.setInventory(ProductPriceUnitInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteProductPriceUnitMsg msg) {
        APIDeleteProductPriceUnitEvent evt = new APIDeleteProductPriceUnitEvent(msg.getId());
        if (!StringUtils.isEmpty(msg.getLineName())) {

            UpdateQuery q = UpdateQuery.New(ProductPriceUnitVO.class);
            q.condAnd(ProductPriceUnitVO_.lineName, SimpleQuery.Op.EQ, msg.getLineName());
            q.condAnd(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, msg.getProductCategoryUuid());
            q.delete();

        } else if (!StringUtils.isEmpty(msg.getUuid())) {
            ProductPriceUnitVO vo = dbf.findByUuid(msg.getUuid(), ProductPriceUnitVO.class);
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

        APICreateECPProductPriceUnitEvent evt = new APICreateECPProductPriceUnitEvent(msg.getId());
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
