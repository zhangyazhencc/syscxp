package com.syscxp.core.job;

import com.syscxp.header.managementnode.ManagementNodeVO;
import com.syscxp.header.vo.ForeignKey;
import com.syscxp.header.vo.ForeignKey.ReferenceOption;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
public class JobQueueEntryVO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column
    private long id;
    
    @Column
    @ForeignKey(parentEntityClass = JobQueueVO.class, onDeleteAction = ReferenceOption.CASCADE)
    private long jobQueueId;

    @Column
    private String name;

    @Column
    private String resourceUuid;

    @Column
    private boolean uniqueResource;

    @Column
    private String jobData;

    @Column
    @Enumerated(EnumType.STRING)
    private JobState state;

    @Column
    private Date takenDate;

    @Column
    private long takenTimes;

    @Column
    private Date inDate;
    
    @Column
    private Date doneDate;
    
    @Column
    private byte[] context;

    @Column
    private String owner;
    
    @Column
    private String errText;

    @Column
    @ForeignKey(parentEntityClass = ManagementNodeVO.class, onDeleteAction = ReferenceOption.SET_NULL)
    private String issuerManagementNodeId;

    @Column
    private boolean restartable;

    public JobQueueEntryVO() {
        this.state = JobState.Pending;
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

    public JobState getState() {
        return state;
    }

    public void setState(JobState state) {
        this.state = state;
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

    public byte[] getContext() {
        return context;
    }

    public void setContext(byte[] context) {
        this.context = context;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRestartable() {
        return restartable;
    }

    public void setRestartable(boolean restartable) {
        this.restartable = restartable;
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

    public String getJobData() {
        return jobData;
    }

    public void setJobData(String jobData) {
        this.jobData = jobData;
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
}
