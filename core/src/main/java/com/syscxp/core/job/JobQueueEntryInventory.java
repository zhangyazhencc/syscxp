package com.syscxp.core.job;

import com.syscxp.header.search.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Create by DCY on 2017/12/13
 */
@Inventory(mappingVOClass = JobQueueEntryVO.class)
public class JobQueueEntryInventory {
    private long id;
    private long jobQueueId;
    private String jobQueueName;
    private String name;
    private String resourceUuid;
    private boolean uniqueResource;
    private String jobData;
    private JobState state;
    private Date takenDate;
    private long takenTimes;
    private Date inDate;
    private Date doneDate;
    private String errText;
    private String issuerManagementNodeId;
    private boolean restartable;

    public static JobQueueEntryInventory valueOf(JobQueueEntryVO vo){
        JobQueueEntryInventory inv = new JobQueueEntryInventory();

        inv.setId(vo.getId());
        inv.setJobQueueId(vo.getJobQueueId());
        inv.setJobQueueName(vo.getJobQueueVO().getName());
        inv.setName(vo.getName());
        inv.setResourceUuid(vo.getResourceUuid());
        inv.setUniqueResource(vo.isUniqueResource());
        inv.setJobData(vo.getJobData());
        inv.setState(vo.getState());
        inv.setTakenDate(vo.getTakenDate());
        inv.setTakenTimes(vo.getTakenTimes());
        inv.setInDate(vo.getInDate());
        inv.setDoneDate(vo.getDoneDate());
        inv.setErrText(vo.getErrText());
        inv.setIssuerManagementNodeId(vo.getIssuerManagementNodeId());
        inv.setRestartable(vo.isRestartable());

        return inv;
    }

    public static List<JobQueueEntryInventory> valueOf(Collection<JobQueueEntryVO> vos) {
        List<JobQueueEntryInventory> lst = new ArrayList<JobQueueEntryInventory>(vos.size());
        for (JobQueueEntryVO vo : vos) {
            lst.add(JobQueueEntryInventory.valueOf(vo));
        }
        return lst;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getJobQueueId() {
        return jobQueueId;
    }

    public void setJobQueueId(long jobQueueId) {
        this.jobQueueId = jobQueueId;
    }

    public String getJobQueueName() {
        return jobQueueName;
    }

    public void setJobQueueName(String jobQueueName) {
        this.jobQueueName = jobQueueName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceUuid() {
        return resourceUuid;
    }

    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
    }

    public boolean isUniqueResource() {
        return uniqueResource;
    }

    public void setUniqueResource(boolean uniqueResource) {
        this.uniqueResource = uniqueResource;
    }

    public String getJobData() {
        return jobData;
    }

    public void setJobData(String jobData) {
        this.jobData = jobData;
    }

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
    }

    public Date getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(Date takenDate) {
        this.takenDate = takenDate;
    }

    public long getTakenTimes() {
        return takenTimes;
    }

    public void setTakenTimes(long takenTimes) {
        this.takenTimes = takenTimes;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Date getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(Date doneDate) {
        this.doneDate = doneDate;
    }

    public String getErrText() {
        return errText;
    }

    public void setErrText(String errText) {
        this.errText = errText;
    }

    public String getIssuerManagementNodeId() {
        return issuerManagementNodeId;
    }

    public void setIssuerManagementNodeId(String issuerManagementNodeId) {
        this.issuerManagementNodeId = issuerManagementNodeId;
    }

    public boolean isRestartable() {
        return restartable;
    }

    public void setRestartable(boolean restartable) {
        this.restartable = restartable;
    }
}
