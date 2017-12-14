package com.ty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Globals {

    @Value("${ty.oauth.jump.url}")
    private String  oauthJumUrl;

    @Value("${ty.api.url.searchBalanceUrl}")
    private String searchBalanceUrl;

    @Value("${ty.api.url.searchBalanceDetailUrl}")
    private String searchBalanceDetailUrl;

    @Value("${ty.api.url.vaildCodeUrl}")
    private String vaildCodeUrl;
    @Value("${ty.telphone.prefix}")
    private String tyTelPrefix;

    @Value("${ty.api.url.searchVoucherUrl}")
    private String searchVoucherUrl;

    @Value("${ty.api.url.pullRedPacketUrl}")
    private String pullRedPacketUrl;

    @Value("${ty.api.url.openRedPacketUrl}")
    private String openRedPacketUrl;

    public String getOpenRedPacketUrl() {
        return openRedPacketUrl;
    }

    public void setOpenRedPacketUrl(String openRedPacketUrl) {
        this.openRedPacketUrl = openRedPacketUrl;
    }

    public String getPullRedPacketUrl() {
        return pullRedPacketUrl;
    }

    public void setPullRedPacketUrl(String pullRedPacketUrl) {
        this.pullRedPacketUrl = pullRedPacketUrl;
    }

    public String getSearchBalanceUrl() {
        return searchBalanceUrl;
    }

    public void setSearchBalanceUrl(String searchBalanceUrl) {
        this.searchBalanceUrl = searchBalanceUrl;
    }

    public String getSearchBalanceDetailUrl() {
        return searchBalanceDetailUrl;
    }

    public void setSearchBalanceDetailUrl(String searchBalanceDetailUrl) {
        this.searchBalanceDetailUrl = searchBalanceDetailUrl;
    }

    public String getVaildCodeUrl() {
        return vaildCodeUrl;
    }

    public void setVaildCodeUrl(String vaildCodeUrl) {
        this.vaildCodeUrl = vaildCodeUrl;
    }

    public String getTyTelPrefix() {
        return tyTelPrefix;
    }

    public void setTyTelPrefix(String tyTelPrefix) {
        this.tyTelPrefix = tyTelPrefix;
    }

    public String getSearchVoucherUrl() {
        return searchVoucherUrl;
    }

    public void setSearchVoucherUrl(String searchVoucherUrl) {
        this.searchVoucherUrl = searchVoucherUrl;
    }

    private Map<String,String> mapOauthJumUrl;
    public String getOauthJumUrl() {
        return oauthJumUrl;
    }

    public void setOauthJumUrl(String oauthJumUrl) {
        this.oauthJumUrl = oauthJumUrl;
    }
    public String getOauthJumUrlByKey(String key){
        if(mapOauthJumUrl==null){
            mapOauthJumUrl=new HashMap();
            String[] jumpurls=oauthJumUrl.split(",");
            for(String j:jumpurls){
                String[] kv=j.split(":");
                mapOauthJumUrl.put(kv[0],kv[1]);
            }
        }
        return mapOauthJumUrl.get(key);
    }
}
