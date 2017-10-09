package com.syscxp.core.config;

import com.syscxp.header.search.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Inventory(mappingVOClass = GlobalConfigVO.class)
public class GlobalConfigInventory {
	private String name;
	private String category;
	private String description;
	private String defaultValue;
	private String value;

	public static GlobalConfigInventory valueOf(GlobalConfigVO vo) {
		GlobalConfigInventory inv = new GlobalConfigInventory();
		inv.setName(vo.getName());
		inv.setDefaultValue(vo.getDefaultValue());
		inv.setDescription(vo.getDescription());
		inv.setCategory(vo.getCategory());
		inv.setValue(vo.getValue());
		return inv;
	}

    public static List<GlobalConfigInventory> valueOf(Collection<GlobalConfigVO> vos) {
        List<GlobalConfigInventory> invs = new ArrayList<GlobalConfigInventory>();
        for (GlobalConfigVO vo : vos) {
            invs.add(valueOf(vo));
        }
        return invs;
    }
	
	public static GlobalConfigInventory valueOf(GlobalConfig c) {
		GlobalConfigInventory inv = new GlobalConfigInventory();
		inv.setName(c.getName());
		inv.setDefaultValue(c.getDefaultValue());
		inv.setDescription(c.getDescription());
		inv.setCategory(c.getCategory());
		inv.setValue(c.value());
		return inv;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
