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

    public interface restMethod{
        public static final String OPEN_TSDB_QUERY = "/api/query";
    }

    public static class QueryCondition{
        private Long start;
        private Long end;
        private List<Query> queries;

        public QueryCondition() {
        }

        public QueryCondition(Long start, Long end, List<Query> queries) {
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

        public List<Query> getQueries() {
            return queries;
        }

        public void setQueries(List<Query> queries) {
            this.queries = queries;
        }
    }

    public static class Query{
        private String aggregator;
        private String metric;
        private Tags tags;

        public Query(String aggregator, String metric, Tags tags) {
            this.aggregator = aggregator;
            this.metric = metric;
            this.tags = tags;
        }

        public Query() {
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

        public Tags getTags() {
            return tags;
        }

        public void setTags(Tags tags) {
            this.tags = tags;
        }
    }

    public static class Tags {
        private String endpoint;
        private String ifname;
        private String tunnelid;

        public Tags(String endpoint, String ifname, String tunnelid) {
            this.endpoint = endpoint;
            this.ifname = ifname;
            this.tunnelid = tunnelid;
        }

        public Tags(String endpoint, String ifname) {
            this.endpoint = endpoint;
            this.ifname = ifname;
        }

        public Tags() {
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

    public static class CustomCondition {
        private String nodeUuid;
        private Map<String,Tags> tags;

        public String getNodeUuid() {
            return nodeUuid;
        }

        public void setNodeUuid(String nodeUuid) {
            this.nodeUuid = nodeUuid;
        }

        public Map<String, Tags> getTags() {
            return tags;
        }

        public void setTags(Map<String, Tags> tags) {
            this.tags = tags;
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

    public static class QueryResult{
        private String nodeUuid;
        private String metric;
        private Tags tags;
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

        public Tags getTags() {
            return tags;
        }

        public void setTags(Tags tags) {
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
