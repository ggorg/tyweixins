package com.gen.framework.common.vo;

public class ResponseVO {
    private Integer reCode;
    private String reMsg;
    private Object data;

    public ResponseVO(Integer reCode, String reMsg, Object data) {
        this.reCode = reCode;
        this.reMsg = reMsg;
        this.data = data;
    }

    public ResponseVO() {
    }

    public Integer getReCode() {
        return reCode;
    }

    public void setReCode(Integer reCode) {
        this.reCode = reCode;
    }

    public String getReMsg() {
        return reMsg;
    }

    public void setReMsg(String reMsg) {
        this.reMsg = reMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
