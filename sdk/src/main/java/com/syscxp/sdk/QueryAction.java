package com.syscxp.sdk;

import java.util.ArrayList;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:06
 * Author: wj
 */
public abstract class QueryAction extends AbstractAction {
    public java.util.List<String> conditions = new ArrayList<>();

    public Integer limit;

    public Integer start;

    public Boolean count;

    public String groupBy;

    public Boolean replyWithCount;

    public String sortBy;

    public String sortDirection;

    public java.util.List fields;

}
