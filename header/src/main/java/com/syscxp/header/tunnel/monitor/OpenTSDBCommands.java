package com.syscxp.header.tunnel.monitor;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: OpenTSDB数据查询.
 */
public class OpenTSDBCommands {
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
        private tags tags;

        public Query(String aggregator, String metric, OpenTSDBCommands.tags tags) {
            this.aggregator = aggregator;
            this.metric = metric;
            this.tags = tags;
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

        public OpenTSDBCommands.tags getTags() {
            return tags;
        }

        public void setTags(OpenTSDBCommands.tags tags) {
            this.tags = tags;
        }
    }

    public static class tags{
        private String endpoint;
        private String ifname;

        public tags(String endpoint, String ifname) {
            this.endpoint = endpoint;
            this.ifname = ifname;
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
        private String metric;
        private tags tags;
        private List agggregateTags;
        private List<Map<Long,Object>> dps;

        public String getMetric() {
            return metric;
        }

        public void setMetric(String metric) {
            this.metric = metric;
        }

        public OpenTSDBCommands.tags getTags() {
            return tags;
        }

        public void setTags(OpenTSDBCommands.tags tags) {
            this.tags = tags;
        }

        public List getAgggregateTags() {
            return agggregateTags;
        }

        public void setAgggregateTags(List agggregateTags) {
            this.agggregateTags = agggregateTags;
        }

        public List<Map<Long, Object>> getDps() {
            return dps;
        }

        public void setDps(List<Map<Long, Object>> dps) {
            this.dps = dps;
        }
    }
}
