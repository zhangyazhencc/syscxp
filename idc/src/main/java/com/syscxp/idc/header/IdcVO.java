package com.syscxp.idc.header;

import com.syscxp.header.vo.EO;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table
@EO(EOClazz = IdcEO.class)
public class IdcVO extends IdcAO {
}