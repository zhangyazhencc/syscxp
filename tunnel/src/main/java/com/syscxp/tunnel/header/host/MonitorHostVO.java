package com.syscxp.tunnel.header.host;

import com.syscxp.header.core.encrypt.DECRYPT;
import com.syscxp.header.core.encrypt.ENCRYPTParam;
import com.syscxp.header.host.HostEO;
import com.syscxp.header.vo.EO;
import com.syscxp.header.host.HostVO;

import javax.persistence.*;

@Entity
@Table
@PrimaryKeyJoinColumn(name="uuid", referencedColumnName="uuid")
@EO(EOClazz = HostEO.class, needView = false)
public class MonitorHostVO extends HostVO {
    @Column
    private String username;

    @Column
    @ENCRYPTParam
    private String password;

    @Column
    private Integer sshPort;

    public MonitorHostVO() {
    }

    public MonitorHostVO(HostVO vo) {
        super(vo);
    }
    

    @DECRYPT
    public String getPassword() {
        return password;
    }

//    @ENCRYPT
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }
}

