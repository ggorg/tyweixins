package com.ty.enums;

public enum ActEnum {
    act1("1","翼支付交易明细查询"),
    act2("2","翼支付账户余额查询"),
    act3("3","用户已有经红查询"),
    act4("4","红包活动列表查询"),
    act5("5","用户代金券信息查询"),
    act6("6","短信发送");
    private String code;
    private String name;

    ActEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static String getNameByCode(String code) {
        for (ActEnum dt : ActEnum.values()) {
            if (dt.code.equals(code)) {
                return dt.getName();
            }
        }
        return null;
    }
}
