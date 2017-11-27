package com.gen.framework.common.beans;

public class SysUserBean {
    private String uname;
    private String upassword;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    @Override
    public String toString() {
        return "SysUserBean{" +
                "uname='" + uname + '\'' +
                ", upassword='" + upassword + '\'' +
                '}';
    }
}
