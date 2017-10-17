package com.syscxp.tunnel.manage;

import com.syscxp.tunnel.header.controller.ControllerRestResponse;
import com.syscxp.tunnel.header.controller.TunnelMonitorMpls;
import com.syscxp.tunnel.header.controller.TunnelMonitorSdn;

import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-10-16.
 * @Description: 控制器下发命令.
 */
public class ControllerCommands {
    /**
     * 启动/停止监控命令
     */
    public static class TunnelMonitorCommand {
        private List<TunnelMonitorSdn> sdnConfig;
        private List<TunnelMonitorMpls> mplsConfig;

        /**
         *
         * @param sdnConfigList sdn交换机命令
         * @param mplsConfigList mpls交换机命令
         * @return 启动监控机命令
         */
        public static TunnelMonitorCommand valueOf(List<TunnelMonitorSdn> sdnConfigList, List<TunnelMonitorMpls> mplsConfigList) {

            TunnelMonitorCommand tunnelMonitorCmd = new TunnelMonitorCommand();
            tunnelMonitorCmd.setSdnConfig(sdnConfigList);
            tunnelMonitorCmd.setMplsConfig(mplsConfigList);

            return tunnelMonitorCmd;
        }

        public List<TunnelMonitorSdn> getSdnConfig() {
            return sdnConfig;
        }

        public void setSdnConfig(List<TunnelMonitorSdn> sdnConfig) {
            this.sdnConfig = sdnConfig;
        }

        public List<TunnelMonitorMpls> getMplsConfig() {
            return mplsConfig;
        }

        public void setMplsConfig(List<TunnelMonitorMpls> mplsConfig) {
            this.mplsConfig = mplsConfig;
        }
    }

    public static class TunnelMonitorResponse extends ControllerRestResponse {

    }

    /**
     * 开启tunnel命令
     */
    public static class StartTunnelCommand {

    }

    /**关闭tunnel监控命令
     */
    public static class StopTunnelCommand {

    }

}
