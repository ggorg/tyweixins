package com.ty.entity;

public class TyUser {
    private Integer id;
    private String tyTelphone;
    private String tyOpenId;
    private Boolean tyDisabled;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTyTelphone() {
        return tyTelphone;
    }

    public void setTyTelphone(String tyTelphone) {
        this.tyTelphone = tyTelphone;
    }

    public String getTyOpenId() {
        return tyOpenId;
    }

    public void setTyOpenId(String tyOpenId) {
        this.tyOpenId = tyOpenId;
    }

    public Boolean getTyDisabled() {
        return tyDisabled;
    }

    public void setTyDisabled(Boolean tyDisabled) {
        this.tyDisabled = tyDisabled;
    }
}
