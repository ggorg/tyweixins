package com.gen.framework.common.beans;

public class SysMenuBean {
    private String mName;
    private Integer mParentId;
    private Integer mSort;
    private String mUrl;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public Integer getmParentId() {
        return mParentId;
    }

    public void setmParentId(Integer mParentId) {
        this.mParentId = mParentId;
    }

    public Integer getmSort() {
        return mSort;
    }

    public void setmSort(Integer mSort) {
        this.mSort = mSort;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
