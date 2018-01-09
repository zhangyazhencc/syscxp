### 部署Host
+ hostIp 用于管理
+ publicIp 用于VPN公网接入
+ hostType 唯一值VPN
+ 用户名·密码·端口修改要Host对应，否则无法连接
+ status agent连接状态
+ state 只用启用**Enable**才能用于创建VPN

### Host接口
+ interfaceName 运行VpnServer的网卡
+ endpointUuid  host能支持的连接点
+ Host删除，相应的接口清空