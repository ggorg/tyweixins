package com.ty.entity;

public class TyBalanceTradeDetail {
    private String ourTransNo;
    private String payTransNo;
    private String orderDate;
    private String trnanAmt;
    private String mchntName;

    public String getOurTransNo() {
        return ourTransNo;
    }

    public void setOurTransNo(String ourTransNo) {
        this.ourTransNo = ourTransNo;
    }

    public String getPayTransNo() {
        return payTransNo;
    }

    public void setPayTransNo(String payTransNo) {
        this.payTransNo = payTransNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTrnanAmt() {
        return trnanAmt;
    }

    public void setTrnanAmt(String trnanAmt) {
        this.trnanAmt = trnanAmt;
    }

    public String getMchntName() {
        return mchntName;
    }

    public void setMchntName(String mchntName) {
        this.mchntName = mchntName;
    }
}
