package com.Xls2Json.model;

public class RMOD {
	String mrbtsid;
	String rModId;
	String prodCode;
	String moduleLocation;
	String climateControlProfiling;
	public String getMrbtsid() {
		return mrbtsid;
	}
	public void setMrbtsid(String string) {
		this.mrbtsid = string;
	}
	public String getrModId() {
		return rModId;
	}
	public void setrModId(String rModId) {
		this.rModId = rModId;
	}
	public String getProdCode() {
		return prodCode;
	}
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}
	public String getModuleLocation() {
		return moduleLocation;
	}
	public void setModuleLocation(String moduleLocation) {
		this.moduleLocation = moduleLocation;
	}
	@Override
	public String toString() {
		return "RMOD [mrbtsid=" + mrbtsid + ", rModId=" + rModId + ", prodCode=" + prodCode + ", moduleLocation="
				+ moduleLocation + ", climateControlProfiling=" + climateControlProfiling + "]";
	}
	public String getClimateControlProfiling() {
		return climateControlProfiling;
	}
	public void setClimateControlProfiling(String climateControlProfiling) {
		this.climateControlProfiling = climateControlProfiling;
	}

}
