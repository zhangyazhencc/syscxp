package com.syscxp.sdk;

import java.util.HashMap;

public class SourceClassMap {
    final static HashMap<String, String> srcToDstMapping = new HashMap() {
        {
			put("com.syscxp.header.configuration.BandwidthOfferingInventory", "com.syscxp.sdk.BandwidthOfferingInventory");
			put("com.syscxp.header.tunnel.edgeLine.EdgeLineInventory", "com.syscxp.sdk.EdgeLineInventory");
			put("com.syscxp.header.tunnel.endpoint.EndpointInventory", "com.syscxp.sdk.EndpointInventory");
			put("com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointInventory", "com.syscxp.sdk.InnerConnectedEndpointInventory");
			put("com.syscxp.header.tunnel.host.HostSwitchMonitorInventory", "com.syscxp.sdk.HostSwitchMonitorInventory");
			put("com.syscxp.header.tunnel.host.NettoolMonitorHostInventory", "com.syscxp.sdk.NettoolMonitorHostInventory");
			put("com.syscxp.header.tunnel.monitor.SpeedRecordsInventory", "com.syscxp.sdk.SpeedRecordsInventory");
			put("com.syscxp.header.tunnel.node.NodeInventory", "com.syscxp.sdk.NodeInventory");
			put("com.syscxp.header.tunnel.switchs.SwitchPortInventory", "com.syscxp.sdk.SwitchPortInventory");
			put("com.syscxp.header.tunnel.tunnel.InterfaceInventory", "com.syscxp.sdk.InterfaceInventory");
			put("com.syscxp.header.tunnel.tunnel.PortOfferingInventory", "com.syscxp.sdk.PortOfferingInventory");
			put("com.syscxp.header.tunnel.tunnel.TunnelInventory", "com.syscxp.sdk.TunnelInventory");
			put("com.syscxp.header.tunnel.tunnel.TunnelSwitchPortInventory", "com.syscxp.sdk.TunnelSwitchPortInventory");
        }
    };

    final static HashMap<String, String> dstToSrcMapping = new HashMap() {
        {
			put("com.syscxp.sdk.BandwidthOfferingInventory", "com.syscxp.header.configuration.BandwidthOfferingInventory");
			put("com.syscxp.sdk.EdgeLineInventory", "com.syscxp.header.tunnel.edgeLine.EdgeLineInventory");
			put("com.syscxp.sdk.EndpointInventory", "com.syscxp.header.tunnel.endpoint.EndpointInventory");
			put("com.syscxp.sdk.HostSwitchMonitorInventory", "com.syscxp.header.tunnel.host.HostSwitchMonitorInventory");
			put("com.syscxp.sdk.InnerConnectedEndpointInventory", "com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointInventory");
			put("com.syscxp.sdk.InterfaceInventory", "com.syscxp.header.tunnel.tunnel.InterfaceInventory");
			put("com.syscxp.sdk.NettoolMonitorHostInventory", "com.syscxp.header.tunnel.host.NettoolMonitorHostInventory");
			put("com.syscxp.sdk.NodeInventory", "com.syscxp.header.tunnel.node.NodeInventory");
			put("com.syscxp.sdk.PortOfferingInventory", "com.syscxp.header.tunnel.tunnel.PortOfferingInventory");
			put("com.syscxp.sdk.SpeedRecordsInventory", "com.syscxp.header.tunnel.monitor.SpeedRecordsInventory");
			put("com.syscxp.sdk.SwitchPortInventory", "com.syscxp.header.tunnel.switchs.SwitchPortInventory");
			put("com.syscxp.sdk.TunnelInventory", "com.syscxp.header.tunnel.tunnel.TunnelInventory");
			put("com.syscxp.sdk.TunnelSwitchPortInventory", "com.syscxp.header.tunnel.tunnel.TunnelSwitchPortInventory");
        }
    };
}
