package com.syscxp.header.tunnel.sla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-05-10.
 * @Description: .
 */
public class Test {
    @Autowired
    private static RestTemplate restTemplate;

    public static void main(String[] args) {

        List<SlaRange> curRanges = new ArrayList<>();
        curRanges.add(new SlaRange(100,200));

        List<SlaRange> hisRanges = new ArrayList<>();
        hisRanges.add(new SlaRange(150,170));
        hisRanges.add(new SlaRange(110,130));
        hisRanges.add(new SlaRange(190,250));

        listIte(hisRanges,curRanges);
    }

    private static void listIte(List<SlaRange> hisRanges,List<SlaRange> curRanges){
        long start;
        long end;
        for(SlaRange his : hisRanges){
            ListIterator<SlaRange> curIter = curRanges.listIterator();
            while (curIter.hasNext()){
                SlaRange curRange = curIter.next();

                start = curRange.getStart();
                end = curRange.getEnd();
                if (end < his.getStart() || start > his.getEnd()) {
                    continue;
                } else if (start > his.getStart() && end < his.getEnd()) {
                    curIter.remove();
                    continue;
                } else if (start < his.getStart() && end > his.getEnd()) {
                    curIter.remove();
                    curIter.add(new SlaRange(start, his.getStart()));
                    curIter.add(new SlaRange(his.getEnd(), end));
                    continue;
                } else if (start < his.getStart() && end > his.getStart() && end < his.getEnd()) {
                    curIter.remove();
                    curIter.add(new SlaRange(start, his.getStart()));
                    continue;
                } else if (start > his.getStart() && start < his.getEnd() && end > his.getEnd()) {
                    curIter.remove();
                    curIter.add(new SlaRange(his.getEnd(), end));
                    continue;
                }
            }
        }

        for(SlaRange range :curRanges)
            System.out.println(String.format("start: %s, end: %s",range.getStart(),range.getEnd()));
    }

    static class SlaRange {
        long start;
        long end;

        public SlaRange(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }
}
