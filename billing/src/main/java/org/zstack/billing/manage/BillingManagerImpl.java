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
import org.zstack.header.alipay.*;
import org.zstack.billing.identity.IdentityGlobalProperty;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
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
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;
    @Autowired
    private GlobalConfigFacade gcf;

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
        } else if (msg instanceof APIReChargeProxyMsg) {
            handle((APIReChargeProxyMsg) msg);
        } else if (msg instanceof APICreateOrderMsg) {
            handle((APICreateOrderMsg) msg);
        } else if (msg instanceof APIGetExpenseGrossMonthListMsg) {
            handle((APIGetExpenseGrossMonthListMsg) msg);
        } else if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        } else if (msg instanceof APIPayRenewOrderMsg) {
            handle((APIPayRenewOrderMsg) msg);
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
        } else if (msg instanceof APIDeleteCanceledOrderMsg) {
            handle((APIDeleteCanceledOrderMsg) msg);
        } else if (msg instanceof APIGetBillMsg) {
            handle((APIGetBillMsg) msg);
        } else if (msg instanceof APIGetMonetaryGroupByProductTypeMsg) {
            handle((APIGetMonetaryGroupByProductTypeMsg) msg);
        } else if (msg instanceof APICreateReceiptMsg) {
            handle((APICreateReceiptMsg) msg);
        } else if (msg instanceof APIConfirmReceiptMsg) {
            handle((APIConfirmReceiptMsg) msg);
        } else if (msg instanceof APIRechargeMsg) {
            handle((APIRechargeMsg) msg);
        } else if (msg instanceof APIVerifyReturnMsg) {
            handle((APIVerifyReturnMsg) msg);
        }else if (msg instanceof APIVerifyNotifyMsg) {
            handle((APIVerifyNotifyMsg) msg);
        } else if (msg instanceof APIAllotDischargeMsg) {
            handle((APIAllotDischargeMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIAllotDischargeMsg msg) {
        String uuid = msg.getUuid();
        AccountDischargeVO accountDischargeVO = dbf.findByUuid(uuid,AccountDischargeVO.class);
        accountDischargeVO.setDisCharge(msg.getDischarge());
        dbf.updateAndRefresh(accountDischargeVO);
        AccountDischargeInventory inventory = AccountDischargeInventory.valueOf(accountDischargeVO);
        APIAllotDischargeEvent evt = new APIAllotDischargeEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }

    private void handle(APIVerifyNotifyMsg msg) {
            Map<String, String> param = msg.getParam();
            APIVerifyNotifyReply reply = new APIVerifyNotifyReply();
            boolean signVerified = false;
            try {
                signVerified = AlipaySignature.rsaCheckV1(param, IdentityGlobalProperty.ALIPAY_PUBLIC_KEY, IdentityGlobalProperty.CHARSET, IdentityGlobalProperty.SIGN_TYPE); //调用SDK验证签名
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
                q.add(DealDetailVO_.state,Op.EQ,DealState.SUCCESS.toString());
                DealDetailVO dealDetailVO = q.find();

                if (dealDetailVO == null || dealDetailVO.getIncome().compareTo(new BigDecimal(total_amount)) != 0 || !seller_id.equals(IdentityGlobalProperty.SELLER_ID) || !app_id.equals(IdentityGlobalProperty.APP_ID)) {
                    reply.setInventory(false);
                }

                if(trade_status.equals("TRADE_FINISHED")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    if(dealDetailVO.getState().equals(DealState.FAILURE)) {
                        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
                        BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                        vo.setCashBalance(balance);
                        dbf.updateAndRefresh(vo);

                        dealDetailVO.setBalance(balance);
                        dealDetailVO.setState(DealState.SUCCESS);
                        dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                        dealDetailVO.setTradeNO(trade_no);
                        dbf.updateAndRefresh(dealDetailVO);
                    }

                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                }else if (trade_status.equals("TRADE_SUCCESS")){
                    //判断该笔订单是否在商户网站中已经做过处理
                    //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    //如果有做过处理，不执行商户的业务程序
                    if(dealDetailVO.getState().equals(DealState.FAILURE)) {
                        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
                        BigDecimal balance = vo.getCashBalance().add(new BigDecimal(total_amount));
                        vo.setCashBalance(balance);
                        dbf.updateAndRefresh(vo);

                        dealDetailVO.setBalance(balance);
                        dealDetailVO.setState(DealState.SUCCESS);
                        dealDetailVO.setFinishTime(dbf.getCurrentSqlTime());
                        dealDetailVO.setTradeNO(trade_no);
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
            signVerified = AlipaySignature.rsaCheckV1(param, IdentityGlobalProperty.ALIPAY_PUBLIC_KEY, IdentityGlobalProperty.CHARSET, IdentityGlobalProperty.SIGN_TYPE); //调用SDK验证签名
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

            if (dealDetailVO == null || dealDetailVO.getIncome().setScale(2).compareTo(new BigDecimal(total_amount)) != 0 ||!seller_id.equals(IdentityGlobalProperty.SELLER_ID) || !app_id.equals(IdentityGlobalProperty.APP_ID)) {
                reply.setInventory(false);
                bus.reply(msg, reply);
                return;
            } else {
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
        BigDecimal total = msg.getTotal().setScale(2,BigDecimal.ROUND_HALF_UP);
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        String accountUuid = msg.getSession().getAccountUuid();
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
        vo.setDealWay(DealWay.BALANCE_BILL);
        vo.setIncome(total);
        vo.setFinishTime(currentTimestamp);
        vo.setAccountUuid(accountUuid);
        dbf.persistAndRefresh(vo);
        AlipayClient alipayClient = new DefaultAlipayClient(IdentityGlobalProperty.GATEWAYURL, IdentityGlobalProperty.APP_ID, IdentityGlobalProperty.MERCHANT_PRIVATE_KEY, "json", IdentityGlobalProperty.CHARSET, IdentityGlobalProperty.ALIPAY_PUBLIC_KEY, IdentityGlobalProperty.SIGN_TYPE);
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(IdentityGlobalProperty.RETURN_URL);
        alipayRequest.setNotifyUrl(IdentityGlobalProperty.NOTIFY_URL);
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

    private void handle(APIConfirmReceiptMsg msg) {
        String receiptUuid = msg.getReceiptUuid();
        ReceiptState state = msg.getState();
        ReceiptVO vo = dbf.findByUuid(receiptUuid, ReceiptVO.class);
        vo.setState(msg.getState());
        if (vo.getState().equals(ReceiptState.REJECT)) {
            vo.setCommet(msg.getReason());
        }
        dbf.updateAndRefresh(vo);
        ReceiptInventory inventory = ReceiptInventory.valueOf(vo);
        APIConfirmReceiptEvent evt = new APIConfirmReceiptEvent(msg.getId());
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

    private void handle(APIGetMonetaryGroupByProductTypeMsg msg) {
        String accountUuid = msg.getSession().getAccountUuid();
        String sql = "select count(*) as categoryCount, sum(payPresent) as payPresentTotal,sum(payCash) as payCashTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime BETWEEN :dateStart and  :dateEnd  group by productType ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", accountUuid);
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs = q.getResultList();
        List<Monetary> bills = objs.stream().map(Monetary::new).collect(Collectors.toList());

        APIGetMonetaryGroupByProductTypeReply reply = new APIGetMonetaryGroupByProductTypeReply();
        reply.setInventory(bills);
        bus.reply(msg, reply);

    }

    private void handle(APIGetBillMsg msg) {
        BillVO vo = dbf.findByUuid(msg.getUuid(), BillVO.class);
        BillInventory inventory = BillInventory.valueOf(vo);
        APIGetBillReply reply = new APIGetBillReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    private void handle(APIDeleteCanceledOrderMsg msg) {
        String orderUuid = msg.getUuid();
        OrderVO vo = dbf.findByUuid(orderUuid, OrderVO.class);
        if (vo == null || !vo.getState().equals(OrderState.CANCELED)) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_PERMIT_UPDATE, String.format("if order not this state, can not be deleted")));
        }
        dbf.remove(vo);
        OrderInventory ri = OrderInventory.valueOf(vo);
        APIDeleteCanceledOrderEvent evt = new APIDeleteCanceledOrderEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

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
            vo.setState(msg.getState());//todo this would handle product interface
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

    @Transactional
    private void handle(APIPayRenewOrderMsg msg) {
        String orderUuid = msg.getOrderUuid();
        OrderVO orderVo = dbf.findByUuid(orderUuid, OrderVO.class);
        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        if (!orderVo.getState().equals(OrderState.NOTPAID)) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_PERMIT_UPDATE, String.format("if order not this state, can not be updated")));
        }
        BigDecimal total = orderVo.getPayCash();
        Timestamp currentTimeStamp = dbf.getCurrentSqlTime();

        if (msg.getConfirm().equals(Confirm.OK)) {
            payMethod(msg, orderVo, abvo, total, currentTimeStamp);
            orderVo.setState(OrderState.PAID);
        } else if (msg.getConfirm().equals(Confirm.CANCEL)) {
            orderVo.setState(OrderState.CANCELED);
        }

        dbf.updateAndRefresh(orderVo);
        OrderInventory oi = OrderInventory.valueOf(orderVo);
        APIPayRenewOrderEvent evt = new APIPayRenewOrderEvent(msg.getId());
        evt.setInventory(oi);
        bus.publish(evt);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    void payMethod(APIMessage msg, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(msg.getSession().getAccountUuid());
                dealDetailVO.setDealWay(DealWay.BALANCE_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(total.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(presentNow);
                dealDetailVO.setOutTradeNO(orderVo.getUuid());
                dbf.persistAndRefresh(dealDetailVO);

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
                dealDetailVO.setDealWay(DealWay.BALANCE_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(payPresent.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(BigDecimal.ZERO);
                dealDetailVO.setOutTradeNO(orderVo.getUuid());
                dbf.persistAndRefresh(dealDetailVO);

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
                dVO.setOutTradeNO(orderVo.getUuid());
                dbf.persistAndRefresh(dVO);
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
            dbf.persistAndRefresh(dVO);
        }
    }

    private void handle(APIUpdateRenewMsg msg) {
        boolean isRenewAuto = msg.isRenewAuto();
        String uuid = msg.getUuid();
        RenewVO vo = dbf.findByUuid(uuid, RenewVO.class);
        if (vo.isRenewAuto() != isRenewAuto) {
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
    private void handle(APICreateOrderMsg msg){
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        ProductPriceUnit productPriceUnit = msg.getProductPriceUnit();

        SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
        q.add(ProductPriceUnitVO_.category, Op.EQ, productPriceUnit.getCategory());
        q.add(ProductPriceUnitVO_.productType, Op.EQ, productPriceUnit.getProductType());
        q.add(ProductPriceUnitVO_.config, Op.EQ, productPriceUnit.getConfig());
        ProductPriceUnitVO productPriceUnitVO = q.find();
        if(productPriceUnitVO == null){
            throw new IllegalArgumentException("product config is invalid");
        }

        SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
        qDischarge.add(AccountDischargeVO_.category, Op.EQ, productPriceUnit.getCategory());
        qDischarge.add(AccountDischargeVO_.productType, Op.EQ, productPriceUnit.getProductType());
        qDischarge.add(AccountDischargeVO_.accountUuid, Op.EQ, msg.getSession().getAccountUuid());
        AccountDischargeVO accountDischargeVO = qDischarge.find();
        int productDisCharge = 100;
        if(accountDischargeVO != null){
            productDisCharge = accountDischargeVO.getDisCharge()==0?100:accountDischargeVO.getDisCharge();
        }

        BigDecimal duration = BigDecimal.valueOf(msg.getDuration());

        if (msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
            duration = duration.multiply(BigDecimal.valueOf(12));
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal reCash = BigDecimal.ZERO;//drop product
        BigDecimal downCash = BigDecimal.ZERO;//downgrade

        OrderVO orderVo = new OrderVO();

        if(msg.getType().equals(OrderType.UPGRADE)){
            OrderVO oldOrderVO = dbf.findByUuid(msg.getOldOrderUuid(), OrderVO.class);
            BigDecimal oldPayPreset = oldOrderVO.getPayPresent();
            BigDecimal oldPayCash = oldOrderVO.getPayCash();
            Timestamp startTime = oldOrderVO.getProductEffectTimeStart();//todo this would get from product
            Timestamp endTime = oldOrderVO.getProductEffectTimeEnd();//todo this would get from product
            long useDays = (currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            long needPayDays = (endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24)+1 ;
            long days = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            BigDecimal avgOldPriceByDay = oldPayCash.add(oldPayPreset).divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_EVEN);
            BigDecimal usedMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(useDays));
            BigDecimal remainMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(needPayDays));
            originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(needPayDays)).subtract(remainMoney);
            total = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(needPayDays)).multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN)).subtract(remainMoney);

            if (total.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            payMethod(msg, orderVo, abvo, total, currentTimestamp);
            //todo modify product config
        } else if(msg.getType().equals(OrderType.DOWNGRADE)){
            OrderVO oldOrderVO = dbf.findByUuid(msg.getOldOrderUuid(), OrderVO.class);
            BigDecimal oldPayPreset = oldOrderVO.getPayPresent();
            BigDecimal oldPayCash = oldOrderVO.getPayCash();
            Timestamp startTime = oldOrderVO.getProductEffectTimeStart();//todo this would get from product
            Timestamp endTime = oldOrderVO.getProductEffectTimeEnd();//todo this would get from product
            long useDays = (currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            long needPayDays = (endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24)+1 ;
            long days = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            BigDecimal avgOldPriceByDay = oldPayCash.add(oldPayPreset).divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_EVEN);
            BigDecimal usedMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(useDays));
            BigDecimal remainMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(needPayDays));
            BigDecimal needPay = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(new BigDecimal(needPayDays));
            if (usedMoney.compareTo(oldPayPreset) <= 0) {
                downCash = oldPayCash;
            } else {
                downCash = oldPayPreset.add(oldPayCash).subtract(usedMoney).subtract(needPay);
            }
            BigDecimal remainCash = abvo.getCashBalance().add(downCash);
            abvo.setCashBalance(remainCash);
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getSession().getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(downCash);
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(currentTimestamp);
            dVO.setType(DealType.REFUND);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCash);
            dVO.setOutTradeNO(orderVo.getUuid());
            dbf.persistAndRefresh(dVO);

        } else if(msg.getType().equals(OrderType.UN_SUBCRIBE)){
            OrderVO oldOrderVO = dbf.findByUuid(msg.getOldOrderUuid(), OrderVO.class);
            BigDecimal oldPayPreset = oldOrderVO.getPayPresent();
            BigDecimal oldPayCash = oldOrderVO.getPayCash();
            Timestamp startTime = oldOrderVO.getProductEffectTimeStart();//todo this would get from product
            Timestamp endTime = oldOrderVO.getProductEffectTimeEnd();//todo this would get from product
            long useDays = (currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            long needPayDays = (endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24)+1 ;
            long days = (endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
            BigDecimal avgOldPriceByDay = oldPayCash.add(oldPayPreset).divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_EVEN);
            BigDecimal usedMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(useDays));
            BigDecimal remainMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(needPayDays));
            if (usedMoney.compareTo(oldPayPreset) <= 0) {
                reCash = oldPayCash;
            } else {
                reCash = oldPayPreset.add(oldPayCash).subtract(usedMoney);
            }
            BigDecimal remainCash = abvo.getCashBalance().add(reCash);
            abvo.setCashBalance(remainCash);
            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getSession().getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(reCash);
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(currentTimestamp);
            dVO.setType(DealType.REFUND);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCash);
            dVO.setOutTradeNO(orderVo.getUuid());
            dbf.persistAndRefresh(dVO);
        } else if(msg.getType().equals(OrderType.BUY)){
            originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(duration);
            total = originalPrice.multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));

            if (total.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            payMethod(msg, orderVo, abvo, total, currentTimestamp);
            //todo create product and order set productUuid

        } else if(msg.getType().equals(OrderType.SLA_COMPENSATION)){
            orderVo.setPayCash(BigDecimal.ZERO);
            orderVo.setPayPresent(BigDecimal.ZERO);
            
        } else if(msg.getType().equals(OrderType.RENEW)){
            originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(duration);
            total = originalPrice.multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
            if (total.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            payMethod(msg, orderVo, abvo, total, currentTimestamp);
            //todo generate product from tunel

        }

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getSession().getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setType(msg.getType());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        calendar.add(Calendar.MONTH, duration.intValue());
        orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));
        orderVo.setProductEffectTimeStart(currentTimestamp);

        orderVo.setProductChargeModel(msg.getProductChargeModel());//todo this value would be got from account
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDiscount(BigDecimal.valueOf(productDisCharge));
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setPrice(total);
        orderVo.setDuration(msg.getDuration());

        dbf.updateAndRefresh(abvo);
        OrderVO orderNewVO = dbf.persistAndRefresh(orderVo);
        switch (orderNewVO.getType()) {
            case BUY:
            case RENEW:
                SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
                q.add(RenewVO_.accountUuid, Op.EQ, orderNewVO.getAccountUuid());
                q.add(RenewVO_.productUuid, Op.EQ, orderNewVO.getProductUuid());
                RenewVO renewVO = query.find();
                if (renewVO != null) {//if bought this product then update it
                    renewVO.setDuration(orderNewVO.getDuration());
                    renewVO.setProductChargeModel(orderNewVO.getProductChargeModel());
                    renewVO.setExpiredDate(orderNewVO.getProductEffectTimeEnd());
                    renewVO.setProductUnitPriceUuid(orderNewVO.getProductUnitPriceUuid());
                    dbf.updateAndRefresh(renewVO);
                } else {
                    renewVO = new RenewVO();
                    renewVO.setUuid(Platform.getUuid());
                    renewVO.setProductChargeModel(orderNewVO.getProductChargeModel());
                    renewVO.setDuration(orderNewVO.getDuration());
                    renewVO.setProductUuid(orderNewVO.getProductUuid());
                    renewVO.setAccountUuid(orderNewVO.getAccountUuid());
                    renewVO.setProductName(orderNewVO.getProductName());
                    renewVO.setProductType(orderNewVO.getProductType());
                    renewVO.setExpiredDate(orderNewVO.getProductEffectTimeEnd());
                    renewVO.setProductUnitPriceUuid(orderNewVO.getProductUnitPriceUuid());
                    dbf.persistAndRefresh(renewVO);
                }
                break;

        }

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);

    }


    private void handle(APIReChargeProxyMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid( msg.getAccountUuid(), AccountBalanceVO.class);

        if(msg.getPresent()!=null){
            vo.setPresentBalance(vo.getPresentBalance().add(msg.getPresent()));
        }else if(msg.getCash()!=null){
            vo.setCashBalance(vo.getCashBalance().add(msg.getCash()));
        }else if(msg.getCredit()!=null){
            vo.setCreditPoint(msg.getCredit());
        }
        vo = dbf.updateAndRefresh(vo);
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        APIReChargeProxyEvent evt = new APIReChargeProxyEvent(msg.getId());
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
        switch (msg.getType()) {
            case UPGRADE:
            case DOWNGRADE:
            case UN_SUBCRIBE:
                if (StringUtils.isEmpty(msg.getOldOrderUuid())) {
                    throw new IllegalArgumentException(" oldOrderId must be not null");
                }
                OrderVO oldOrderVO = dbf.findByUuid(msg.getOldOrderUuid(), OrderVO.class);
                if (oldOrderVO == null) {
                    throw new IllegalArgumentException("the order that wanna op is not find,please check it out");
                }
        }
    }

}
