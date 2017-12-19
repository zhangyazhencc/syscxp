#单价定义

##产品分类表 ProductCategoryVO
   ```
     字段：private String uuid;
            private Category code;
            private String name;
            private ProductType productTypeCode;
            private String productTypeName;
        定义产品的类型和分类
   ```
##  产品单价表 ProductPriceUnitVO
  ```
     字段：private String ProductCategoryVO.uuid;
               private String areaCode;
               private String areaName;
               private String lineCode;
               private String lineName;
               private String configCode;
               private String configName;
               private int unitPrice;
           定义产品的类型和分类
   ```
## 由以上两张表 五个属性定义一个产品的价格五个属性分别为
   * productType 产品分类
   * categoryCode 产品小类
   * areaCode  区域code
   * lineCode  线Code
   * configCode 配置Code
   
 默认价格： 如果lineCode 在数据字典中找不到会把lineCode 设置为"DEFAULT"继续找默认价格
          如果还找不到则会把areaCode设置为"DEFAULT" 继续找默认价格
          如果还没找到则需要预先添加该单价
1. 云专线
    1. 同城 --> 'TUNNEL','CITY','DEFAULT','DEFAULT','2M'
    2. 区域 --> 'TUNNEL','REGION','CSJ','DEFAULT','2M'
    3. 长传 --> 'TUNNEL','LONG','DEFAULT','DEFAULT','2M'
    4. 跨国 --> 'TUNNEL','ABROAD','CHINA2ABROAD','昆山/法国','2M'
2. 互联云
    1. 带宽  -->  'ECP','BANDWIDTH','上海','5ab81d8796964113a37768042913270e','1M'
                  1M为固定配置价格，其他配置的价格则为1M的价格乘以相应配置
                  例如1M的价格为1000元/月 ，5M 的则为5*1000 元/月 以此类推
    2. 主机 --> 'ECP','HOST','DEFAULT','DEFAULT','005a7e6d1c354d81bf35a7f8afda5dca'
    3. 数据盘  --> 'ECP','DISK','DEFAULT','DEFAULT','71cd5ed52dac407ba13d696a638a3bb9'
3. 端口
   1. 'PORT','PORT','DEFAULT','DEFAULT','005a7e6d1c354d81bf35a7f8afda5dca'
                     