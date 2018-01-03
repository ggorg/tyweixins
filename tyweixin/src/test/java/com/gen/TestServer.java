package com.gen;

import com.ty.util.TydicDES;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public abstract class TestServer {

	/*public static void main(String[] args) {
		// http
		sendPost("http://www.internetfinancesystem.com:80/coupon/webserver/get","TQgVtn6MPQ/IJywOkWmpewQQrcpTwGaVEZGj29i8G0yV+ihQI7UJa6JUYBWgAxGw");
		// https
		startHttpsPost();
	}*/
    public static void main(String[] args) {
//		sendHttpPost();
        startHttpsPost();
//		String strRequest1="{\"pay_user\":\"15393944645\",\"act_code\":\"1\"}";
//		String strRequest2="{\"pay_user\":\"15393944645\",\"act_code\":\"2\"}";
//		String strRequest3="{\"pay_user\":\"15393944645\",\"act_code\":\"3\"}";
//		String strRequest4="{'pay_user':'15393944645','act_code':'4','efCampaignId':'1156','packetId':'192374372','packetValue':'100','seqCode':'27364326432943947'}";
//		String strRequest5="{\"pay_user\":\"15393944645\",\"act_code\":\"5\"}";
//		String strRequest6="{\"phoneNo\":\"15393944645\",\"message\":\"杨涛\",\"act_code\":\"6\"}";
//		String strRequest7="{\"pay_user\":\"15331555409\",\"act_code\":\"7\"}";
        try{
//		String req1 = TydicDES.encodeValue(strRequest1);
//		String req2 = TydicDES.encodeValue(strRequest2);
//		String req3 = TydicDES.encodeValue(strRequest3);
//		String req4 = TydicDES.encodeValue(strRequest4);
//		String req5 = TydicDES.encodeValue(strRequest5);
//		String req6 = TydicDES.encodeValue(strRequest6);
//		String req7 = TydicDES.encodeValue(strRequest7);
//		System.out.println("--------------加密----------------");
//		System.out.println("请求报文1："+req1);
//		System.out.println("请求报文2："+req2);
//		System.out.println("请求报文3："+req3);
//		System.out.println("请求报文4："+req4);
//		System.out.println("请求报文5："+req5);
//		System.out.println("请求报文6："+req6);
//		System.out.println("请求报文7："+req7);
//		System.out.println("--------------解密----------------");
//		System.out.println("请求报文1："+TydicDES.decodedecodeValue(req1));
//		System.out.println("请求报文2："+TydicDES.decodedecodeValue(req2));
//		System.out.println("请求报文3："+TydicDES.decodedecodeValue(req3));
//		System.out.println("请求报文4："+TydicDES.decodedecodeValue(req4));
//		System.out.println("请求报文5："+TydicDES.decodedecodeValue(req5));
//		System.out.println("请求报文6："+TydicDES.decodedecodeValue(req6));
//		System.out.println("请求报文7："+TydicDES.decodedecodeValue(req7));
        }catch(Exception e){
            e.printStackTrace();
        }
//		Base64();
    }

    private static void Base64(){
        String str = "CE85ZOVOEya5l2qrF3k1i4FwEpdZzPDQ81utg8VkQ9iSfC/z1+cOg/k0+PxIgW6HZZCVT4m6bo/0\r\nqF7MVqs7Kf9Txuz3UDF2HfxL5jecufwFsD/LuA/tHfAEgnNv7Riax8GkILCSBz4QfT6jn22TH05s\r\nJ/Rg2ontSzhrtMZZp5lObi7x5J12BtAVIwK/ABHj";
        String endStr = Base64.getEncoder().encodeToString(str.getBytes());
        System.out.println("Base64加密："+endStr);
        byte[] bytes = Base64.getDecoder().decode(endStr);
        String st = new String(bytes);
        System.out.println("Base64解密："+st);
        if(str.equals(st)){
            System.out.println("解密成功");
        }
    }

    private static void sendHttpPost(){
        //测试时使用自己的open_id
        //String reqUrl = "http://222.221.16.170/coupon/webserver/get";
        String reqUrl ="http://www.internetfinancesystem.com/coupon/webserver/get";
//		String reqUrl = "http://localhost:8010/webserver/get";
//		String reqUrl="https://www.internetfinancesystem.com:80/coupon/webserver/get";
//		String strRequest1="{\"pay_user\":\"15393944645\",\"act_code\":\"1\"}";
//		String strRequest6="{\"phoneNo\":\"18088262389\",\"message\":\"杨涛的测试短信\",\"act_code\":\"6\"}";
        String strRequest3="{\"pay_user\":\"15393944645\",\"act_code\":\"3\"}";
        // 红包充值
        String strRequest4="{'pay_user':'18855952542','act_code':'4','efCampaignId':'1260','packetId':'192374372','packetValue':'100','seqCode':'27364326432943947'}";
        try{
            String strParm=TydicDES.encodeValue(strRequest4);
            String resultStr = sendPost(reqUrl, strParm);
            System.out.println("--------------返回密文:"+resultStr);
            System.out.println("----解密后的结果:"+TydicDES.decodedecodeValue(resultStr));
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public static String sendPost(String strURL, String params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
            out.append(params);
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error"; // 自定义错误信息
    }

    public static String sendHtpps(String a,String url) {
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn;
        try {
            trustAllHosts();
            URL realUrl = new URL(url);
            //通过请求地址判断请求类型(http或者是https)
            if (realUrl.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
                https.setHostnameVerifier(DO_NOT_VERIFY);
                conn = https;
            } else {
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            // 设置通用的请求属性
            conn.setConnectTimeout(8000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "text/plain;charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(a);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {// 使用finally块来关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public static void startHttpsPost() {
        /*String url="https://127.0.0.1:8080/openNozzle";
        String a = "aaaaaa";
        */
        String reqUrl="https://222.221.16.170:80/coupon/webserver/get";
//		String strRequest1="{\"phoneNo\":\"18088262389\",\"message\":\"测试一下\",\"act_code\":\"6\"}";
        String strRequest2="{\"pay_user\":\"15393944645\",\"act_code\":\"2\"}";
        // 红包充值
        String strRequest4="{'pay_user':'17787019595','act_code':'4','efCampaignId':'1305','packetId':'192374332','packetValue':'550','seqCode':'27364326432943947'}";
        try{
            String strParm=TydicDES.encodeValue(strRequest4);
            String resultStr = sendHtpps(strParm,reqUrl);
            System.out.println("--------------返回密文:"+resultStr);
            System.out.println("----解密后的结果:"+TydicDES.decodedecodeValue(resultStr));
        }catch(Exception e){
            e.printStackTrace();
        }

        System.out.println();
    }

    public static int compare_date(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }
}
