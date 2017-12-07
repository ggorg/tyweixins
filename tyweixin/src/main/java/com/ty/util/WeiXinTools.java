package com.ty.util;

public class WeiXinTools {
	/*private static String ticket;
	private static Map<String,String> userLocation=new HashMap<String, String>();
	private static Map<String,String> userJumpUrl=new HashMap<String, String>();
	public static void initJsapiticket(String accessToken){
		InputStream is=null;
		try {
			HttpResponse response=	HttpUtil.formGet(ConcatString.HTTP_WX_KEY, "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi", null);
			if(response.getStatusLine().getStatusCode()== 200 || response.getStatusLine().getStatusCode()==302){
				is=response.getEntity().getContent();
				JSONObject jsonobj=JSONObject.fromObject(IOUtils.toString(is));
				
				if(jsonobj.getString("errmsg").equals("ok")){
					ticket=jsonobj.getString("ticket");
				}
			}else{
				System.out.println("获取ticket失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(is!=null)is.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	*//**
	 * 
	 * @param url  当前页面的url 比如：http://wwww.youryour.cn/TestChat/test 或者http://wwww.youryour.cn/TestChat/test?xx=xx
	 * @param jsApiList 想调什么函数就加什么函数,并且以逗号隔开，比如openLocation,getLocation,closeWindow
	 * @return
	 *//*
	public static String initJssdk(String url,String jsApiList){
		
		String timestamp=(System.currentTimeMillis()/1000)+"";
		String nonceStr=RandomStringUtils.randomAlphabetic(16);
		StringBuilder builder=new StringBuilder();
		builder.append("jsapi_ticket=").append(ticket).append("&");
		builder.append("noncestr=").append(nonceStr).append("&");
		builder.append("timestamp=").append(timestamp).append("&");
		builder.append("url=").append(url);
		String shastr=DigestUtils.shaHex(builder.toString());
		builder=new StringBuilder();
		builder.append("<script type=\"text/javascript\" src=\""+ProperFileUtils.getInstance().getProperty("winxin_js_sdk")+"\"></script>");
		builder.append("<script type=\"text/javascript\">");
		builder.append("wx.config({");
		builder.append("debug:").append("false").append(",");
		builder.append("appId:'").append(ConcatString.APPID).append("',");
		builder.append("timestamp:").append(timestamp).append(",");
		builder.append("nonceStr:'").append(nonceStr).append("',");
		builder.append("signature:'").append(shastr).append("',");
		builder.append("jsApiList:").append(Arrays.asList(jsApiList).toString().replaceAll("([a-zA-Z]+)", "'$1'"));
		builder.append("});");
	*//*	wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: 'wxd5ba1ab308c1908b', // 必填，公众号的唯一标识
		    timestamp:<%=timestamp%> , // 必填，生成签名的时间戳
		    nonceStr: '<%=nonce%>', // 必填，生成签名的随机串
		    signature: '<%=shastr%>',// 必填，签名，见附录1
		    jsApiList: ['openLocation','getLocation','closeWindow'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});*//*
		builder.append("</script>");



		return builder.toString();
	}
	public static void put(String openid,String location){
		userLocation.put(openid, location);
	}
	public static String getLocation(String openid){
		if(userLocation.containsKey(openid)){
			return userLocation.get(openid);
		}
		return null;
	}
	public static void removeAll(){
		userLocation.clear();
	}
	public static void putJumpUrl(String openid,String jumpurl){
		userJumpUrl.put(openid, jumpurl);
	}
	public static String getJumpUrl(String openid){
		if(userJumpUrl.containsKey(openid)){
			return userJumpUrl.get(openid);
		}
		return null;
	}
	public static void removeJumpUrl(String openid){
		if(userJumpUrl.containsKey(openid)){
			userJumpUrl.remove(openid);
		}
		
	}
	public static String getCookieValue(){
		try {
			Cookie[] cookies=ServletActionContext.getRequest().getCookies();
			if(cookies!=null && cookies.length>0){
				for(Cookie c:cookies){
					if(c.getName().equals("cmdStr")){
						String reverseString=new StringBuilder().append(c.getValue()).reverse().toString();
					
						String realString=new StringBuilder().append(new  org.w3c.tools.codec.Base64Decoder(reverseString).processString()).reverse().toString();
						return realString;
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void setEncodeCookie(String value){
		try {
			
			StringBuilder builder=new StringBuilder();
			builder.append(value);
			builder.reverse();
			String base64=new StringBuilder().append(new  org.w3c.tools.codec.Base64Encoder(builder.toString()).processString()).reverse().toString();
			System.out.println("i am cookie:"+base64);
			Cookie cookie=new Cookie("cmdStr",base64);
			ServletActionContext.getResponse().addCookie(cookie);		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void main(String[] args) throws Base64FormatException {
System.out.println(new  org.w3c.tools.codec.Base64Encoder("123456789").processString());
System.out.println(new  org.w3c.tools.codec.Base64Decoder("MTIzNDU2Nzg5").processString());
	}*/
}
