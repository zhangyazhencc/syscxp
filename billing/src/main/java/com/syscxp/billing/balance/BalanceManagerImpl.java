package com.syscxp.billing.balance;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.syscxp.billing.BillingGlobalProperty;
import com.syscxp.billing.header.balance.*;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.account.*;
import com.syscxp.header.billing.ProductCategoryVO;
import com.syscxp.header.billing.ProductCategoryVO_;
import com.syscxp.header.billing.*;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.IdentityGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alipay.APIVerifyNotifyMsg;
import com.syscxp.header.alipay.APIVerifyNotifyReply;
import com.syscxp.header.alipay.APIVerifyReturnMsg;
import com.syscxp.header.alipay.APIVerifyReturnReply;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class BalanceManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BalanceManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIGetAccountBalanceMsg) {
            handle((APIGetAccountBalanceMsg) msg);
        } else if (msg instanceof APIUpdateAccountBalanceMsg) {
            handle((APIUpdateAccountBalanceMsg) msg);
        } else if (msg instanceof APIGetExpenseGrossMonthMsg) {
            handle((APIGetExpenseGrossMonthMsg) msg);
        } else if (msg instanceof APIUpdateAccountDiscountMsg) {
            handle((APIUpdateAccountDiscountMsg) msg);
        } else if (msg instanceof APIGetProductPriceMsg) {
            handle((APIGetProductPriceMsg) msg);
        } else if (msg instanceof APIGetAccountBalanceListMsg) {
            handle((APIGetAccountBalanceListMsg) msg);
        } else if (msg instanceof APICreateAccountDiscountMsg) {
            handle((APICreateAccountDiscountMsg) msg);
        } else if (msg instanceof APIDeleteAccountDiscountMsg) {
            handle((APIDeleteAccountDiscountMsg) msg);
        } else if (msg instanceof APIGetAccountDiscountCategoryMsg) {
            handle((APIGetAccountDiscountCategoryMsg) msg);
        } else if (msg instanceof APIRechargeMsg) {
            handle((APIRechargeMsg) msg);
        } else if (msg instanceof APIVerifyReturnMsg) {
            handle((APIVerifyReturnMsg) msg);
        } else if (msg instanceof APIVerifyNotifyMsg) {
            handle((APIVerifyNotifyMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetAccountDiscountCategoryMsg msg) {
        SimpleQuery<ProductPriceUnitVO> query = dbf.createQuery(ProductPriceUnitVO.class);
        query.groupBy(ProductPriceUnitVO_.categoryCode);
        List<ProductPriceUnitVO> categories = query.list();
        APIGetAccountDiscountCategoryReply reply = new APIGetAccountDiscountCategoryReply();
        reply.setInventories(ProductPriceUnitInventory.valueOf(categories));
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIDeleteAccountDiscountMsg msg) {
        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you hava not permission");
        }
        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        APIGetAccountUuidListByProxyMsg aMsg = new APIGetAccountUuidListByProxyMsg();
        aMsg.setAccountUuid(accountDiscountVO.getAccountUuid());
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIGetAccountUuidListByProxyReply replay = (APIGetAccountUuidListByProxyReply) RESTApiDecoder.loads(rsp.getResult());
            List<String> customerUuids = replay.getAccountUuidBoundToProxy();
            if (customerUuids != null && customerUuids.size() > 0) {
                for (String id : customerUuids) {
                    SimpleQuery<AccountDiscountVO> query = dbf.createQuery(AccountDiscountVO.class);
                    query.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, id);
                    query.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, accountDiscountVO.getProductCategoryUuid());
                    List<AccountDiscountVO> accountDiscountVOS = query.list();
                    if (accountDiscountVOS != null && accountDiscountVOS.size() > 0) {
                        for (AccountDiscountVO vo : accountDiscountVOS) {
                            dbf.getEntityManager().remove(dbf.getEntityManager().merge(vo));
                        }
                    }
                }

            }
        }
        dbf.getEntityManager().remove(dbf.getEntityManager().merge(accountDiscountVO));
        APIDeleteAccountDiscountEvent event = new APIDeleteAccountDiscountEvent(msg.getId());
        event.setInventory(AccountDiscountInventory.valueOf(accountDiscountVO));
        bus.publish(event);
    }

    private void handle(APICreateAccountDiscountMsg msg) {

        ProductCategoryVO productCategoryEO = findProductCategory(msg.getProductType(), msg.getCategory());

        AccountDiscountVO accountDiscountVO = new AccountDiscountVO();
        accountDiscountVO.setUuid(Platform.getUuid());
        accountDiscountVO.setAccountUuid(msg.getAccountUuid());
        accountDiscountVO.setProductCategoryUuid(productCategoryEO.getUuid());
        accountDiscountVO.setDiscount(msg.getDiscount());
        dbf.persistAndRefresh(accountDiscountVO);
        APICreateAccountDiscountEvent event = new APICreateAccountDiscountEvent(msg.getId());
        AccountDiscountInventory inventory = AccountDiscountInventory.valueOf(accountDiscountVO);
        inventory.setProductType(productCategoryEO.getProductTypeCode().toString());
        inventory.setProductTypeName(productCategoryEO.getProductTypeName());
        inventory.setCategory(productCategoryEO.getCode().toString());
        inventory.setCategoryName(productCategoryEO.getName());
        event.setInventory(inventory);
        bus.publish(event);

    }


    private void handle(APIGetAccountBalanceListMsg msg) {
        List<String> accountUuids = msg.getAccountUuids();
        SimpleQuery<AccountBalanceVO> query = dbf.createQuery(AccountBalanceVO.class);
        query.add(AccountBalanceVO_.uuid, SimpleQuery.Op.IN, accountUuids);
        List<AccountBalanceVO> accountBalanceVOs = query.list();
        List<AccountBalanceInventory> accountBalanceInventories = AccountBalanceInventory.valueOf(accountBalanceVOs);
        APIGetAccountBalanceListReply reply = new APIGetAccountBalanceListReply();
        reply.setInventories(accountBalanceInventories);
        bus.reply(msg, reply);
    }

    private void handle(APIGetProductPriceMsg msg) {
        List<ProductPriceUnit> units = msg.getUnits();
        List<ProductPriceUnitInventory> productPriceUnits = new ArrayList<>();

        AccountBalanceVO accountBalanceVO = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);

        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        for (ProductPriceUnit unit : units) {

            int times = 1;

            if (unit.getProductTypeCode().equals(ProductType.ECP) && unit.getCategoryCode().equals(Category.BANDWIDTH)) {
                String configCode = unit.getConfigCode().replaceAll("\\D", "");
                times = Integer.parseInt(configCode);
                unit.setConfigCode("1M");
            }

            SimpleQuery<ProductCategoryVO> queryEO = dbf.createQuery(ProductCategoryVO.class);
            queryEO.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, unit.getProductTypeCode());
            queryEO.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, unit.getCategoryCode());
            ProductCategoryVO productCategoryEO = queryEO.find();
            if (productCategoryEO == null) {
                throw new IllegalArgumentException(" not found this type product, check it");
            }
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryEO.getUuid());
            q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, unit.getAreaCode());
            q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, unit.getLineCode());
            q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, unit.getConfigCode());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("please check the argurment");
            }
            ProductPriceUnitInventory inventory = ProductPriceUnitInventory.valueOf(productPriceUnitVO);
            SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
            qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryEO.getUuid());
            qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDiscountVO accountDiscountVO = qDiscount.find();
            int discount = 100;
            if (accountDiscountVO != null) {
                discount = accountDiscountVO.getDiscount() == 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice() * times));
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            discountPrice = discountPrice.add(currentDiscount);
            inventory.setDiscount(discount);
            productPriceUnits.add(inventory);
        }

        BigDecimal duration = BigDecimal.valueOf(msg.getDuration());
        if (msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
            duration = duration.multiply(BigDecimal.valueOf(12));
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        originalPrice = originalPrice.multiply(duration);
        discountPrice = discountPrice.multiply(duration);
        boolean payable = discountPrice.compareTo(mayPayTotal) <= 0;


//        AccountBalanceInventory accountBalanceInventory = AccountBalanceInventory.valueOf(accountBalanceVO);
        APIGetProductPriceReply reply = new APIGetProductPriceReply();
//        reply.setAccountBalanceInventory(accountBalanceInventory);
        reply.setProductPriceInventories(productPriceUnits);
        reply.setMayPayTotal(mayPayTotal);
        reply.setOriginalPrice(originalPrice);
        reply.setDiscountPrice(discountPrice);
        reply.setPayable(payable);
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIUpdateAccountDiscountMsg msg) {

        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            SimpleQuery<AccountDiscountVO> q = dbf.createQuery(AccountDiscountVO.class);
            q.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            q.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, accountDiscountVO.getProductCategoryUuid());
            AccountDiscountVO adVO = q.find();
            if (adVO != null) {
                int discount = adVO.getDiscount();
                if (msg.getDiscount() < discount) {
                    throw new IllegalArgumentException("cannot give a discount large than self");
                }
            } else {
                if (msg.getDiscount() != 100) {
                    throw new IllegalArgumentException("just can be set 100");
                }
            }

        }
        if (msg.getSession().getType().equals(AccountType.SystemAdmin)) {
            APIGetAccountUuidListByProxyMsg aMsg = new APIGetAccountUuidListByProxyMsg();
            aMsg.setAccountUuid(accountDiscountVO.getAccountUuid());
            InnerMessageHelper.setMD5(aMsg);
            String gstr = RESTApiDecoder.dump(aMsg);
            RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
            if (rsp.getState().equals(RestAPIState.Done.toString())) {
                APIGetAccountUuidListByProxyReply replay = (APIGetAccountUuidListByProxyReply) RESTApiDecoder.loads(rsp.getResult());
                List<String> customerUuids = replay.getAccountUuidBoundToProxy();
                if (customerUuids != null && customerUuids.size() > 0) {
                    for (String accountUuid : customerUuids) {
                        SimpleQuery<AccountDiscountVO> query = dbf.createQuery(AccountDiscountVO.class);
                        query.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, accountDiscountVO.getAccountUuid());
                        query.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, accountDiscountVO.getProductCategoryUuid());
                        AccountDiscountVO accountDiscountVO1 = query.find();
                        if (accountDiscountVO1 != null) {
                            int discount = accountDiscountVO1.getDiscount();
                            if (discount < msg.getDiscount()) {
                                accountDiscountVO1.setDiscount(msg.getDiscount());
                                dbf.getEntityManager().merge(accountDiscountVO1);
                            }
                        }

                    }
                }
            }
        }
        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you are not permit");
        }

        accountDiscountVO.setDiscount(msg.getDiscount());
        dbf.getEntityManager().merge(accountDiscountVO);
        dbf.getEntityManager().flush();
        AccountDiscountInventory inventory = AccountDiscountInventory.valueOf(accountDiscountVO);
        APIUpdateAccountDiscountEvent evt = new APIUpdateAccountDiscountEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private AccountBalanceVO initAccountBlance(String accountUuid) {
        APIValidateAccountMsg aMsg = new APIValidateAccountMsg();
        aMsg.setUuid(accountUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIValidateAccountReply replay = (APIValidateAccountReply) RESTApiDecoder.loads(rsp.getResult());
            if (!replay.isValidAccount()) {
                throw new IllegalArgumentException("the account uuid is not valid");
            }
        }
        AccountBalanceVO accountBalanceVO = new AccountBalanceVO();
        accountBalanceVO.setUuid(accountUuid);
        accountBalanceVO.setCreditPoint(BigDecimal.ZERO);
        accountBalanceVO.setPresentBalance(BigDecimal.ZERO);
        accountBalanceVO.setCashBalance(BigDecimal.ZERO);
        dbf.getEntityManager().persist(accountBalanceVO);
        return accountBalanceVO;
    }

    private void handle(APIGetExpenseGrossMonthMsg msg) {

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate start = LocalDate.parse(msg.getDateStart());
        LocalDate end = LocalDate.parse(msg.getDateEnd());
        List<ExpenseGross> list = new ArrayList<ExpenseGross>();
        long duration = ChronoUnit.MONTHS.between(start, end);
        for (int i = 0; i <= duration; i++) {
            ExpenseGross e = new ExpenseGross();
            e.setMon(start.format(f));
            list.add(e);
            start = start.plusMonths(1);
        }

        String sql = "select DATE_FORMAT(payTime,'%Y-%m') mon,sum(payPresent)+sum(payCash) as payTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and DATE_FORMAT(payTime,'%Y-%m-%d  %T') between :dateStart and :dateEnd group by mon order by mon asc";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", msg.getSession().getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs = q.getResultList();
        List<ExpenseGross> vos = objs.stream().map(ExpenseGross::new).collect(Collectors.toList());
        for (ExpenseGross e : list) {
            e.setTotal(getValue(e.getMon(), vos));
        }
        APIGetExpenseGrossMonthReply reply = new APIGetExpenseGrossMonthReply();
        reply.setInventories(list);
        bus.reply(msg, reply);
    }

    private BigDecimal getValue(String s, List<ExpenseGross> vos) {
        for (ExpenseGross e : vos) {
            if (s.equals(e.getMon())) {
                return e.getTotal();
            }
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    private void handle(APIUpdateAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        if (vo == null) {
            initAccountBlance(msg.getAccountUuid());
        }

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        int hash = msg.getAccountUuid().hashCode();
        if (hash < 0) {
            hash = ~hash;
        }
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (msg.getPresent() != null) {
            vo.setPresentBalance(vo.getPresentBalance().add(msg.getPresent()));
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getAccountUuid());
            dVO.setDealWay(DealWay.PRESENT_BILL);
            dVO.setIncome(msg.getPresent());
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(dbf.getCurrentSqlTime());
            dVO.setType(DealType.PRESENT);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(vo.getCashBalance() == null ? BigDecimal.ZERO : vo.getCashBalance());
            dVO.setOutTradeNO(outTradeNO);
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
            dVO.setComment(msg.getComment());
            dbf.getEntityManager().persist(dVO);
        } else if (msg.getCash() != null) {
            vo.setCashBalance(vo.getCashBalance().add(msg.getCash()));
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(msg.getCash());
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(dbf.getCurrentSqlTime());
            dVO.setType(DealType.PROXY_RECHARGE);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(vo.getCashBalance());
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
            dVO.setOutTradeNO(msg.getTradeNO());
            dVO.setComment(msg.getComment());

            dbf.getEntityManager().persist(dVO);
        } else if (msg.getCredit() != null) {
            vo.setCreditPoint(msg.getCredit());
        }
        dbf.getEntityManager().merge(vo);
        dbf.getEntityManager().flush();
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        APIUpdateAccountBalanceEvent evt = new APIUpdateAccountBalanceEvent(msg.getId());
        evt.setInventory(abi);
        bus.publish(evt);

    }

    private void handle(APIGetAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        if (vo == null) {
            vo = initAccountBlance(msg.getAccountUuid());
        }
        AccountBalanceInventory inventory = AccountBalanceInventory.valueOf(vo);
        APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIRechargeMsg msg) {
        String accountUuid = msg.getSession().getAccountUuid();
        if (!StringUtils.isEmpty(msg.getAccountUuid())) {
            if (!dbf.isExist(msg.getAccountUuid(), AccountBalanceVO.class)) {
                AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
                if (vo == null) {
                    initAccountBlance(msg.getAccountUuid());
                }
            }
            accountUuid = msg.getAccountUuid();
        }
        BigDecimal total = msg.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        int hash = accountUuid.hashCode();
        if (hash < 0) {
            hash = ~hash;
        }
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        DealDetailVO vo = new DealDetailVO();
        vo.setUuid(Platform.getUuid());
        vo.setOutTradeNO(outTradeNO);
        vo.setState(DealState.FAILURE);
        vo.setType(DealType.RECHARGE);
        vo.setDealWay(DealWay.CASH_BILL);
        vo.setIncome(total == null ? BigDecimal.ZERO : total);
        vo.setExpend(BigDecimal.ZERO);
        AccountBalanceVO accountBalanceVO = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        vo.setBalance(accountBalanceVO.getCashBalance());
        vo.setFinishTime(currentTimestamp);
        vo.setAccountUuid(accountUuid);
        vo.setOpAccountUuid(msg.getSession().getAccountUuid());
        dbf.getEntityManager().persist(vo);
        AlipayClient alipayClient = new DefaultAlipayClient(BillingGlobalProperty.GATEWAYURL, BillingGlobalProperty.APP_ID, BillingGlobalProperty.MERCHANT_PRIVATE_KEY, "json", BillingGlobalProperty.CHARSET, BillingGlobalProperty.ALIPAY_PUBLIC_KEY, BillingGlobalProperty.SIGN_TYPE);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(BillingGlobalProperty.RETURN_URL);
        alipayRequest.setNotifyUrl(BillingGlobalProperty.NOTIFY_URL);
        Map<String, String> param = new HashMap<>();
        param.put("out_trade_no", outTradeNO);
        param.put("total_amount", total.toString());
        param.put("subject", "cloud-special-network");
        param.put("body", "专有网络");
        param.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(JSONObjectUtil.toJsonString(param));
        String result = "FAILURE";
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            logger.error("cannot access alipay");
            throw new RuntimeException("cannot access alipay");
        }
        logger.info(result);
        APIRechargeReply reply = new APIRechargeReply();
        reply.setInventory(result);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIVerifyNotifyMsg msg) {
        Map<String, String> param = msg.getParam();
        APIVerifyNotifyReply reply = new APIVerifyNotifyReply();
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(param, BillingGlobalProperty.ALIPAY_PUBLIC_KEY, BillingGlobalProperty.CHARSET, BillingGlobalProperty.SIGN_TYPE); //调用SDK验证签名
        } catch (AlipayApiException e) {
            logger.error(e.getErrMsg());
            reply.setInventory(false);
        }
        if (signVerified) {

            String out_trade_no = param.get("out_trade_no");
            String trade_no = param.get("trade_no");
            String total_amount = param.get("total_amount");
            String seller_id = param.get("seller_id");
            String app_id = param.get("app_id");
            String trade_status = param.get("trade_status");
            SimpleQuery<DealDetailVO> q = dbf.createQuery(DealDetailVO.class);
            q.add(DealDetailVO_.outTradeNO, SimpleQuery.Op.EQ, out_trade_no);
            q.add(DealDetailVO_.state, SimpleQuery.Op.EQ, DealState.SUCCESS);
            DealDetailVO dealDetailVO = q.find();

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(BillingGlobalProperty.SELLER_ID) || !app_id.equals(BillingGlobalProperty.APP_ID)) {
                reply.setInventory(false);
                bus.reply(msg, reply);
                return;
            }

            if (trade_status.equals("TRADE_FINISHED")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                if (dealDetailVO.getState().equals(DealState.FAILURE) && dealDetailVO.getOutTradeNO().equals(out_trade_no)) {
                    AccountBalanceVO vo = dbf.findByUuid(dealDetailVO.getAccountUuid(), AccountBalanceVO.class);
                    BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                    vo.setCashBalance(balance);
                    dbf.getEntityManager().merge(vo);

                    dealDetailVO.setBalance(balance);
                    dealDetailVO.setState(DealState.SUCCESS);
                    dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                    dealDetailVO.setTradeNO(trade_no);
                    dealDetailVO.setOutTradeNO(out_trade_no);
                    dbf.getEntityManager().merge(dealDetailVO);
                }

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序
                if (dealDetailVO.getState().equals(DealState.FAILURE)) {
                    AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
                    BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                    vo.setCashBalance(balance);
                    dbf.getEntityManager().merge(vo);

                    dealDetailVO.setBalance(balance);
                    dealDetailVO.setState(DealState.SUCCESS);
                    dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                    dealDetailVO.setTradeNO(trade_no);
                    dealDetailVO.setOutTradeNO(out_trade_no);
                    dbf.getEntityManager().merge(dealDetailVO);
                }

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }


        }
        reply.setInventory(signVerified);

        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APIVerifyReturnMsg msg) {
        Map<String, String> param = msg.getParam();
        APIVerifyReturnReply reply = new APIVerifyReturnReply();
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(param, BillingGlobalProperty.ALIPAY_PUBLIC_KEY, BillingGlobalProperty.CHARSET, BillingGlobalProperty.SIGN_TYPE); //调用SDK验证签名
        } catch (AlipayApiException e) {
            logger.error(e.getErrMsg());
            reply.setInventory(false);
        }
        if (signVerified) {

            String out_trade_no = param.get("out_trade_no");
            String trade_no = param.get("trade_no");
            String total_amount = param.get("total_amount");
            String seller_id = param.get("seller_id");
            String app_id = param.get("app_id");
            SimpleQuery<DealDetailVO> q = dbf.createQuery(DealDetailVO.class);
            q.add(DealDetailVO_.outTradeNO, SimpleQuery.Op.EQ, out_trade_no);
            DealDetailVO dealDetailVO = q.find();

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(BillingGlobalProperty.SELLER_ID) || !app_id.equals(BillingGlobalProperty.APP_ID)) {
                reply.setInventory(false);
                bus.reply(msg, reply);
                return;
            } else if (dealDetailVO.getOutTradeNO().equals(out_trade_no)) {
                AccountBalanceVO vo = dbf.findByUuid(dealDetailVO.getAccountUuid(), AccountBalanceVO.class);
                BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                vo.setCashBalance(balance);
                dbf.getEntityManager().merge(vo);

                dealDetailVO.setBalance(balance == null ? BigDecimal.ZERO : balance);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                dealDetailVO.setTradeNO(trade_no);
                dbf.getEntityManager().merge(dealDetailVO);
            }
        }
        reply.setInventory(signVerified);

        bus.reply(msg, reply);
    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_BALANCE);
    }

    @Override
    public boolean start() {
        try {

        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateAccountDiscountMsg) {
            validate((APICreateAccountDiscountMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateAccountDiscountMsg msg) {

        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you are not the permit");
        }

        ProductCategoryVO productCategoryEO = findProductCategory(msg.getProductType(), msg.getCategory());
        if (productCategoryEO == null) {
            throw new IllegalArgumentException("check the input value");
        }

        if (getAccountDiscountVO(msg.getAccountUuid(), productCategoryEO.getUuid()) != null) {
            throw new IllegalArgumentException("the account has the discount");
        }

        if (!isBoundAccountWithProxy(msg.getAccountUuid(), msg.getSession().getAccountUuid())) {
            throw new IllegalArgumentException("you can only set yourself customers");
        }

        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            AccountDiscountVO accountDiscountVO = getAccountDiscountVO(msg.getSession().getAccountUuid(), productCategoryEO.getUuid());
            if (accountDiscountVO != null) {
                if (accountDiscountVO.getDiscount() > msg.getDiscount()) {
                    throw new IllegalArgumentException("the discount must be less than yourself");
                }
            } else {
                if (msg.getDiscount() != 100) {
                    throw new IllegalArgumentException("yourself do not have the discount so your customer must set to 100");
                }
            }
        }

        if (msg.getSession().getType().equals(AccountType.SystemAdmin)) {
            if (validAccount(msg.getAccountUuid()) == AccountType.Normal) {
                throw new IllegalArgumentException("the account has proxy,proxy can set his customer discount");
            }
        }

    }

    private ProductCategoryVO findProductCategory(ProductType type, Category category) {
        SimpleQuery<ProductCategoryVO> queryEO = dbf.createQuery(ProductCategoryVO.class);
        queryEO.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, type);
        queryEO.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, category);
        return queryEO.find();
    }

    private AccountDiscountVO getAccountDiscountVO(String accountUuid, String productCategoryUuid) {
        SimpleQuery<AccountDiscountVO> query = dbf.createQuery(AccountDiscountVO.class);
        query.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryUuid);
        return query.find();
    }

    private boolean isBoundAccountWithProxy(String accountUuid, String proxyUuid) {
        APIValidateAccountWithProxyMsg aMsg = new APIValidateAccountWithProxyMsg();
        aMsg.setAccountUuid(accountUuid);
        aMsg.setProxyUuid(proxyUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gStr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gStr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIValidateAccountWithProxyReply replay = (APIValidateAccountWithProxyReply) RESTApiDecoder.loads(rsp.getResult());
            return replay.isHasRelativeAccountWithProxy();
        }
        return false;
    }

    private AccountType validAccount(String accountUuid) {
        APIValidateAccountMsg aMsg = new APIValidateAccountMsg();
        aMsg.setUuid(accountUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gStr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gStr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIValidateAccountReply replay = (APIValidateAccountReply) RESTApiDecoder.loads(rsp.getResult());
            if (!replay.isValidAccount()) {
                throw new IllegalArgumentException("the account is not valid");
            }
            return replay.getType();
        }

        return null;

    }
}
