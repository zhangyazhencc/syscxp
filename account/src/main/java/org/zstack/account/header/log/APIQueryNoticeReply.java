package org.zstack.account.header.log;

import org.zstack.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryNoticeReply {

    private List<NoticeInventory> inventories;
}
