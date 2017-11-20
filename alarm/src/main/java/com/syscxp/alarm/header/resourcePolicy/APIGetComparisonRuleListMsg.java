package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;

@Action(category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"read"})
public class APIGetComparisonRuleListMsg extends APISyncCallMessage{

}
