# 订单
## 创建订单
1. 消息uri com.syscxp.header.billing.APICreateBuyOrderMsg
2. 参数 List<ProductInfoForOrder> products
3. ProductInfoForOrder 字段列表
   *  ProductChargeModel productChargeModel;按天还是按年 **as**："BY_DAY","BY_YEAR" 
   *  private int duration;购买期限  **as**：10
   *  private List<ProductPriceUnit> units; **as**：
   *  private String productName;价格单元 **as**：产品名称
   *  private ProductType productType;：产品类型 **as**
   *  private String productUuid;产品id **as**：
   *  private String descriptionData;产品说明 **as**：
   *  private String callBackData; 返回数据**as**：
   *  private String accountUuid;用户id **as**：
   *  private String opAccountUuid; 操作人id**as**：
   *  private String notifyUrl;回调通知地址 **as**：
      * 价格单元字段： 
       1. private ProductType productTypeCode;产品类型id
       2. private Category categoryCode;产品分类id
       3. private String areaCode = "DEFAULT";产品区域id
       4. private String lineCode = "DEFAULT";产品线路id
       5. private String configCode = "DEFAULT";产品配置id
 4. 执行logic
     ```
        此接口可以创建多条订单 units 每条元素对应创建一条订单需要的参数
        每条订单可以传入多个单价ProductPriceUnit
        通过ProductPriceUnit 获得每条计价单位需要的价格
        通过accountUuid 获取此条价格对此用户折扣信息
        通过duration 和 productChargeModel 计算出此条订单的打折前后的需要价格
        通过accountUUid 获取该用户的余额信息
        验证账户余额是否充足
        生成订单和用户消费记录并扣除用户相应余额
        生成该用户该产品续费记录并记录下当时此产品的单价，自动续费按此价格
        保存通知回调的信息，job定时通知创建订单成功
     ```
## 创建订单父类 APICreateOrderMsg
       1. uri com.syscxp.header.billing.APICreateOrderMsg
       2. 参数
           * private List<ProductPriceUnit> units; 价格单元
           * private String productName; 产品名称
           * private ProductType productType; 产品类型
           * private String productUuid; 产品id
           * private String descriptionData; 产品描述信息
           * private String callBackData; 返回信息
           * private String accountUuid; 用户id
           * private String opAccountUuid; 操作用户id
           * private String notifyUrl; 回调通知url
## 退费订单
1. 消息uri com.syscxp.header.billing.APICreateUnsubcribeOrderMsg 继承 APICreateOrderMsg
2. 参数
     * private Timestamp startTime;
     * private Timestamp expiredTime;
     * private boolean isCreateFailure;
     * APICreateOrderMsg 字段
     
3. 执行逻辑
   ```
      如果是用户自己删除产品调用此接口会扣除一天的费用且购买使用的赠送金额不返还
      如果是公司没有开通成功产生退费则全部退还包括赠送金额
      通过accountUUid 和productUuid 找到用户续费表里保存有该产品当时购买是的价格a
      通过传入过来的产品过期时间计算到现在还剩多少时间b，
      剩余时间算法：未满一个月按还剩天数除以该月的天数，小数位后保留4位
      计算出该产品赔偿的天数的价格c，计算原理同上
      a*b-c 获得该产品还剩得的价格
      生成退费订单
      删除用户该产品的续费记录
      生成该交易记录
  
   ```

##修改产品订单
1. 消息uri com.syscxp.header.billing.APICreateModifyOrderMsg
2. 参数
     * private Timestamp startTime;
     * private Timestamp expiredTime;
     * APICreateOrderMsg 字段
     
3. 执行逻辑

      ```
         通过传入过来的新修改后配置信息获取价格 a
         通过续费表保存产品当时购买的价格和过期时间计算出该产品还剩余多少钱 b
         a-b>0 则为升级，需要支付a-b的钱，和该用户可支付的余额对比 如果不够抛错
         a-b <=0 降级，此时需要计算该产品在有效期内有没有被赔偿过，赔偿的有效期内的钱计算出来为c不坐为退费相
         则退费的钱为 a-b-c为该产品降级应退费的钱
         生成订单
         记录交易记录
         修改续费表记录下该产品现在的价格
         修改用户余额
         通知回调
      ```
      
## 续费订单
