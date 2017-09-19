package org.zstack.billing.manage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zstack.billing.header.balance.*;
import org.zstack.billing.header.bill.*;
import org.zstack.billing.header.order.*;
import org.zstack.billing.header.receipt.*;
import org.zstack.billing.header.renew.*;
import org.zstack.billing.header.sla.*;
import org.zstack.core.identity.IdentityGlobalProperty;
import org.zstack.core.identity.InnerMessageHelper;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.header.account.APIValidateAccountMsg;
import org.zstack.header.account.APIValidateAccountReply;
import org.zstack.header.alipay.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class BillingManagerImpl extends AbstractService implements BillingManager, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BillingManagerImpl.class);

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
        } else if (msg instanceof APICreateOrderMsg) {
            handle((APICreateOrderMsg) msg);
        } else if (msg instanceof APIGetExpenseGrossMonthListMsg) {
            handle((APIGetExpenseGrossMonthListMsg) msg);
        } else if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        } else if (msg instanceof APIGetValuebleReceiptMsg) {
            handle((APIGetValuebleReceiptMsg) msg);
        } else if (msg instanceof APICreateReceiptPostAddressMsg) {
            handle((APICreateReceiptPostAddressMsg) msg);
        } else if (msg instanceof APIUpdateReceiptPostAddressMsg) {
            handle((APIUpdateReceiptPostAddressMsg) msg);
        } else if (msg instanceof APIDeleteReceiptPostAddressMsg) {
            handle((APIDeleteReceiptPostAddressMsg) msg);
        } else if (msg instanceof APICreateReceiptInfoMsg) {
            handle((APICreateReceiptInfoMsg) msg);
        } else if (msg instanceof APIUpdateReceiptInfoMsg) {
            handle((APIUpdateReceiptInfoMsg) msg);
        } else if (msg instanceof APIDeleteReceiptInfoMsg) {
            handle((APIDeleteReceiptInfoMsg) msg);
        } else if (msg instanceof APICreateSLACompensateMsg) {
            handle((APICreateSLACompensateMsg) msg);
        } else if (msg instanceof APIUpdateSLACompensateMsg) {
            handle((APIUpdateSLACompensateMsg) msg);
        } else if (msg instanceof APIGetBillMsg) {
            handle((APIGetBillMsg) msg);
        } else if (msg instanceof APICreateReceiptMsg) {
            handle((APICreateReceiptMsg) msg);
        } else if (msg instanceof APIUpdateReceiptMsg) {
            handle((APIUpdateReceiptMsg) msg);
        } else if (msg instanceof APIRechargeMsg) {
            handle((APIRechargeMsg) msg);
        } else if (msg instanceof APIVerifyReturnMsg) {
            handle((APIVerifyReturnMsg) msg);
        } else if (msg instanceof APIVerifyNotifyMsg) {
            handle((APIVerifyNotifyMsg) msg);
        } else if (msg instanceof APIUpdateAccountDischargeMsg) {
            handle((APIUpdateAccountDischargeMsg) msg);
        } else if (msg instanceof APIDeleteSLACompensateMsg) {
            handle((APIDeleteSLACompensateMsg) msg);
        } else if (msg instanceof APIGetProductPriceMsg) {
            handle((APIGetProductPriceMsg) msg);
        } else if (msg instanceof APIGetAccountBalanceListMsg) {
            handle((APIGetAccountBalanceListMsg) msg);
        }  else if (msg instanceof APIUpdateOrderExpiredTimeMsg) {
            handle((APIUpdateOrderExpiredTimeMsg) msg);
        }   else if (msg instanceof APICreateAccountDischargeMsg) {
            handle((APICreateAccountDischargeMsg) msg);
        }   else if (msg instanceof APIDeleteAccountDischargeMsg) {
            handle((APIDeleteAccountDischargeMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
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
        query.add(AccountDischargeVO_.accountUuid, Op.EQ, msg.getAccountUuid());
        query.add(AccountDischargeVO_.productType, Op.EQ, msg.getProductType());
        query.add(AccountDischargeVO_.category, Op.EQ, msg.getCategory());
        boolean exists = query.isExists();
        if(exists){
            throw new IllegalArgumentException("the account has the discharge");
        }
        AccountDischargeVO accountDischargeVO = new AccountDischargeVO();
        accountDischargeVO.setUuid(Platform.getUuid());
        accountDischargeVO.setAccountUuid(msg.getAccountUuid());
        accountDischargeVO.setProductType(msg.getProductType());
        accountDischargeVO.setCategory(msg.getCategory());
        accountDischargeVO.setDisCharge(msg.getDisCharge());
        dbf.persistAndRefresh(accountDischargeVO);
        APICreateAccountDischargeEvent event = new APICreateAccountDischargeEvent(msg.getId());
        event.setInventory(AccountDischargeInventory.valueOf(accountDischargeVO));
        bus.publish(event);

    }

    private void handle(APIUpdateOrderExpiredTimeMsg msg) {
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.productUuid, Op.EQ, msg.getProductUuid());
        query.add(OrderVO_.productStatus, Op.EQ, 0);
        OrderVO orderVO = query.find();
        if(orderVO == null){
            throw new RuntimeException("cannot find the order");
        }
        orderVO.setProductEffectTimeStart(msg.getStartTime());
        orderVO.setProductEffectTimeEnd(msg.getEndTime());
        orderVO.setProductStatus(1);
        dbf.updateAndRefresh(orderVO);
        APIUpdateOrderExpiredTimeEvent event = new APIUpdateOrderExpiredTimeEvent();
        event.setInventory(OrderInventory.valueOf(orderVO));
        bus.publish(event);
    }

    private void handle(APIGetAccountBalanceListMsg msg) {
        List<String> accountUuids = msg.getAccountUuids();
        SimpleQuery<AccountBalanceVO> query = dbf.createQuery(AccountBalanceVO.class);
        query.add(AccountBalanceVO_.uuid, Op.IN, accountUuids);
        List<AccountBalanceVO> accountBalanceVOs = query.list();
        List<AccountBalanceInventory> accountBalanceInventories = AccountBalanceInventory.valueOf(accountBalanceVOs);
        APIGetAccountBalanceListReply reply = new APIGetAccountBalanceListReply();
        reply.setInventories(accountBalanceInventories);
        bus.reply(msg,reply);
    }

    private void handle(APIGetProductPriceMsg msg) {
        List<ProductPriceUnit> units = msg.getUnits();
        List<ProductPriceUnitInventory> productPriceUnits = new ArrayList<>();
        AccountBalanceVO accountBalanceVO = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.category, Op.EQ, unit.getCategory());
            q.add(ProductPriceUnitVO_.productType, Op.EQ, unit.getProductType());
            q.add(ProductPriceUnitVO_.config, Op.EQ, unit.getConfig());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("please check the argurment");
            }
            ProductPriceUnitInventory inventory = ProductPriceUnitInventory.valueOf(productPriceUnitVO);
            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, Op.EQ, unit.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, Op.EQ, unit.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int discharge = 100;
            if (accountDischargeVO != null) {
                discharge = accountDischargeVO.getDisCharge() == 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            inventory.setDischarge(discharge);
            productPriceUnits.add(inventory);
        }
        AccountBalanceInventory accountBalanceInventory = AccountBalanceInventory.valueOf(accountBalanceVO);
        ProductPriceInventory productPriceInventory = new ProductPriceInventory();
        productPriceInventory.setAccountBalanceInventory(accountBalanceInventory);
        productPriceInventory.setProductPriceInventories(productPriceUnits);
        APIGetProductPriceReply reply = new APIGetProductPriceReply();
        reply.setInventory(productPriceInventory);
        bus.reply(msg, reply);
    }

    private void handle(APIDeleteSLACompensateMsg msg) {
        String uuid = msg.getUuid();
        SLACompensateVO slaCompensateVO = dbf.findByUuid(uuid, SLACompensateVO.class);
        if (slaCompensateVO != null) {
            dbf.remove(slaCompensateVO);
        }
        SLACompensateInventory inventory = SLACompensateInventory.valueOf(slaCompensateVO);
        APIDeleteSLACompensateEvent event = new APIDeleteSLACompensateEvent();
        event.setInventory(inventory);
        bus.publish(event);

    }

    private void handle(APIUpdateAccountDischargeMsg msg) {
        String uuid = msg.getUuid();
        AccountDischargeVO accountDischargeVO = dbf.findByUuid(uuid, AccountDischargeVO.class);
        accountDischargeVO.setDisCharge(msg.getDischarge());
        dbf.updateAndRefresh(accountDischargeVO);
        AccountDischargeInventory inventory = AccountDischargeInventory.valueOf(accountDischargeVO);
        APIUpdateAccountDischargeEvent evt = new APIUpdateAccountDischargeEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
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
            q.add(DealDetailVO_.outTradeNO, Op.EQ, out_trade_no);
            q.add(DealDetailVO_.state, Op.EQ, DealState.SUCCESS);
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
            q.add(DealDetailVO_.outTradeNO, Op.EQ, out_trade_no);
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

                dealDetailVO.setBalance(balance);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                dealDetailVO.setTradeNO(trade_no);
                dbf.updateAndRefresh(dealDetailVO);
            }
        }
        reply.setInventory(signVerified);

        bus.reply(msg, reply);
    }

    private void handle(APIRechargeMsg msg) {
        String accountUuid = msg.getSession().getAccountUuid();
        if (!StringUtils.isEmpty(msg.getAccountUuid())) {
            if (!dbf.isExist(msg.getAccountUuid(), AccountBalanceVO.class)) {
                throw new IllegalArgumentException("could not find the account,please check it");
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
        vo.setIncome(total);
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
        param.put("subject", "cloud special network");
        param.put("body", "description");
        param.put("product_code", "FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizContent(JSONObjectUtil.toJsonString(param));
        String result = "FAILURE";
        try {
            result = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            logger.error("cannot access alipay");
        }
        logger.info(result);
        APIRechargeReply reply = new APIRechargeReply();
        reply.setInventory(result);
        bus.reply(msg, reply);

    }

    private void handle(APIUpdateReceiptMsg msg) {
        String receiptUuid = msg.getUuid();
        ReceiptState state = msg.getState();
        ReceiptVO vo = dbf.findByUuid(receiptUuid, ReceiptVO.class);
        vo.setState(msg.getState());
        if (vo.getState().equals(ReceiptState.REJECT)) {
            vo.setCommet(msg.getReason());
        }
        dbf.updateAndRefresh(vo);
        ReceiptInventory inventory = ReceiptInventory.valueOf(vo);
        APIUpdateReceiptEvent evt = new APIUpdateReceiptEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

    private void handle(APICreateReceiptMsg msg) {
        String accountUuid = msg.getSession().getAccountUuid();
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        BigDecimal total = msg.getTotal();
        BigDecimal consumeCash = getConsumeCashByAccountUuid(accountUuid);
        BigDecimal hadReceiptCash = getHadReceiptCashByAccountUuid(accountUuid);
        AccountBalanceVO vo = dbf.findByUuid(accountUuid, AccountBalanceVO.class);
        BigDecimal valuebleReceipt = consumeCash.subtract(hadReceiptCash);
        BigDecimal hadConsumeCreditPoint = BigDecimal.ZERO;
        if (vo.getCashBalance().compareTo(BigDecimal.ZERO) < 0) {
            hadConsumeCreditPoint = vo.getCashBalance();
            valuebleReceipt = valuebleReceipt.add(hadConsumeCreditPoint);
        }
        if (total.compareTo(valuebleReceipt) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_VALID_VALUE, String.format("create receipt money must not great than %s", valuebleReceipt.toString())));
        }
        ReceiptVO receiptVO = new ReceiptVO();
        receiptVO.setUuid(Platform.getUuid());
        receiptVO.setAccountUuid(accountUuid);
        receiptVO.setApplyTime(currentTimestamp);
        receiptVO.setState(ReceiptState.UNDONE);
        receiptVO.setTotal(total);
        ReceiptPostAddressVO receiptPostAddressVO = dbf.findByUuid(msg.getReceiptAddressUuid(), ReceiptPostAddressVO.class);
        receiptVO.setReceiptPostAddressVO(receiptPostAddressVO);
        String receiptInfoUuid = msg.getReceiptInfoUuid();
        ReceiptInfoVO receiptInfoVO = dbf.findByUuid(receiptInfoUuid, ReceiptInfoVO.class);
        receiptVO.setReceiptInfoVO(receiptInfoVO);
        dbf.persistAndRefresh(receiptVO);
        ReceiptInventory inventory = ReceiptInventory.valueOf(receiptVO);
        APICreateReceiptEvent evt = new APICreateReceiptEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

    private void handle(APIGetBillMsg msg) {
        BillVO vo = dbf.findByUuid(msg.getUuid(), BillVO.class);

        Timestamp billTimestamp = vo.getBillDate();
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(billTimestamp);
        calendar1.set(Calendar.HOUR_OF_DAY, 0);
        calendar1.set(Calendar.MINUTE, 0);
        calendar1.set(Calendar.SECOND, 0);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Timestamp startTime = new Timestamp(calendar1.getTime().getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 0);
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        calendar2.set(Calendar.MILLISECOND, 999);
        Timestamp endTime = new Timestamp(calendar2.getTime().getTime());
        String accountUuid = msg.getSession().getAccountUuid();
        String sql = "select count(*) as categoryCount, sum(payPresent) as payPresentTotal,sum(payCash) as payCashTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime BETWEEN :dateStart and  :dateEnd  group by productType ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", accountUuid);
        q.setParameter("dateStart", startTime);
        q.setParameter("dateEnd", endTime);
        List<Object[]> objs = q.getResultList();
        List<Monetary> bills = objs.stream().map(Monetary::new).collect(Collectors.toList());
        BillInventory inventory = BillInventory.valueOf(vo);
        inventory.setBills(bills);
        APIGetBillReply reply = new APIGetBillReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    private void handle(APIUpdateSLACompensateMsg msg) {
        SLACompensateVO vo = dbf.findByUuid(msg.getUuid(), SLACompensateVO.class);
        if (msg.getAccountUuid() != null) {
            vo.setAccountUuid(msg.getAccountUuid());
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
        }
        if (msg.getDuration() != null) {
            vo.setDuration(msg.getDuration());
        }
        if (msg.getProductName() != null) {
            vo.setProductName(msg.getProductName());
        }
        if (msg.getProductType() != null) {
            vo.setProductType(msg.getProductType());
        }
        if (msg.getReason() != null) {
            vo.setReason(msg.getReason());
        }
        if (msg.getTimeStart() != null) {
            vo.setTimeStart(msg.getTimeStart());
        }
        if (msg.getTimeEnd() != null) {
            vo.setTimeEnd(msg.getTimeEnd());
        }
        if (msg.getProductUuid() != null) {
            vo.setProductUuid(msg.getProductUuid());
        }
        if (msg.getState() != null) {
            vo.setState(msg.getState());
        }

        dbf.updateAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APIUpdateSLACompensateEvent evt = new APIUpdateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APICreateSLACompensateMsg msg) {
        SLACompensateVO vo = new SLACompensateVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setDescription(msg.getDescription());
        vo.setDuration(msg.getDuration());
        vo.setProductUuid(msg.getProductUuid());
        vo.setProductName(msg.getProductName());
        vo.setProductType(msg.getProductType());
        vo.setReason(msg.getReason());
        vo.setState(SLAState.NOT_APPLY);

        dbf.persistAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APICreateSLACompensateEvent evt = new APICreateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIDeleteReceiptInfoMsg msg) {
        String uuid = msg.getUuid();
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(), ReceiptInfoVO.class);
        if (vo != null) {
            dbf.remove(vo);
        }
        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APIDeleteReceiptInfoEvent evt = new APIDeleteReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptInfoMsg msg) {
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(), ReceiptInfoVO.class);
        if (msg.getAddress() != null) {
            vo.setAddress(msg.getAddress());
        }
        if (msg.getBankAccountNumber() != null) {
            vo.setBankAccountNumber(msg.getBankAccountNumber());
        }
        if (msg.getBankName() != null) {
            vo.setBankName(msg.getBankName());
        }
        if (msg.getIdentifyNumber() != null) {
            vo.setIdentifyNumber(msg.getIdentifyNumber());
        }
        if (msg.getTelephone() != null) {
            vo.setTelephone(msg.getTelephone());
        }
        if (msg.getTitle() != null) {
            vo.setTitle(msg.getTitle());
        }
        if (msg.getType() != null) {
            vo.setType(msg.getType());
        }
        if (vo.isDefault() != msg.isDefault()) {
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptInfoVO> q = dbf.createQuery(ReceiptInfoVO.class);
            q.add(ReceiptInfoVO_.accountUuid, Op.EQ, vo.getAccountUuid());
            List<ReceiptInfoVO> ids = q.list();
            for (ReceiptInfoVO riVO : ids) {
                if (riVO.getUuid().equals(msg.getUuid())) {
                    continue;
                }
                if (riVO.isDefault()) {
                    riVO.setDefault(false);
                    dbf.updateAndRefresh(riVO);
                }
            }

        }
        dbf.updateAndRefresh(vo);
        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APIUpdateReceiptInfoEvent evt = new APIUpdateReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APICreateReceiptInfoMsg msg) {
        ReceiptInfoVO vo = new ReceiptInfoVO();
        vo.setUuid(Platform.getUuid());
        vo.setTitle(msg.getTitle());
        vo.setType(msg.getType());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        if (msg.getType().equals(ReceiptType.VAT_SPECIAL_RECEIPT)) {
            vo.setAddress(msg.getAddress());
            vo.setBankAccountNumber(msg.getBankAccountNumber());
            vo.setBankName(msg.getBankName());
            vo.setIdentifyNumber(msg.getIdentifyNumber());
            vo.setComment(msg.getComment());
            vo.setTelephone(msg.getTelephone());
        }

        dbf.persistAndRefresh(vo);

        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APICreateReceiptInfoEvent evt = new APICreateReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIDeleteReceiptPostAddressMsg msg) {
        String uuid = msg.getUuid();
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(), ReceiptPostAddressVO.class);
        if (vo != null) {
            dbf.removeByPrimaryKey(uuid, ReceiptPostAddressVO.class);
        }
        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APIDeleteReceiptPostAddressEvent evt = new APIDeleteReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptPostAddressMsg msg) {
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(), ReceiptPostAddressVO.class);
        if (msg.getName() != null) {
            vo.setName(msg.getName());
        }
        if (msg.getTelephone() != null) {
            vo.setTelephone(msg.getTelephone());
        }
        if (msg.getAddress() != null) {
            vo.setAddress(msg.getAddress());
        }
        if (vo.isDefault() != msg.isDefault()) {
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptPostAddressVO> q = dbf.createQuery(ReceiptPostAddressVO.class);
            q.add(ReceiptPostAddressVO_.accountUuid, Op.EQ, vo.getAccountUuid());
            List<ReceiptPostAddressVO> all = q.list();
            for (ReceiptPostAddressVO receiptPostAddressVO : all) {
                if (receiptPostAddressVO.getUuid().equals(msg.getUuid())) {
                    continue;
                }
                ReceiptPostAddressVO v = dbf.findByUuid(receiptPostAddressVO.getUuid(), ReceiptPostAddressVO.class);
                if (v.isDefault()) {
                    v.setDefault(false);
                    dbf.updateAndRefresh(v);
                }
            }

        }
        dbf.updateAndRefresh(vo);
        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APIUpdateReceiptPostAddressEvent evt = new APIUpdateReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APICreateReceiptPostAddressMsg msg) {
        ReceiptPostAddressVO vo = new ReceiptPostAddressVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        vo.setAddress(msg.getAddress());
        vo.setName(msg.getName());
        vo.setTelephone(msg.getTelephone());
        dbf.persistAndRefresh(vo);

        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APICreateReceiptPostAddressEvent evt = new APICreateReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APIGetValuebleReceiptMsg msg) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        BigDecimal consumeCash = getConsumeCashByAccountUuid(currentAccountUuid);
        BigDecimal hadReceiptCash = getHadReceiptCashByAccountUuid(currentAccountUuid);
        AccountBalanceVO vo = dbf.findByUuid(currentAccountUuid, AccountBalanceVO.class);
        BigDecimal valuebleReceipt = consumeCash.subtract(hadReceiptCash);
        BigDecimal hadConsumeCreditPoint = BigDecimal.ZERO;
        if (vo.getCashBalance().compareTo(BigDecimal.ZERO) < 0) {
            hadConsumeCreditPoint = vo.getCashBalance();
            valuebleReceipt = valuebleReceipt.add(hadConsumeCreditPoint);
        }
        APIGetValuebleReceiptReply reply = new APIGetValuebleReceiptReply();
        reply.setValuebleReceipt(valuebleReceipt);
        reply.setConsumeCash(consumeCash);
        reply.setHadConsumeCreditPoint(hadConsumeCreditPoint);
        reply.setHadReceiptCash(hadReceiptCash);
        bus.reply(msg, reply);

    }

    @Transactional(readOnly = true)
    BigDecimal getConsumeCashByAccountUuid(String accountUuid) {
        String sql = "select sum(vo.payCash)" + " from OrderVO vo " + " where vo.accountUuid = :accountUuid";
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal hadReceiptCash = vq.getSingleResult();
        hadReceiptCash = hadReceiptCash == null ? BigDecimal.ZERO : hadReceiptCash;
        return hadReceiptCash;
    }

    @Transactional(readOnly = true)
    BigDecimal getHadReceiptCashByAccountUuid(String accountUuid) {
        String sql = "select sum(vo.total)" + " from ReceiptVO vo " + " where vo.accountUuid = :accountUuid ";
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal consumeCash = vq.getSingleResult();
        consumeCash = consumeCash == null ? BigDecimal.ZERO : consumeCash;
        return consumeCash;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void payMethod(APIMessage msg, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {
        int hash = msg.getSession().getAccountUuid().hashCode();
        if (hash < 0) {
            hash = ~hash;
        }
        String outTradeNO = currentTimeStamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(msg.getSession().getAccountUuid());
                dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(total.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(presentNow);
                dealDetailVO.setOutTradeNO(outTradeNO);
                dealDetailVO.setOpAccountUuid(msg.getSession().getAccountUuid());
                dbf.getEntityManager().persist(dealDetailVO);

            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                abvo.setCashBalance(remainCash);
                abvo.setPresentBalance(BigDecimal.ZERO);
                orderVo.setPayPresent(payPresent);

                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(msg.getSession().getAccountUuid());
                dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(payPresent.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(BigDecimal.ZERO);
                dealDetailVO.setOutTradeNO(outTradeNO + "-1");
                dealDetailVO.setOpAccountUuid(msg.getSession().getAccountUuid());
                dbf.getEntityManager().persist(dealDetailVO);

                orderVo.setPayCash(payCash);

                DealDetailVO dVO = new DealDetailVO();
                dVO.setUuid(Platform.getUuid());
                dVO.setAccountUuid(msg.getSession().getAccountUuid());
                dVO.setDealWay(DealWay.CASH_BILL);
                dVO.setIncome(BigDecimal.ZERO);
                dVO.setExpend(payCash.negate());
                dVO.setFinishTime(currentTimeStamp);
                dVO.setType(DealType.DEDUCTION);
                dVO.setState(DealState.SUCCESS);
                dVO.setBalance(remainCash);
                dVO.setOutTradeNO(outTradeNO + "-2");
                dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
                dbf.getEntityManager().persist(dVO);
            }
        } else {
            BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
            abvo.setCashBalance(remainCashBalance);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setPayCash(total);

            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getSession().getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(BigDecimal.ZERO);
            dVO.setExpend(total.negate());
            dVO.setFinishTime(currentTimeStamp);
            dVO.setType(DealType.DEDUCTION);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCashBalance);
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
            dVO.setOutTradeNO(outTradeNO);
            dbf.getEntityManager().persist(dVO);
        }
    }

    private void handle(APIUpdateRenewMsg msg) {
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        if (vo.isRenewAuto() != msg.isRenewAuto()) {
            vo.setRenewAuto(msg.isRenewAuto());
        }
        dbf.updateAndRefresh(vo);
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewEvent evt = new APIUpdateRenewEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APIGetExpenseGrossMonthListMsg msg) {
        String sql = "select DATE_FORMAT(payTime,'%Y-%m') mon,sum(payPresent)+sum(payCash) as payTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime between :dateStart and :dateEnd group by mon order by mon asc";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", msg.getSession().getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs = q.getResultList();
        List<ExpenseGross> vos = objs.stream().map(ExpenseGross::new).collect(Collectors.toList());
        APIGetExpenseGrossMonthListReply reply = new APIGetExpenseGrossMonthListReply();
        reply.setInventories(vos);
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<String> productPriceUnitUuids = msg.getProductPriceUnitUuids();
        for (String productPriceUnitUuid : productPriceUnitUuids) {
            ProductPriceUnitVO productPriceUnitVO = dbf.findByUuid(productPriceUnitUuid, ProductPriceUnitVO.class);
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, Op.EQ, productPriceUnitVO.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, Op.EQ, productPriceUnitVO.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int productDisCharge = 100;
            if (accountDischargeVO != null) {
                productDisCharge = accountDischargeVO.getDisCharge() <= 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
            BigDecimal currentDischarge = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(productDisCharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            dischargePrice = dischargePrice.add(currentDischarge);

        }

        BigDecimal duration = BigDecimal.valueOf(msg.getDuration());
        if (msg.getType() == OrderType.BUY || msg.getType() == OrderType.RENEW) {
            if (msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
                duration = duration.multiply(BigDecimal.valueOf(12));
            }
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        OrderVO orderVo = new OrderVO();

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getSession().getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setProductChargeModel(msg.getProductChargeModel());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDuration(msg.getDuration());

        if (msg.getType() == OrderType.MODIFY || msg.getType() == OrderType.UN_SUBCRIBE) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, -1);
            Timestamp startTime = new Timestamp(c.getTime().getTime());
            c.add(Calendar.MONTH,2);
            Timestamp endTime = new Timestamp(c.getTime().getTime()); long useDays = Math.abs(currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
            long days = Math.abs(endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);

            SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
            query.add(RenewVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
            query.add(RenewVO_.productUuid, Op.EQ, msg.getProductUuid());
            RenewVO renewVO = query.find();
            if (renewVO == null) {
                throw new IllegalArgumentException("could not find the product purchased history ");
            }

            BigDecimal remainMoney = renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays));
            if (msg.getType() == OrderType.MODIFY) {
                BigDecimal needPayMoney = dischargePrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
                BigDecimal needPayOriginMoney = originalPrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
                BigDecimal subMoney = needPayMoney.subtract(remainMoney);
                if (subMoney.compareTo(BigDecimal.ZERO) >= 0) {//upgrade
                    if (subMoney.compareTo(mayPayTotal) > 0) {
                        throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
                    }
                    orderVo.setType(OrderType.UPGRADE);
                    orderVo.setOriginalPrice(needPayOriginMoney.subtract(remainMoney));
                    orderVo.setPrice(subMoney);
                    orderVo.setProductEffectTimeStart(startTime);
                    orderVo.setProductEffectTimeEnd(endTime);
                    payMethod(msg, orderVo, abvo, subMoney, currentTimestamp);

                } else { //downgrade
                    BigDecimal valuePayCash = getValueblePayCash(msg.getSession().getAccountUuid(), msg.getProductUuid());
                    orderVo.setType(OrderType.DOWNGRADE);
                    if (subMoney.compareTo(valuePayCash.negate()) < 0) {
                        subMoney = valuePayCash.negate();
                    }
                    orderVo.setPayCash(subMoney);
                    orderVo.setPayPresent(BigDecimal.ZERO);
                    orderVo.setOriginalPrice(subMoney);
                    orderVo.setPrice(subMoney);
                    orderVo.setProductEffectTimeStart(startTime);
                    orderVo.setProductEffectTimeEnd(endTime);
                    BigDecimal remainCash = abvo.getCashBalance().add(subMoney.negate());
                    abvo.setCashBalance(remainCash);

                    DealDetailVO dVO = new DealDetailVO();
                    dVO.setUuid(Platform.getUuid());
                    dVO.setAccountUuid(msg.getSession().getAccountUuid());
                    dVO.setDealWay(DealWay.CASH_BILL);
                    dVO.setIncome(subMoney.negate());
                    dVO.setExpend(BigDecimal.ZERO);
                    dVO.setFinishTime(currentTimestamp);
                    dVO.setType(DealType.REFUND);
                    dVO.setState(DealState.SUCCESS);
                    dVO.setBalance(remainCash);
                    dVO.setOutTradeNO(orderVo.getUuid());
                    dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
                    dbf.getEntityManager().persist(dVO);
                }
                renewVO.setProductChargeModel(orderVo.getProductChargeModel());
                renewVO.setProductDescription(orderVo.getProductDescription());
                renewVO.setPricePerDay(dischargePrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN));
                dbf.getEntityManager().merge(renewVO);
                SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
                q.add(PriceRefRenewVO_.renewUuid, Op.EQ, renewVO.getUuid());
                List<PriceRefRenewVO> renewVOs = q.list();
                dbf.removeCollection(renewVOs, PriceRefRenewVO.class);
                for (String productPriceUnitUuid : productPriceUnitUuids) {
                    PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
                    priceRefRenewVO.setUuid(Platform.getUuid());
                    priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
                    priceRefRenewVO.setAccountUuid(msg.getSession().getAccountUuid());
                    priceRefRenewVO.setRenewUuid(renewVO.getUuid());
                    dbf.getEntityManager().persist(priceRefRenewVO);
                }
            } else {
                BigDecimal valuePayCash = getValueblePayCash(msg.getSession().getAccountUuid(), msg.getProductUuid());
                orderVo.setType(OrderType.UN_SUBCRIBE);
                if (remainMoney.compareTo(valuePayCash) < 0) {
                    remainMoney = valuePayCash;
                }
                orderVo.setOriginalPrice(remainMoney);
                orderVo.setPrice(remainMoney);
                orderVo.setProductEffectTimeEnd(currentTimestamp);
                orderVo.setProductEffectTimeEnd(startTime);
                BigDecimal remainCash = abvo.getCashBalance().add(remainMoney);
                abvo.setCashBalance(remainCash);
                orderVo.setPayPresent(BigDecimal.ZERO);
                orderVo.setPayCash(remainMoney.negate());

                DealDetailVO dVO = new DealDetailVO();
                dVO.setUuid(Platform.getUuid());
                dVO.setAccountUuid(msg.getSession().getAccountUuid());
                dVO.setDealWay(DealWay.CASH_BILL);
                dVO.setIncome(remainMoney);
                dVO.setExpend(BigDecimal.ZERO);
                dVO.setFinishTime(currentTimestamp);
                dVO.setType(DealType.REFUND);
                dVO.setState(DealState.SUCCESS);
                dVO.setBalance(remainCash);
                dVO.setOutTradeNO(orderVo.getUuid());
                dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
                dbf.getEntityManager().persist(dVO);
                dbf.getEntityManager().remove(renewVO);
                SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
                q.add(PriceRefRenewVO_.renewUuid, Op.EQ, renewVO.getUuid());
                List<PriceRefRenewVO> renewVOs = q.list();
                dbf.removeCollection(renewVOs, PriceRefRenewVO.class);
            }


        } else if (msg.getType().equals(OrderType.BUY)) {
            originalPrice = originalPrice.multiply(duration);
            dischargePrice = dischargePrice.multiply(duration);

            if (dischargePrice.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            orderVo.setOriginalPrice(originalPrice);
            orderVo.setPrice(dischargePrice);
            orderVo.setType(OrderType.BUY);
            if(msg.getProductType() == ProductType.TUNNEL){
                orderVo.setProductStatus(0);
            }
            payMethod(msg, orderVo, abvo, dischargePrice, currentTimestamp);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTimestamp);
            calendar.add(Calendar.MONTH, duration.intValue());
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));

            RenewVO renewVO = new RenewVO();
            renewVO.setUuid(Platform.getUuid());
            renewVO.setProductChargeModel(orderVo.getProductChargeModel());
            renewVO.setProductUuid(orderVo.getProductUuid());
            renewVO.setAccountUuid(orderVo.getAccountUuid());
            renewVO.setProductName(orderVo.getProductName());
            renewVO.setProductType(orderVo.getProductType());
            renewVO.setProductDescription(orderVo.getProductDescription());
            renewVO.setRenewAuto(true);
            renewVO.setPricePerDay(dischargePrice.divide(BigDecimal.valueOf(30).multiply(duration), 4, BigDecimal.ROUND_HALF_EVEN));
            dbf.getEntityManager().persist(renewVO);

            for (String productPriceUnitUuid : productPriceUnitUuids) {
                PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
                priceRefRenewVO.setUuid(Platform.getUuid());
                priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
                priceRefRenewVO.setAccountUuid(msg.getSession().getAccountUuid());
                priceRefRenewVO.setRenewUuid(renewVO.getUuid());
                dbf.getEntityManager().persist(priceRefRenewVO);
            }


        } else if (msg.getType().equals(OrderType.SLA_COMPENSATION)) {
            orderVo.setPayCash(BigDecimal.ZERO);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setType(OrderType.SLA_COMPENSATION);
            orderVo.setOriginalPrice(BigDecimal.ZERO);
            orderVo.setPrice(BigDecimal.ZERO);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTimestamp);
            calendar.add(Calendar.DAY_OF_YEAR, duration.intValue());
            orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));
        } else if (msg.getType().equals(OrderType.RENEW)) {
            originalPrice = originalPrice.multiply(duration);
            dischargePrice = dischargePrice.multiply(duration);
            if (originalPrice.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            payMethod(msg, orderVo, abvo, dischargePrice, currentTimestamp);
            orderVo.setType(OrderType.RENEW);
            orderVo.setOriginalPrice(originalPrice);
            orderVo.setPrice(dischargePrice);
            //todo modify product from tunel
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTimestamp);
            calendar.add(Calendar.MONTH, duration.intValue());
            orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));

            SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
            query.add(RenewVO_.accountUuid, Op.EQ, orderVo.getAccountUuid());
            query.add(RenewVO_.productUuid, Op.EQ, orderVo.getProductUuid());
            RenewVO renewVO = query.find();
            if (renewVO == null) {
                throw new IllegalArgumentException("can not found the product");
            }
            renewVO.setProductChargeModel(orderVo.getProductChargeModel());

            Timestamp startTime = new Timestamp(currentTimestamp.getTime() - 30 * 24 * 60 * 60 * 1000);//todo this would get from product
            Timestamp endTime = new Timestamp(currentTimestamp.getTime() + 30 * 24 * 60 * 60 * 1000);//todo this would get from product
            long notUseDays = (endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
            renewVO.setPricePerDay(renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays)).add(dischargePrice).divide(BigDecimal.valueOf(notUseDays).add(duration)));
            dbf.getEntityManager().merge(renewVO);

        }


        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);

    }

    @Transactional
    private BigDecimal getValueblePayCash(String accountUuid, String productUuid) {
        BigDecimal total = BigDecimal.ZERO;
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.accountUuid, Op.EQ, accountUuid);
        query.add(OrderVO_.productUuid, Op.EQ, productUuid);
        query.add(OrderVO_.productEffectTimeEnd, Op.GT, dbf.getCurrentSqlTime());
        query.orderBy(OrderVO_.createDate, SimpleQuery.Od.DESC);
        List<OrderVO> orderVOs = query.list();
        if (orderVOs.size() == 0) {
            throw new IllegalArgumentException("the productUuid is not valid");
        }
        for (OrderVO orderVO : orderVOs) {
            if (orderVO.getType() == OrderType.DOWNGRADE || orderVO.getType() == OrderType.UN_SUBCRIBE) {
                break;
            }
            total = total.add(orderVO.getPayCash());
        }

        return total;

    }


    private void handle(APIUpdateAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        if(vo == null){
            APIValidateAccountMsg aMsg = new APIValidateAccountMsg();
            aMsg.setUuid(msg.getAccountUuid());
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
            accountBalanceVO.setUuid(msg.getAccountUuid());
            accountBalanceVO.setCreditPoint(BigDecimal.ZERO);
            accountBalanceVO.setPresentBalance(BigDecimal.ZERO);
            accountBalanceVO.setCashBalance(BigDecimal.ZERO);
            vo = dbf.persistAndRefresh(accountBalanceVO);
        }

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        int hash = msg.getAccountUuid().hashCode();
        if (hash < 0) {
            hash = ~hash;
        }
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (msg.getPresent() != null) {
            vo.setPresentBalance(vo.getPresentBalance().add(msg.getPresent()));
            DealDetailVO dealDetailVO = new DealDetailVO();
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getAccountUuid());
            dVO.setDealWay(DealWay.PRESENT_BILL);
            dVO.setIncome(msg.getPresent());
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(dbf.getCurrentSqlTime());
            dVO.setType(DealType.RECHARGE);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(vo.getCashBalance());
            dVO.setOutTradeNO(outTradeNO);
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
            dbf.persist(dVO);
        } else if (msg.getCash() != null) {
            vo.setCashBalance(vo.getCashBalance().add(msg.getCash()));
            DealDetailVO dealDetailVO = new DealDetailVO();
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(msg.getCash());
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(dbf.getCurrentSqlTime());
            dVO.setType(DealType.RECHARGE);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(vo.getCashBalance());
            dVO.setOutTradeNO(outTradeNO);
            dVO.setOpAccountUuid(msg.getSession().getAccountUuid());
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
        AccountBalanceVO vo =  dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        AccountBalanceInventory inventory = AccountBalanceInventory.valueOf(vo);
        APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID);
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

        if (msg instanceof APICreateOrderMsg) {
            validate((APICreateOrderMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateOrderMsg msg) {

    }

}
