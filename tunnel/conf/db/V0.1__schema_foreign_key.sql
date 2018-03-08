use syscxp_tunnel;

ALTER TABLE SpeedTestTunnelVO ADD CONSTRAINT fkSpeedTestTunnelVOTunnelVO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;


##节点区域关系表

ALTER TABLE `syscxp_tunnel`.`ZoneNodeRefVO` ADD CONSTRAINT fkZoneNodeRefVONodeEO FOREIGN KEY (nodeUuid) REFERENCES NodeEO (uuid) ON DELETE CASCADE;
ALTER TABLE `syscxp_tunnel`.`ZoneNodeRefVO` ADD CONSTRAINT fkZoneNodeRefVOzoneVO FOREIGN KEY (zoneUuid) REFERENCES ZoneVO (uuid) ON DELETE CASCADE;

##云专线端口信息表
ALTER TABLE TunnelSwitchPortVO ADD CONSTRAINT fkTunnelSwitchPortVOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;

##Qinq模式网段
ALTER TABLE QinqVO ADD CONSTRAINT fkQinqVOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;


##通道监控
ALTER TABLE TunnelMonitorVO ADD CONSTRAINT fkTunnelMonitorVOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;


ALTER TABLE MonitorHostVO ADD CONSTRAINT fkMonitorHostVOHostEO FOREIGN KEY (uuid) REFERENCES HostEO (uuid) ON UPDATE RESTRICT ON DELETE CASCADE;
ALTER TABLE MonitorHostVO ADD CONSTRAINT fkMonitorHostVONodeEO FOREIGN KEY (nodeUuid) REFERENCES NodeEO (uuid) ON UPDATE RESTRICT ON DELETE CASCADE;


ALTER TABLE SpeedRecordsVO ADD CONSTRAINT fkSpeedRecordsVOTunnelEO FOREIGN KEY (tunnelUuid) REFERENCES TunnelEO (uuid) ON DELETE CASCADE;


ALTER TABLE InterfaceEO ADD CONSTRAINT fkInterfaceEOEndpointEO FOREIGN KEY (endpointUuid) REFERENCES EndpointEO (uuid) ON DELETE RESTRICT;
ALTER TABLE InterfaceEO ADD CONSTRAINT fkInterfaceEOSwitchPortVO FOREIGN KEY (switchPortUuid) REFERENCES SwitchPortVO (uuid) ON DELETE RESTRICT;

ALTER TABLE EdgeLineEO ADD CONSTRAINT fkEdgeLineEOEndpointEO FOREIGN KEY (endpointUuid) REFERENCES EndpointEO (uuid) ON DELETE RESTRICT;
ALTER TABLE EdgeLineEO ADD CONSTRAINT fkEdgeLineEOInterfaceEO FOREIGN KEY (interfaceUuid) REFERENCES InterfaceEO (uuid) ON DELETE RESTRICT;

ALTER TABLE TunnelSwitchPortVO ADD CONSTRAINT fkTunnelSwitchPortVOSwitchPortVO FOREIGN KEY (switchPortUuid) REFERENCES SwitchPortVO (uuid) ON DELETE RESTRICT;
ALTER TABLE TunnelSwitchPortVO ADD CONSTRAINT fkTunnelSwitchPortVOEndpointEO FOREIGN KEY (endpointUuid) REFERENCES EndpointEO (uuid) ON DELETE RESTRICT;

ALTER TABLE SwitchEO ADD CONSTRAINT fkSwitchEOPhysicalSwitchEO FOREIGN KEY (physicalSwitchUuid) REFERENCES PhysicalSwitchEO (uuid) ON DELETE RESTRICT;

ALTER TABLE SwitchPortVO ADD CONSTRAINT fkSwitchPortVOSwitchEO FOREIGN KEY (switchUuid) REFERENCES SwitchEO (uuid) ON DELETE CASCADE;

CREATE INDEX idxResourceMotifyRecordVOcreateDate ON ResourceMotifyRecordVO (createDate);
CREATE INDEX idxResourceOrderEffectiveVOorderUuid ON ResourceOrderEffectiveVO (orderUuid);