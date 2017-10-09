package com.syscxp.account.header.log;

import com.syscxp.header.search.Inventory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = NoticeVO.class)
public class NoticeInventory {
    private String uuid;
    private String title;
    private String link;
    private String status;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp lastOpDate;
    private Timestamp createDate;

    public static NoticeInventory valueOf(NoticeVO vo) {
        NoticeInventory inv = new NoticeInventory();
        inv.setUuid(vo.getUuid());
        inv.setTitle(vo.getTitle());
        inv.setLink(vo.getLink());
        inv.setStatus(vo.getStatus().toString());
        inv.setStartTime(vo.getStartTime());
        inv.setEndTime(vo.getEndTime());
        inv.setLastOpDate(vo.getLastOpDate());
        inv.setCreateDate(vo.getCreateDate());
        return inv;
    }

    public static List<NoticeInventory> valueOf(Collection<NoticeVO> vos) {
        List<NoticeInventory> lst = new ArrayList<NoticeInventory>(vos.size());
        for (NoticeVO vo : vos) {
            lst.add(NoticeInventory.valueOf(vo));
        }
        return lst;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getLastOpDate() {
        return lastOpDate;
    }

    public void setLastOpDate(Timestamp lastOpDate) {
        this.lastOpDate = lastOpDate;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
