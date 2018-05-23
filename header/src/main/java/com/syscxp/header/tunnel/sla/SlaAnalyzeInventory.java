package com.syscxp.header.tunnel.sla;

import com.syscxp.header.billing.ProductType;
import com.syscxp.header.search.Inventory;
import com.syscxp.header.tunnel.monitor.SpeedTestTunnelVO;
import com.syscxp.header.tunnel.tunnel.TunnelInventory;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-14.
 * @Description: .
 */
@Inventory(mappingVOClass = SlaAnalyzeVO.class)
public class SlaAnalyzeInventory {
    private SlaLevel level;
    private Timestamp start;
    private Timestamp end;
    private long duration;


    public static SlaAnalyzeInventory valueOf(SlaAnalyzeVO vo) {
        SlaAnalyzeInventory inv = new SlaAnalyzeInventory();
        inv.setLevel(vo.getLevel());
        inv.setStart(vo.getStart());
        inv.setEnd(vo.getEnd());
        inv.setDuration(vo.getDuration());

        return inv;
    }

    public static List<SlaAnalyzeInventory> valueOf(Collection<SlaAnalyzeVO> vos) {
        List<SlaAnalyzeInventory> lst = new ArrayList<SlaAnalyzeInventory>(vos.size());
        for (SlaAnalyzeVO vo : vos) {
            lst.add(SlaAnalyzeInventory.valueOf(vo));
        }
        return lst;
    }


    public SlaLevel getLevel() {
        return level;
    }

    public void setLevel(SlaLevel level) {
        this.level = level;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

}
