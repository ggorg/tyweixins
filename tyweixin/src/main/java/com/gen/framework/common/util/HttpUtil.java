package com.gen.framework.common.util;


import com.gen.framework.common.exception.GenException;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * dengrg
 */

public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    public static String formPost(String url, Map<String, String> params) throws Exception {

        return post(url, null, params);
    }




    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String jsonPost(String url,String json)throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse(url);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request okReqeut = new Request.Builder().url(httpUrl).post(body).build();
        Call call = okHttpClient.newCall(okReqeut);

        Response response = call.execute();
        return retString(response, "jsonPost", HttpUrl.parse(url), json);
    }
    public static String post(String url, Map<String, String> urlParam, Map<String, String> params) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        HttpUrl httpUrl = HttpUrl.parse(url);
        FormBody.Builder builder = new FormBody.Builder();

        if (Objects.nonNull(urlParam) && !urlParam.isEmpty()) {
            HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
            urlParam.forEach(urlBuilder::addQueryParameter);
            httpUrl = urlBuilder.build();
        }


        if (params != null && !params.isEmpty()) {
            params.forEach(builder::add);
        }

        FormBody fb = builder.build();
        Request okReqeut = new Request.Builder().url(httpUrl).post(fb).build();
        Call call = okHttpClient.newCall(okReqeut);

        Response response = call.execute();

        return retString(response, "formPost", httpUrl, params);
    }


    public static String doGet(String url, Map<String, String> params) throws Exception {
        OkHttpClient okHttpClient = new OkHttpClient();
        String nUrl = url;
        if (params != null && !params.isEmpty()) {
            if (nUrl.indexOf("?") == -1) {
                nUrl += "?";
            } else {
                nUrl += "&";
            }
            Set<String> keys = params.keySet();
            for (String k : keys) {
                nUrl += k + "=" + params.get(k) + "&";
            }
            nUrl = nUrl.substring(0, nUrl.length() - 1);
        }
        Request okReqeut = new Request.Builder().url(nUrl).get().build();
        Call call = okHttpClient.newCall(okReqeut);

        Response response = call.execute();
        return retString(response, "doGet", HttpUrl.parse(url), params);

    }


    public static String toUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        } else if (!url.contains("http:")) {
            return "http:" + url;
        }
        return url;
    }

    private static String retString(Response response, String methodStr, HttpUrl url, Object params) throws Exception {
        try {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("HttpUtil->");
                sb.append(methodStr);
                sb.append("(");
                sb.append(url);
                sb.append(",");
                sb.append(params);
                sb.append(")->");
                sb.append(response.code());
                sb.append("->");
                sb.append(response.message());
                sb.append("->请求失败");
                throw new GenException(sb.toString());
            }
        } finally {
            response.close();
        }

    }





}
