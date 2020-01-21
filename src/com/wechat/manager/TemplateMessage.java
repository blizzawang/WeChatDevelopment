package com.wechat.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.wechat.service.WxService;

import net.sf.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TemplateMessage {
	
	//设置行业
	public static void setIndustry() {
		String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=" + WxService.getTokenIfExpired();
		String json = "{\r\n" + 
				"    \"industry_id1\":\"1\",\r\n" + 
				"    \"industry_id2\":\"4\"\r\n" + 
				"}";
		//发送Post请求
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
		RequestBody requestBody = RequestBody.create(mediaType, json);
		Request request = new Request.Builder().post(requestBody).url(url).build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response resp) throws IOException {
				String result = resp.body().string();
				System.out.println(result);
			}
				
			@Override
			public void onFailure(Call call, IOException e) {
			}
		});
	}
	
	/**
	 * 获取设置的行业信息
	 */
	public static void getIndustry() {
		OkHttpClient client = new OkHttpClient();
		String url = "https://api.weixin.qq.com/cgi-bin/template/get_industry?access_token=" + WxService.getTokenIfExpired();
		Request request = new Request.Builder().get().url(url).build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response resp) throws IOException {
				String result = resp.body().string();
				System.out.println(result);
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
			}
		});
	}
	
	/**
	 * 发送模板消息
	 */
	public static void sendTemplateMessage() {
		String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + WxService.getTokenIfExpired();
		String json = "{\r\n" + 
				"           \"touser\":\"olvcit_LiCooaDHspeDuj3FY0wCs\",\r\n" + 
				"           \"template_id\":\"O7098ghaIZ8i_O7MKmCUL_KQ-TbWraZjTQMfwCWhoEc\",   \r\n" + 
				"           \"data\":{\r\n" + 
				"                   \"first\": {\r\n" + 
				"                       \"value\":\"您的企业资料审核通过\",\r\n" + 
				"                       \"color\":\"#173177\"\r\n" + 
				"                   },\r\n" + 
				"                   \"keyword1\":{\r\n" + 
				"                       \"value\":\"11****3729\",\r\n" + 
				"                       \"color\":\"#173177\"\r\n" + 
				"                   },\r\n" + 
				"                   \"keyword2\": {\r\n" + 
				"                       \"value\":\"肯德基\",\r\n" + 
				"                       \"color\":\"#173177\"\r\n" + 
				"                   },\r\n" + 
				"                   \"keyword3\": {\r\n" + 
				"                       \"value\":\"资料齐全\",\r\n" + 
				"                       \"color\":\"#173177\"\r\n" + 
				"                   },\r\n" + 
				"                   \"remark\":{\r\n" + 
				"                       \"value\":\"马上开始使用吧\",\r\n" + 
				"                       \"color\":\"#173177\"\r\n" + 
				"                   }\r\n" + 
				"           }\r\n" + 
				"       }";
		//发送Post请求
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
		RequestBody requestBody = RequestBody.create(mediaType, json);
		Request request = new Request.Builder().post(requestBody).url(url).build();
		client.newCall(request).enqueue(new Callback() {
					
			@Override
			public void onResponse(Call call, Response resp) throws IOException {
				String result = resp.body().string();
				System.out.println(result);
			}
						
			@Override
			public void onFailure(Call call, IOException e) {
			}
		});
	}
	
	/**
	 * 获取带参数二维码的ticket
	 * @return
	 */
	public static String getQrCodeTicket() {
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + WxService.getTokenIfExpired();
		String json = "{\"expire_seconds\": 604800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": 123}}}";
		//发送Post请求
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
		RequestBody requestBody = RequestBody.create(mediaType, json);
		Request request = new Request.Builder().post(requestBody).url(url).build();
		try {
			Response response = client.newCall(request).execute();
			String result = response.body().string();
			JSONObject jsonObject = JSONObject.fromObject(result);
			String ticket = jsonObject.getString("ticket");
			return ticket;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 生成带参数的二维码
	 */
	public static void createQrCode() {
		String ticket = getQrCodeTicket();
		String url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + ticket;
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url(url).build();
		try {
			Response response = client.newCall(request).execute();
			InputStream in = response.body().byteStream();
			//保存图片
			FileOutputStream out = new FileOutputStream(new File("qrcode.png"));
			//保存图片
			byte[] buffer = new byte[1024];
			int len = -1;
			while((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取用户信息
	 */
	public static String getUserInfo(String openId) {
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + WxService.getTokenIfExpired() + "&openid=" + openId + "&lang=zh_CN";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url(url).build();
		try {
			Response response = client.newCall(request).execute();
			String result = response.body().string();
			System.out.println(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String userInfo = getUserInfo("olvcit_LiCooaDHspeDuj3FY0wCs");
		System.out.println(userInfo);
	} 
}
