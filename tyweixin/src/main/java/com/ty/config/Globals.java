package com.ty.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Globals {

    @Value("${ty.oauth.jump.url}")
    private String  oauthJumUrl;

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
