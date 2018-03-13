package com.ty.entity;

public class TyActivityPropagate {
    private Integer id;
    private Integer actId;
    private String topTitle;
    private String topImgSrc;
    private String mainDesc;
    private String actJsonStr;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActId() {
        return actId;
    }

    public void setActId(Integer actId) {
        this.actId = actId;
    }

    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public String getTopImgSrc() {
        return topImgSrc;
    }

    public void setTopImgSrc(String topImgSrc) {
        this.topImgSrc = topImgSrc;
    }

    public String getMainDesc() {
        return mainDesc;
    }

    public void setMainDesc(String mainDesc) {
        this.mainDesc = mainDesc;
    }

    public String getActJsonStr() {
        return actJsonStr;
    }

    public void setActJsonStr(String actJsonStr) {
        this.actJsonStr = actJsonStr;
    }
}
