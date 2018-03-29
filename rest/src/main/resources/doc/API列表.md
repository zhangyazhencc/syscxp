# API列表

## 接口列表

|接口功能|Action ID|功能描述|
|---|---|---|
|查看带宽规格|QueryBandwidthOffering|用于查看带宽规格|
|查看端口规格|QueryPortOffering|用于查看端口规格|
|---|---|---|
|创建最后一公里|CreateEdgeLine|用于申请最后一公里|
|最后一公里续费|RenewEdgeLine|用于最后一公里续费|
|查看最后一公里|QueryEdgeLine|用于查看用户申请的最后一公里|
|查看最后一公里的退订价格|GetUnscribeEdgeLinePriceDiff|用于查看最后一公里的退订价格|
|---|---|---|
|购买物理接口|CreateInterface|用于购买物理接口|
|修改物理接口|UpdateInterface|用于修改指定物理接口的信息|
|删除物理接口|DeleteInterface|用于删除指定物理接口|
|查看物理接口|QueryInterface|用于查看用户购买的物理接口|
|物理接口续费|RenewInterface|用于给指定物理接口续费|
|查看物理接口价格|GetInterfacePrice|用于查看物理接口价格|
|查看可用物理接口类型|GetInterfaceType|用于查看可用物理接口类型|
|按类型查看交换机端口|ListSwitchPortByType|用于按类型查看交换机端口|
|查看物理接口续费的价格|GetRenewInterfacePrice|用于查看物理接口续费的价格|
|查看物理接口的退订价格|GetUnscribeInterfacePriceDiff|用于查看物理接口的退订价格|
|---|---|---|
|购买云专线|CreateTunnel|用于购买云专线|
|修改云专线|UpdateTunnel|用于修改指定云专线的信息|
|修改云专线带宽|UpdateTunnelBandwidth|用于修改指定云专线的带宽|
|云专线续费|RenewTunnel|用于给指定云专线续费|
|删除云专线|DeleteTunnel|用于删除指定云专线|
|查看云专线|QueryTunnel|用于查看用户购买的云专线|
|查看云专线价格|GetTunnelPrice|用于查看云专线价格|
|查看修改云专线带宽的价格|GetModifyTunnelPriceDiff|用于查看修改云专线带宽的价格|
|查看云专线续费的价格|GetRenewTunnelPrice|用于查看云专线续费的价格|
|查看云专线的退订价格|GetUnscribeTunnelPriceDiff|用于查看云专线的退订价格|
|关闭云专线|DisableTunnel|用于关闭云专线|
|开启云专线|EnableTunnel|用于开启云专线|
|查看共点云专线|ListCrossTunnel|用于查看共点云专线|
|查看内联连接点|ListInnerEndpoint|用于查看内联连接点|
|---|---|---|
|开启云专线监控|StartTunnelMonitor|用于开启指定云专线的监控|
|停止云专线监控|StopTunnelMonitor|用于停止指定云专线的监控|
|重启云专线监控|RestartTunnelMonitor|用于重启指定云专线的监控|
