package com.pansoft.app.smartmobile.view;

/**
 * Created by eunji on 2016/8/26.
 */
public class ResultItembean {
    private String resMsg;
    private String vchrKey;

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getVchrKey() {
        return vchrKey;
    }

    public void setVchrKey(String vchrKey) {
        this.vchrKey = vchrKey;
    }

    public ResultItembean(String vchrKey, String resMsg) {
        this.resMsg = resMsg;
        this.vchrKey = vchrKey;
    }
}
