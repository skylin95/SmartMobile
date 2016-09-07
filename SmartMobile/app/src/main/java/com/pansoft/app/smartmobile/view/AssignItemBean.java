package com.pansoft.app.smartmobile.view;

public class AssignItemBean {
    private String sn;
    private String flowId;
    private String flowName;
    private String strId;
    private String strName;
    private String strBmbh;
    private String strBmmc;
    private String strProjId;
    private String strProjName;
    private String wtrId;
    private String wtrName;
    private String wtrBmbh;
    private String wtrBmmc;
    private String wtrProjId;
    private String wtrProjName;
    private String startTime;
    private String endTime;
    private boolean diable;
    
    public AssignItemBean() {
    	
    }
    
    public AssignItemBean(String flowId, String wtrId, String strId, String startTime, String endTime) {
    	this.setFlowId(flowId);
    	this.setWtrId(wtrId);
    	this.setStrId(strId);
    	this.setStartTime(startTime);
    	this.setEndTime(endTime);
    }
    
	public String getSn() {
		return sn;
	}
	
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	public String getFlowId() {
		return flowId;
	}
	
	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	
	public String getFlowName() {
		return flowName;
	}
	
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	
	public String getStrId() {
		return strId;
	}
	
	public void setStrId(String strId) {
		this.strId = strId;
	}
	
	public String getStrName() {
		return strName;
	}
	
	public void setStrName(String strName) {
		this.strName = strName;
	}
	
	public String getStrBmbh() {
		return strBmbh;
	}
	
	public void setStrBmbh(String strBmbh) {
		this.strBmbh = strBmbh;
	}
	
	public String getStrBmmc() {
		return strBmmc;
	}
	
	public void setStrBmmc(String strBmmc) {
		this.strBmmc = strBmmc;
	}
	
	public String getStrProjId() {
		return strProjId;
	}
	
	public void setStrProjId(String strProjId) {
		this.strProjId = strProjId;
	}
	
	public String getStrProjName() {
		return strProjName;
	}
	
	public void setStrProjName(String strProjName) {
		this.strProjName = strProjName;
	}
	
	public String getWtrId() {
		return wtrId;
	}
	
	public void setWtrId(String wtrId) {
		this.wtrId = wtrId;
	}
	
	public String getWtrName() {
		return wtrName;
	}
	
	public void setWtrName(String wtrName) {
		this.wtrName = wtrName;
	}
	
	public String getWtrBmbh() {
		return wtrBmbh;
	}
	
	public void setWtrBmbh(String wtrBmbh) {
		this.wtrBmbh = wtrBmbh;
	}
	
	public String getWtrBmmc() {
		return wtrBmmc;
	}
	
	public void setWtrBmmc(String wtrBmmc) {
		this.wtrBmmc = wtrBmmc;
	}
	
	public String getWtrProjId() {
		return wtrProjId;
	}
	
	public void setWtrProjId(String wtrProjId) {
		this.wtrProjId = wtrProjId;
	}
	
	public String getWtrProjName() {
		return wtrProjName;
	}
	
	public void setWtrProjName(String wtrProjName) {
		this.wtrProjName = wtrProjName;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public boolean isDiable() {
		return diable;
	}
	
	public void setDiable(boolean diable) {
		this.diable = diable;
	}
}
