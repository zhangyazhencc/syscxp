### VPN创建

1. 保存数据库，绑定证书
2. 创建订单，付款
3. 调用agent初始化

### VPN修改带宽
验证：tunnelUuid和vpnCertUuid不为空，修改次数

1. 创建订单，付款
2. 成功，保存数据库
3. 调用agent限速

### VPN删除

1. 退订
2. 成功，关闭VPN，accountUuid设为null
3. 解绑证书
4. 创建销毁任务

### VPN启用/关闭
验证：tunnelUuid和vpnCertUuid不为空

1. 调用agent停止服务
2. 成功，修改数据库
