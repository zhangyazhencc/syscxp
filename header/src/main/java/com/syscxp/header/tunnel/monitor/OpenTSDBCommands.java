package com.syscxp.header.tunnel.monitor;

import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: OpenTSDB数据查询.
 */
public class OpenTSDBCommands {

    public OpenTSDBCommands() {
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-14.
     * @Description: 查询参数.
     */

    public interface restMethod {
        public static final String OPEN_TSDB_QUERY = "/api/query";
    }

    public static class TunnelQueryCondition {
        private Long start;
        private Long end;
        private List<TunnelQuery> queries;

        public TunnelQueryCondition() {
        }

        public TunnelQueryCondition(Long start, Long end, List<TunnelQuery> queries) {
            this.start = start;
            this.end = end;
            this.queries = queries;
        }

        public Long getStart() {
            return start;
        }

        public void setStart(Long start) {
            this.start = start;
        }

        public Long getEnd() {
            return end;
        }

        public void setEnd(Long end) {
            this.end = end;
        }

        public List<TunnelQuery> getQueries() {
            return queries;
        }

        public void setQueries(List<TunnelQuery> queries) {
            this.queries = queries;
        }
    }

    public static class TunnelQuery {
        private String aggregator;
        private String metric;
        private TunnelTags tags;

        public TunnelQuery(String aggregator, String metric, TunnelTags tags) {
            this.aggregator = aggregator;
            this.metric = metric;
            this.tags = tags;
        }

        public TunnelQuery() {
        }

        public String getAggregator() {
            return aggregator;
        }

        public void setAggregator(String aggregator) {
            this.aggregator = aggregator;
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public TunnelTags getTags() {
            return tags;
        }

        public void setTags(TunnelTags tags) {
            this.tags = tags;
        }
    }

    public static class TunnelTags {
        private String endpoint;
        private String ifname;
        private String tunnelid;

        public TunnelTags(String endpoint, String ifname, String tunnelid) {
            this.endpoint = endpoint;
            this.ifname = ifname;
            this.tunnelid = tunnelid;
        }

        public TunnelTags(String endpoint, String ifname) {
            this.endpoint = endpoint;
            this.ifname = ifname;
        }

        public TunnelTags() {
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getIfname() {
            return ifname;
        }

        public void setIfname(String ifname) {
            this.ifname = ifname;
        }

        public String getTunnelid() {
            return tunnelid;
        }

        public void setTunnelid(String tunnelid) {
            this.tunnelid = tunnelid;
        }
    }

    public static class TunnelCustomCondition {
        private String nodeUuid;
        private String endpointUuid;
        private Map<String, TunnelTags> tags;

        public String getEndpointUuid() {
            return endpointUuid;
        }

        public void setEndpointUuid(String endpointUuid) {
            this.endpointUuid = endpointUuid;
        }

        public Map<String, TunnelTags> getTags() {
            return tags;
        }

        public void setTags(Map<String, TunnelTags> tags) {
            this.tags = tags;
        }

        public String getNodeUuid() {
            return nodeUuid;
        }

        public void setNodeUuid(String nodeUuid) {
            this.nodeUuid = nodeUuid;
        }
    }

    public static class L3Tags {
        private String endpoint;
        private String ifname;
        private String dstendpointid;

        public L3Tags(String endpoint, String ifname, String dstendpointid) {
            this.endpoint = endpoint;
            this.ifname = ifname;
            this.dstendpointid = dstendpointid;
        }

        public L3Tags(String endpoint, String ifname) {
            this.endpoint = endpoint;
            this.ifname = ifname;
        }

        public L3Tags() {
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getIfname() {
            return ifname;
        }

        public void setIfname(String ifname) {
            this.ifname = ifname;
        }

        public String getDstendpointid() {
            return dstendpointid;
        }

        public void setDstendpointid(String dstendpointid) {
            this.dstendpointid = dstendpointid;
        }
    }

    public static class L3CustomCondition {
        private String l3EndpointUuid;
        private L3Tags trafficTags;
        private Map<String, L3Tags> icmpTags;

        public String getL3EndpointUuid() {
            return l3EndpointUuid;
        }

        public void setL3EndpointUuid(String l3EndpointUuid) {
            this.l3EndpointUuid = l3EndpointUuid;
        }

        public L3Tags getTrafficTags() {
            return trafficTags;
        }

        public void setTrafficTags(L3Tags trafficTags) {
            this.trafficTags = trafficTags;
        }

        public Map<String, L3Tags> getIcmpTags() {
            return icmpTags;
        }

        public void setIcmpTags(Map<String, L3Tags> icmpTags) {
            this.icmpTags = icmpTags;
        }
    }

    /**
     * @Author: sunxuelong.
     * @Cretion Date: 2017-11-02.
     * @Description: Agent API返回对象.
     */
    public static class RestResponse {
        private List<QueryResult> results;

        public List<QueryResult> getResults() {
            return results;
        }

        public void setResults(List<QueryResult> results) {
            this.results = results;
        }
    }

    public static class QueryResult {
        private String nodeUuid;
        private String metric;
        private TunnelTags tags;
        private List aggregateTags;
        private Map<Long, Double> dps;

        public QueryResult() {
        }

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public TunnelTags getTags() {
            return tags;
        }

        public void setTags(TunnelTags tags) {
            this.tags = tags;
        }

        public List getAggregateTags() {
            return aggregateTags;
        }

        public void setAggregateTags(List aggregateTags) {
            this.aggregateTags = aggregateTags;
        }

        public Map<Long, Double> getDps() {
            return dps;
        }

        public void setDps(Map<Long, Double> dps) {
            this.dps = dps;
        }

        public String getNodeUuid() {
            return nodeUuid;
        }

        public void setNodeUuid(String nodeUuid) {
            this.nodeUuid = nodeUuid;
        }
    }
}
