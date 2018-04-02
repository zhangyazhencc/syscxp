package com.syscxp.sdk;

import java.util.ArrayList;

/**
 * Project: syscxp
 * Package: com.syscxp.sdk
 * Date: 2017/12/26 14:06
 * Author: wj
 */
public abstract class QueryAction extends AbstractAction {
    @Param(required = true, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.util.List<String> conditions = new ArrayList<>();

    @Param(required = false)
    public java.lang.String uuid;

    @Param(required = false)
    public java.lang.Integer limit;

    @Param(required = false)
    public java.lang.Integer start;

    @Param(required = false)
    public Boolean count;

    @Param(required = false)
    public java.lang.String groupBy;

    @Param(required = false)
    public Boolean replyWithCount;

    @Param(required = false)
    public java.lang.String sortBy;

    @Param(required = false, validValues = {"asc","desc"}, nonempty = false, nullElements = false, emptyString = true, noTrim = false)
    public java.lang.String sortDirection;

    @Param(required = false)
    public java.util.List fields;

}
