package com.syscxp.billing.balance;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.syscxp.billing.AlipayGlobalProperty;
import com.syscxp.billing.header.balance.*;
import com.syscxp.header.account.*;
import com.syscxp.header.billing.ProductCategoryVO;
import com.syscxp.header.billing.ProductCategoryVO_;
import com.syscxp.header.billing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
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
        } else if (msg instanceof APIUpdatePresentMsg) {
            handle((APIUpdatePresentMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIUpdatePresentMsg msg) {

        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        if (vo == null) {
            vo = initAccountBlance(msg.getAccountUuid());
        }

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        int hash = msg.getAccountUuid().hashCode() < 0 ? ~msg.getAccountUuid().hashCode() : msg.getAccountUuid().hashCode();
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (msg.getPresent() != null) {
            vo.setPresentBalance(vo.getPresentBalance().add(msg.getPresent()));
            new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.PRESENT_BILL, msg.getPresent(), BigDecimal.ZERO, dbf.getCurrentSqlTime(), DealType.PROXY_RECHARGE, DealState.SUCCESS, vo.getPresentBalance(), outTradeNO, outTradeNO, msg.getSession().getAccountUuid(),msg.getComment(),null,msg.getSession().getUserUuid());
            dbf.getEntityManager().merge(vo);
            dbf.getEntityManager().flush();
        }

        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        abi.setOutTradeNo(outTradeNO);
        APIUpdatePresentEvent evt = new APIUpdatePresentEvent(msg.getId());
        evt.setInventory(abi);
        bus.publish(evt);

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

        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        APIValidateAccountReply reply =  validAccount(accountDiscountVO.getAccountUuid());
        if(reply.isValidAccount()){
            if(reply.getType() == AccountType.Proxy){
                List<String> customerUuids = getCustomerUuidsFromAccount(accountDiscountVO.getAccountUuid());
                deleteCustomerDiscount(customerUuids, accountDiscountVO.getProductCategoryUuid());
            }
        }

        dbf.getEntityManager().remove(dbf.getEntityManager().merge(accountDiscountVO));
        APIDeleteAccountDiscountEvent event = new APIDeleteAccountDiscountEvent(msg.getId());
        event.setInventory(AccountDiscountInventory.valueOf(accountDiscountVO));
        bus.publish(event);
    }

    @Transactional
    private void deleteCustomerDiscount(List<String> customerUuids, String productCategoryUuid) {
        if (customerUuids != null && customerUuids.size() > 0) {
            for (String customerUuid : customerUuids) {
                AccountDiscountVO customerDiscount = getAccountDiscountVO(customerUuid, productCategoryUuid);
                if (customerDiscount != null) {
                    dbf.getEntityManager().remove(dbf.getEntityManager().merge(customerDiscount));
                }

            }

        }
    }


    @Transactional
    private List<String> getCustomerUuidsFromAccount(String proxyUuid) {
        APIGetAccountUuidListByProxyMsg aMsg = new APIGetAccountUuidListByProxyMsg();
        aMsg.setAccountUuid(proxyUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIGetAccountUuidListByProxyReply replay = (APIGetAccountUuidListByProxyReply) RESTApiDecoder.loads(rsp.getResult());
            return replay.getAccountUuids();
        }
        return null;
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


    @Transactional
    private void handle(APIUpdateAccountDiscountMsg msg) {

        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        APIValidateAccountReply reply =  validAccount(accountDiscountVO.getAccountUuid());
        if(reply.isValidAccount()){
            if(reply.getType() == AccountType.Proxy){
                if (!msg.getSession().getType().equals(AccountType.SystemAdmin)) {
                    throw new IllegalArgumentException("you are have no permit");
                }
                List<String> customerUuids = getCustomerUuidsFromAccount(accountDiscountVO.getAccountUuid());
                updateCustomerDiscount(customerUuids,accountDiscountVO.getProductCategoryUuid(),msg.getDiscount());
            }
        }

        accountDiscountVO.setDiscount(msg.getDiscount());
        dbf.getEntityManager().merge(accountDiscountVO);
        dbf.getEntityManager().flush();
        AccountDiscountInventory inventory = AccountDiscountInventory.valueOf(accountDiscountVO);
        APIUpdateAccountDiscountEvent evt = new APIUpdateAccountDiscountEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

    @Transactional
    private void updateCustomerDiscount(List<String> customerUuids,String productCategoryUuid,int discount) {
        for (String accountUuid : customerUuids) {
            AccountDiscountVO accountDiscountVO = getAccountDiscountVO(accountUuid,productCategoryUuid);
            if (accountDiscountVO != null) {
                if (accountDiscountVO.getDiscount() <discount) {
                    accountDiscountVO.setDiscount(discount);
                    dbf.getEntityManager().merge(accountDiscountVO);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private AccountBalanceVO initAccountBlance(String accountUuid) {

        APIValidateAccountReply reply = validAccount(accountUuid);
        if (reply == null) {
            throw new IllegalArgumentException(" the network must be loss");
        }
        if(!reply.isValidAccount()){
            throw new IllegalArgumentException("the account uuid is not valid");
        }
        AccountBalanceVO accountBalanceVO = new AccountBalanceVO();
        accountBalanceVO.setUuid(accountUuid);
        accountBalanceVO.setCreditPoint(BigDecimal.ZERO);
        accountBalanceVO.setPresentBalance(BigDecimal.ZERO);
        accountBalanceVO.setCashBalance(BigDecimal.ZERO);
        dbf.getEntityManager().persist(accountBalanceVO);
        return accountBalanceVO;
    }

    @Transactional
    private APIValidateAccountReply validAccount(String accountUuid) {
        APIValidateAccountMsg aMsg = new APIValidateAccountMsg();
        aMsg.setUuid(accountUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            try {
                APIValidateAccountReply reply = (APIValidateAccountReply) RESTApiDecoder.loads(rsp.getResult());
                return reply;
            } catch (Exception e){
                logger.error(e.getMessage());
            }

        }
        return null;
    }

    private void handle(APIGetExpenseGrossMonthMsg msg) {

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate start = LocalDate.parse(msg.getDateStart());
        LocalDate end = LocalDate.parse(msg.getDateEnd());
        List<ExpenseGross> list = new ArrayList<>();
        long duration = ChronoUnit.MONTHS.between(start, end);
        for (int i = 0; i <= duration; i++) {
            ExpenseGross e = new ExpenseGross();
            e.setMon(start.format(f));
            list.add(e);
            start = start.plusMonths(1);
        }

        String sql = "select DATE_FORMAT(createDate,'%Y-%m') mon,sum(expend) as payTotal from DealDetailVO where accountUuid = :accountUuid and state = 'SUCCESS' and DATE_FORMAT(createDate,'%Y-%m-%d  %T') between :dateStart and :dateEnd and type = :type group by mon order by mon asc";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", msg.getSession().getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        q.setParameter("type", DealType.DEDUCTION.toString());
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
            vo = initAccountBlance(msg.getAccountUuid());
        }

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        int hash = msg.getAccountUuid().hashCode() < 0 ? ~msg.getAccountUuid().hashCode() : msg.getAccountUuid().hashCode();
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (msg.getPresent() != null) {
            vo.setPresentBalance(vo.getPresentBalance().add(msg.getPresent()));
            new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.PRESENT_BILL, msg.getPresent(), BigDecimal.ZERO, dbf.getCurrentSqlTime(), DealType.PROXY_RECHARGE, DealState.SUCCESS, vo.getPresentBalance(), outTradeNO, outTradeNO, msg.getSession().getAccountUuid(),msg.getComment(),null,msg.getSession().getUserUuid());
        } else if (msg.getCash() != null) {
            vo.setCashBalance(vo.getCashBalance().add(msg.getCash()));
            new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.CASH_BILL, msg.getCash(), BigDecimal.ZERO, dbf.getCurrentSqlTime(), DealType.PROXY_RECHARGE, DealState.SUCCESS, vo.getCashBalance(), outTradeNO, outTradeNO, msg.getSession().getAccountUuid(),msg.getComment(),null,msg.getSession().getUserUuid());
        } else if (msg.getCredit() != null) {
            if(vo.getCashBalance().compareTo(BigDecimal.ZERO)<0 && vo.getCashBalance().add(msg.getCredit()).compareTo(BigDecimal.ZERO)<0){
                throw new IllegalArgumentException(" the credit point value can not  less than the repay value");
            }
            vo.setCreditPoint(msg.getCredit());
        }
        dbf.getEntityManager().merge(vo);
        dbf.getEntityManager().flush();
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        abi.setOutTradeNo(outTradeNO);
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
                     vo = initAccountBlance(msg.getAccountUuid());
                }
            }
            accountUuid = msg.getAccountUuid();
        }
        BigDecimal total = msg.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP);
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        int hash = accountUuid.hashCode() < 0 ? ~accountUuid.hashCode() : accountUuid.hashCode();
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        AccountBalanceVO accountBalanceVO = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.CASH_BILL, total, BigDecimal.ZERO, currentTimestamp, DealType.RECHARGE, DealState.FAILURE, accountBalanceVO.getCashBalance(), outTradeNO, outTradeNO, msg.getSession().getAccountUuid(),null,null,msg.getSession().getUserUuid());

        AlipayClient alipayClient = new DefaultAlipayClient(AlipayGlobalProperty.ALIPAY_GATEWAYURL, AlipayGlobalProperty.ALIPAY_APP_ID, AlipayGlobalProperty.ALIPAY_MERCHANT_PRIVATE_KEY, "json", AlipayGlobalProperty.ALIPAY_CHARSET, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.ALIPAY_SIGN_TYPE);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayGlobalProperty.ALIPAY_RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayGlobalProperty.ALIPAY_NOTIFY_URL);
        Map<String, String> param = new HashMap<>();
        param.put("out_trade_no", outTradeNO);
        param.put("total_amount", total.toString());
        param.put("subject", "syscloud");
        param.put("body", "syscloud");
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
            signVerified = AlipaySignature.rsaCheckV1(param, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.ALIPAY_CHARSET, AlipayGlobalProperty.ALIPAY_SIGN_TYPE); //调用SDK验证签名
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

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(AlipayGlobalProperty.ALIPAY_SELLER_ID) || !app_id.equals(AlipayGlobalProperty.ALIPAY_APP_ID)) {
                reply.setInventory(false);
                logger.info("通知参数验证失败");
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
                    dbf.getEntityManager().flush();
                }

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
            }


        }else {
            logger.info("支付宝通知验证失败");
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
            signVerified = AlipaySignature.rsaCheckV1(param, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.ALIPAY_CHARSET, AlipayGlobalProperty.ALIPAY_SIGN_TYPE); //调用SDK验证签名
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

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(AlipayGlobalProperty.ALIPAY_SELLER_ID) || !app_id.equals(AlipayGlobalProperty.ALIPAY_APP_ID)) {
                reply.setInventory(false);
                bus.reply(msg, reply);
                return;
            } else if (dealDetailVO.getOutTradeNO().equals(out_trade_no)) {
                if (dealDetailVO.getState() != DealState.SUCCESS) {
                    AccountBalanceVO vo = dbf.findByUuid(dealDetailVO.getAccountUuid(), AccountBalanceVO.class);
                    BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                    vo.setCashBalance(balance);
                    dbf.getEntityManager().merge(vo);

                    dealDetailVO.setBalance(balance == null ? BigDecimal.ZERO : balance);
                    dealDetailVO.setState(DealState.SUCCESS);
                    dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                    dealDetailVO.setTradeNO(trade_no);
                    dbf.getEntityManager().merge(dealDetailVO);
                    dbf.getEntityManager().flush();
                    reply.setAddMoney(new BigDecimal(total_amount));
                }

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
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateAccountDiscountMsg) {
            validate((APICreateAccountDiscountMsg) msg);
        }else if(msg instanceof  APIUpdateAccountDiscountMsg){
            validate((APIUpdateAccountDiscountMsg) msg);
        }else if(msg instanceof  APIDeleteAccountDiscountMsg){
            validate((APIDeleteAccountDiscountMsg) msg);
        }
        return msg;
    }

    private void validate(APIDeleteAccountDiscountMsg msg) {
        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you are not the permit");
        }
        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            if (!isBoundAccountWithProxy(accountDiscountVO.getAccountUuid(), msg.getSession().getAccountUuid())) {
                throw new IllegalArgumentException("you can only delete yourself customers");
            }
        }
        validateSysAdminHasPermit(msg.getSession().getType(), accountDiscountVO.getAccountUuid());
    }

    private void validate(APIUpdateAccountDiscountMsg msg) {
        AccountDiscountVO accountDiscountVO = dbf.findByUuid(msg.getUuid(), AccountDiscountVO.class);
        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you are not the permit");
        }

        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            if (!isBoundAccountWithProxy(accountDiscountVO.getAccountUuid(), msg.getSession().getAccountUuid())) {
                throw new IllegalArgumentException("you can only set yourself customers");
            }
        }


        validateSysAdminHasPermit(msg.getSession().getType(), accountDiscountVO.getAccountUuid());

        validateDiscount(msg.getSession().getType(), msg.getSession().getAccountUuid(), accountDiscountVO.getProductCategoryUuid(),msg.getDiscount());

    }

    public void validateSysAdminHasPermit(AccountType type,String accountUuid) {
        if (type.equals(AccountType.SystemAdmin)) {
            APIValidateAccountReply reply = validAccount(accountUuid);
            if (reply == null) {
                throw new IllegalArgumentException(" the network must be loss");
            }
            if (reply.isHasProxy()) {
                throw new IllegalArgumentException("the account has proxy,proxy can set his customer discount");
            }
        }
    }

    private void validateDiscount(AccountType type,String accountUuid,String productCategoryUuid,int discount) {
        if (type.equals(AccountType.Proxy)) {
            AccountDiscountVO adVO = getAccountDiscountVO(accountUuid, productCategoryUuid);
            if (adVO != null) {
                if (discount <  adVO.getDiscount()) {
                    throw new IllegalArgumentException("cannot give a discount large than self");
                }
            } else {
                if (discount != 100) {
                    throw new IllegalArgumentException("just can be set 100");
                }
            }

        }
    }

    private void validate(APICreateAccountDiscountMsg msg) {

        if (msg.getSession().getType() == AccountType.Normal) {
            throw new IllegalArgumentException("you are not the permit");
        }

        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            if (!isBoundAccountWithProxy(msg.getAccountUuid(), msg.getSession().getAccountUuid())) {
                throw new IllegalArgumentException("you can only set yourself customers");
            }
        }

        ProductCategoryVO productCategoryEO = findProductCategory(msg.getProductType(), msg.getCategory());
        if (productCategoryEO == null) {
            throw new IllegalArgumentException("check the input value");
        }

        if (getAccountDiscountVO(msg.getAccountUuid(), productCategoryEO.getUuid()) != null) {
            throw new IllegalArgumentException("the account has the discount");
        }

        validateDiscount(msg.getSession().getType(), msg.getSession().getAccountUuid(), productCategoryEO.getUuid(),msg.getDiscount());

        validateSysAdminHasPermit(msg.getSession().getType(), msg.getAccountUuid());

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

}
