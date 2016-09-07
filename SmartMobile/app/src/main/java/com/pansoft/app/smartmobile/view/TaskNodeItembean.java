package com.pansoft.app.smartmobile.view;

/**
 * Created by eunji on 2016/8/23.
 */
public class TaskNodeItembean {
    private String approverId;
    private String approver;
    private String approveOpinion;
    private String approveTime;
    private int nodeState;

    public TaskNodeItembean() {

    }

    public TaskNodeItembean(String approver, String approveOpinion, String approveTime, int nodeState, String optUid) {
        this.approverId = optUid;
        this.approver = approver;
        this.approveOpinion = approveOpinion;
        this.approveTime = approveTime;
        this.nodeState = nodeState;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String optUid) {
        this.approverId = optUid;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveOpinion() {
        return approveOpinion;
    }

    public void setApproveOpinion(String approveOpinion) {
        this.approveOpinion = approveOpinion;
    }

    public int getNodeState() {
        return nodeState;
    }

    public void setNodeState(int nodeState) {
        this.nodeState = nodeState;
    }
}
