package com.syscxp.billing.balance;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.syscxp.billing.AlipayGlobalProperty;
import com.syscxp.billing.header.balance.*;
import com.syscxp.header.billing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.syscxp.billing.header.balance.*;
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
import com.syscxp.header.account.APIValidateAccountMsg;
import com.syscxp.header.account.APIValidateAccountReply;
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

public class BalanceManagerImpl  extends AbstractService implements ApiMessageInterceptor {

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
        }  else if (msg instanceof APIGetExpenseGrossMonthMsg) {
            handle((APIGetExpenseGrossMonthMsg) msg);
        }else if (msg instanceof APIUpdateAccountDischargeMsg) {
            handle((APIUpdateAccountDischargeMsg) msg);
        }  else if (msg instanceof APIGetProductPriceMsg) {
            handle((APIGetProductPriceMsg) msg);
        } else if (msg instanceof APIGetAccountBalanceListMsg) {
            handle((APIGetAccountBalanceListMsg) msg);
        } else if (msg instanceof APICreateAccountDischargeMsg) {
            handle((APICreateAccountDischargeMsg) msg);
        } else if (msg instanceof APIDeleteAccountDischargeMsg) {
            handle((APIDeleteAccountDischargeMsg) msg);
        } else if (msg instanceof APIGetAccountDischargeCategoryMsg) {
            handle((APIGetAccountDischargeCategoryMsg) msg);
        } else if (msg instanceof APIRechargeMsg) {
            handle((APIRechargeMsg) msg);
        } else if (msg instanceof APIVerifyReturnMsg) {
            handle((APIVerifyReturnMsg) msg);
        } else if (msg instanceof APIVerifyNotifyMsg) {
            handle((APIVerifyNotifyMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetAccountDischargeCategoryMsg msg) {
        SimpleQuery<ProductPriceUnitVO> query = dbf.createQuery(ProductPriceUnitVO.class);
        query.groupBy(ProductPriceUnitVO_.category);
        List<ProductPriceUnitVO> categories = query.list();
        APIGetAccountDischargeCategoryReply reply = new APIGetAccountDischargeCategoryReply();
        reply.setInventories(ProductPriceUnitInventory.valueOf(categories));
        bus.reply(msg, reply);

    }

    private void handle(APIDeleteAccountDischargeMsg msg) {
        AccountDischargeVO accountDischargeVO = dbf.findByUuid(msg.getUuid(), AccountDischargeVO.class);
        if (accountDischargeVO != null) {
            dbf.remove(accountDischargeVO);
        }
        APIDeleteAccountDischargeEvent event = new APIDeleteAccountDischargeEvent(msg.getId());
        event.setInventory(AccountDischargeInventory.valueOf(accountDischargeVO));
        bus.publish(event);
    }

    private void handle(APICreateAccountDischargeMsg msg) {
        SimpleQuery<AccountDischargeVO> query = dbf.createQuery(AccountDischargeVO.class);
        query.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, msg.getCategory());
        boolean exists = query.isExists();
        if (exists) {
            throw new IllegalArgumentException("the account has the discharge");
        }
        SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
        q.add(ProductPriceUnitVO_.category, SimpleQuery.Op.EQ,msg.getCategory());
        q.groupBy(ProductPriceUnitVO_.category);
        ProductPriceUnitVO productPriceUnitVO = q.find();

        AccountDischargeVO accountDischargeVO = new AccountDischargeVO();
        accountDischargeVO.setUuid(Platform.getUuid());
        accountDischargeVO.setAccountUuid(msg.getAccountUuid());
        accountDischargeVO.setCategory(msg.getCategory());
        accountDischargeVO.setDisCharge(msg.getDisCharge());
        accountDischargeVO.setCategoryName(productPriceUnitVO.getCategoryName());
        accountDischargeVO.setProductTypeName(productPriceUnitVO.getProductTypeName());
        accountDischargeVO.setProductType(productPriceUnitVO.getProductType());
        dbf.persistAndRefresh(accountDischargeVO);
        APICreateAccountDischargeEvent event = new APICreateAccountDischargeEvent(msg.getId());
        event.setInventory(AccountDischargeInventory.valueOf(accountDischargeVO));
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

        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.category, SimpleQuery.Op.EQ, unit.getCategory());
            q.add(ProductPriceUnitVO_.productType, SimpleQuery.Op.EQ, unit.getProductType());
            q.add(ProductPriceUnitVO_.config, SimpleQuery.Op.EQ, unit.getConfig());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("please check the argurment");
            }
            ProductPriceUnitInventory inventory = ProductPriceUnitInventory.valueOf(productPriceUnitVO);
            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, unit.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, unit.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int discharge = 100;
            if (accountDischargeVO != null) {
                discharge = accountDischargeVO.getDisCharge() == 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
            BigDecimal currentDischarge = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(discharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            dischargePrice = dischargePrice.add(currentDischarge);
            inventory.setDischarge(discharge);
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
        dischargePrice = dischargePrice.multiply(duration);
        boolean payable = dischargePrice.compareTo(mayPayTotal) <= 0;


        AccountBalanceInventory accountBalanceInventory = AccountBalanceInventory.valueOf(accountBalanceVO);
        APIGetProductPriceReply reply = new APIGetProductPriceReply();
        reply.setAccountBalanceInventory(accountBalanceInventory);
        reply.setProductPriceInventories(productPriceUnits);
        reply.setMayPayTotal(mayPayTotal);
        reply.setOriginalPrice(originalPrice);
        reply.setDischargePrice(dischargePrice);
        reply.setPayable(payable);
        bus.reply(msg, reply);
    }

    private void handle(APIUpdateAccountDischargeMsg msg) {
        AccountDischargeVO accountDischargeVO = dbf.findByUuid(msg.getUuid(), AccountDischargeVO.class);
        if(msg.getSession().getType().equals(AccountType.Proxy)){
            SimpleQuery<AccountDischargeVO> q = dbf.createQuery(AccountDischargeVO.class);
            q.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            q.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, accountDischargeVO.getProductType());
            q.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, accountDischargeVO.getCategory());
            AccountDischargeVO adVO = q.find();
            int disCharge = adVO.getDisCharge();
            if(msg.getDischarge()>disCharge){
                throw new IllegalArgumentException("cannot give a discharge large than self");
            }
        }
        if(msg.getSession().getType() == AccountType.Normal){
            throw new IllegalArgumentException("you are not permit");
        }

        accountDischargeVO.setDisCharge(msg.getDischarge());
        dbf.updateAndRefresh(accountDischargeVO);
        AccountDischargeInventory inventory = AccountDischargeInventory.valueOf(accountDischargeVO);
        APIUpdateAccountDischargeEvent evt = new APIUpdateAccountDischargeEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

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
        return  dbf.persistAndRefresh(accountBalanceVO);
    }

    private void handle(APIGetExpenseGrossMonthMsg msg) {

        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate start = LocalDate.parse(msg.getDateStart());
        LocalDate end = LocalDate.parse(msg.getDateEnd());
        List<ExpenseGross> list = new ArrayList<ExpenseGross>();
        long duration  = ChronoUnit.MONTHS.between(start,end);
        for(int i= 0; i<=duration;i++){
            ExpenseGross e = new ExpenseGross();
            e.setMon(start.format(f));
            list.add(e);
            start =  start.plusMonths(1);
        }

        String sql = "select DATE_FORMAT(payTime,'%Y-%m') mon,sum(payPresent)+sum(payCash) as payTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and DATE_FORMAT(payTime,'%Y-%m-%d  %T') between :dateStart and :dateEnd group by mon order by mon asc";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", msg.getSession().getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs = q.getResultList();
        List<ExpenseGross> vos = objs.stream().map(ExpenseGross::new).collect(Collectors.toList());
        for(ExpenseGross e: list){
            e.setTotal(getValue(e.getMon(),vos));
        }
        APIGetExpenseGrossMonthReply reply = new APIGetExpenseGrossMonthReply();
        reply.setInventories(list);
        bus.reply(msg, reply);
    }

    private BigDecimal getValue(String s,List<ExpenseGross> vos){
        for(ExpenseGross e : vos){
            if(s.equals(e.getMon())){
                return e.getTotal();
            }
        }
        return BigDecimal.ZERO;
    }

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
            dVO.setBalance(vo.getCashBalance()==null?BigDecimal.ZERO:vo.getCashBalance());
            dVO.setOutTradeNO(outTradeNO);
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
            dVO.setComment(msg.getComment());
            dbf.persist(dVO);
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

            dbf.persist(dVO);
        } else if (msg.getCredit() != null) {
            vo.setCreditPoint(msg.getCredit());
        }
        vo = dbf.updateAndRefresh(vo);
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        APIUpdateAccountBalanceEvent evt = new APIUpdateAccountBalanceEvent(msg.getId());
        evt.setInventory(abi);
        bus.publish(evt);

    }

    private void handle(APIGetAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
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
                if(vo == null){
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
        vo.setIncome(total==null?BigDecimal.ZERO:total);
        vo.setExpend(BigDecimal.ZERO);
        AccountBalanceVO accountBalanceVO = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        vo.setBalance(accountBalanceVO.getCashBalance());
        vo.setFinishTime(currentTimestamp);
        vo.setAccountUuid(accountUuid);
        vo.setOpAccountUuid(msg.getSession().getAccountUuid());
        dbf.persistAndRefresh(vo);
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayGlobalProperty.GATEWAYURL, AlipayGlobalProperty.APP_ID, AlipayGlobalProperty.MERCHANT_PRIVATE_KEY, "json", AlipayGlobalProperty.CHARSET, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.SIGN_TYPE);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayGlobalProperty.RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayGlobalProperty.NOTIFY_URL);
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

    private void handle(APIVerifyNotifyMsg msg) {
        Map<String, String> param = msg.getParam();
        APIVerifyNotifyReply reply = new APIVerifyNotifyReply();
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(param, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.CHARSET, AlipayGlobalProperty.SIGN_TYPE); //调用SDK验证签名
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

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(AlipayGlobalProperty.SELLER_ID) || !app_id.equals(AlipayGlobalProperty.APP_ID)) {
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
                    dbf.updateAndRefresh(vo);

                    dealDetailVO.setBalance(balance);
                    dealDetailVO.setState(DealState.SUCCESS);
                    dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                    dealDetailVO.setTradeNO(trade_no);
                    dealDetailVO.setOutTradeNO(out_trade_no);
                    dbf.updateAndRefresh(dealDetailVO);
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
                    dbf.updateAndRefresh(vo);

                    dealDetailVO.setBalance(balance);
                    dealDetailVO.setState(DealState.SUCCESS);
                    dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                    dealDetailVO.setTradeNO(trade_no);
                    dealDetailVO.setOutTradeNO(out_trade_no);
                    dbf.updateAndRefresh(dealDetailVO);
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
            signVerified = AlipaySignature.rsaCheckV1(param, AlipayGlobalProperty.ALIPAY_PUBLIC_KEY, AlipayGlobalProperty.CHARSET, AlipayGlobalProperty.SIGN_TYPE); //调用SDK验证签名
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

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(AlipayGlobalProperty.SELLER_ID) || !app_id.equals(AlipayGlobalProperty.APP_ID)) {
                reply.setInventory(false);
                bus.reply(msg, reply);
                return;
            } else if (dealDetailVO.getOutTradeNO().equals(out_trade_no)) {
                AccountBalanceVO vo = dbf.findByUuid(dealDetailVO.getAccountUuid(), AccountBalanceVO.class);
                BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                vo.setCashBalance(balance);
                dbf.updateAndRefresh(vo);

                dealDetailVO.setBalance(balance==null?BigDecimal.ZERO:balance);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                dealDetailVO.setTradeNO(trade_no);
                dbf.updateAndRefresh(dealDetailVO);
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
        return msg;
    }
}