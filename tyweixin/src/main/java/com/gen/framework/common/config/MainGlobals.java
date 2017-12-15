package com.gen.framework.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainGlobals {

    @Value("${gen.rs.dir}")
    private String rsDir;

    @Value("${ty.oauth.redirectUri}")
    private String redirectUri;

    public String getRsDir() {
        return rsDir;
    }

    public void setRsDir(String rsDir) {
        this.rsDir = rsDir;
    }

    public String getRedirectUri() { return redirectUri; }

    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
