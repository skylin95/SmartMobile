package com.pansoft.app.smartmobile.view;

import java.io.Serializable;

public class TaskItembean implements Serializable {
	private String submitor;    //填报人
	private String taskSN;
	private String flowId;
	private String flowSN;
	private String nodeId;
	private String nodeName;
	private String djlx;
	private String vchrId;
	private String vchrKey;
	private String driverKey;
	private String taskName;
	private String originator;
	private String remark;
	private String amount;
	private String crtTime;
	private String crtDept;
	private String optMsg;
	private Boolean visibility;
	public TaskItembean(String taskName, String originator, String remark, String amount, String crtTime, String crtDept) {
		this.taskName = taskName;
		this.originator = originator;
		this.remark = remark;
		this.amount = amount;
		this.crtTime = crtTime;
		this.crtDept = crtDept;
	}

	public String getSubmitor() {
		return submitor;
	}

	public void setSubmitor(String submitor) {
		this.submitor = submitor;
	}

	public String getTaskSN() {
		return taskSN;
	}

	public void setTaskSN(String taskSN) {
		this.taskSN = taskSN;
	}

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowSN() {
		return flowSN;
	}

	public void setFlowSN(String flowSN) {
		this.flowSN = flowSN;
	}

	public String getVchrId() {
		return vchrId;
	}

	public String getDjlx() {
		return djlx;
	}

	public void setDjlx(String djlx) {
		this.djlx = djlx;
	}

	public void setVchrId(String vchrId) {
		this.vchrId = vchrId;
	}

	public String getVchrKey() {
		return vchrKey;
	}

	public void setVchrKey(String vchrKey) {
		this.vchrKey = vchrKey;
	}

	public String getDriverKey() {
		return driverKey;
	}

	public void setDriverKey(String driverKey) {
		this.driverKey = driverKey;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getpTaskName() {
		return taskName;
	}

	public void setpTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getpOriginator() {
		return originator;
	}

	public void setpOriginator(String originator) {
		this.originator = originator;
	}

	public String getpAmount() {
		return amount;
	}

	public void setpAmount(String amount) {
		this.amount = amount;
	}

	public String getpRemarks() {
		return remark;
	}

	public void setpRemarks(String remark) {
		this.remark = remark;
	}

	public String getpTime() {
		return crtTime;
	}

	public void setpTime(String crtTime) {
		this.crtTime = crtTime;
	}

	public String getpDepartment() {
		return crtDept;
	}

	public void setpDepartment(String crtDept) {
		this.crtDept = crtDept;
	}

	public String getOptMsg() {
		return optMsg;
	}

	public void setOptMsg(String optMsg) {
		this.optMsg = optMsg;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}
}