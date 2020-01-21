package com.wechat.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.wechat.entity.AccessToken;
import com.wechat.entity.Article;
import com.wechat.entity.BaseMessage;
import com.wechat.entity.ImageMessage;
import com.wechat.entity.MusicMessage;
import com.wechat.entity.NewsMessage;
import com.wechat.entity.TextMessage;
import com.wechat.entity.VideoMessage;
import com.wechat.entity.VoiceMessage;
import com.wechat.manager.TemplateMessage;

import net.sf.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WxService {
	
	private static final String TOKEN = "abcdefg";
	private static final String APPKEY = "78af524b4a069965884ff6f6ae54fd6c";
	private static final String APPID = "wx991b926a9b3e17fa";
	private static final String APPSECRET = "ad569d0b94665a0e08a56bdc9316f150";
	
	//标记是否获取到AccessToken
	private static boolean getAccessToken = false;
	
	//用于存储AccessToken
	private static AccessToken aToken;
	
	private static String str = null;	//机器人接口返回的内容
	
	public static boolean check(String timestamp,String nonce,String signature) {
		//将token、timestamp、nonce进行字典排序
		String[] strs = new String[] {TOKEN,timestamp,nonce};
		Arrays.sort(strs);
		//将三个参数字符串拼接成一个字符串进行sha1加密
		String str = strs[0] + strs[1] + strs[2];
		String mySig = sha1(str);
		return mySig.equals(signature);
	}

	/**
	 * sha1加密
	 * @param str
	 * @return
	 */
	private static String sha1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("sha1");
			//加密
			byte[] digest = md.digest(str.getBytes());
			char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			StringBuilder sb = new StringBuilder();
			//处理加密结果
			for(byte b : digest) {
				//处理高四位
				sb.append(chars[(b>>4) & 15]);
				//处理低四位
				sb.append(chars[b & 15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	//处理消息和事件推送
	public static Map<String,String> parseRequest(InputStream in) {
		Map<String, String> map = new HashMap<String, String>();
		//解析XML数据包
		SAXReader reader = new SAXReader();
		try {
			//读取输入流，获取文档对象
			Document document = reader.read(in);
			//获取根结点
			Element root = document.getRootElement();
			//获取根结点的所有子结点
			List<Element> elements  = root.elements();
			for(Element e : elements) {
				map.put(e.getName(),e.getStringValue());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 用于处理所有的事件和消息回复
	 * @param reqMap
	 * @return
	 */
	public static String getData(Map<String, String> reqMap) {
		BaseMessage msg = null;
		String msgType = reqMap.get("MsgType");
		switch (msgType) {
			case "text":
				//处理文本消息
				msg = dealText(reqMap);
				break;
			case "image":
				//如果用户发送的是图片，则回复用户图文消息
				msg = dealImage(reqMap);
				break;
			case "voice":
				
				break;
			case "video":
				
				break;
			case "shortvideo":
				
				break;
			case "location":
				
				break;
			case "link":
				
				break;
			case "event":
				msg = dealEvent(reqMap);
				break;
			default:
				break;
		}
		//将bean对象转换为xml数据
		if(msg != null) {
			return beanToXml(msg);
		}else {
			return null;
		}
	}
	
	/**
	 * 处理事件推送
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealEvent(Map<String, String> reqMap) {
		String event = reqMap.get("Event");
		switch (event) {
			case "CLICK":
				return dealClick(reqMap);
			case "VIEW":
				return dealView(reqMap);
			default:
				break;
		}
		return null;
	}
	
	/**
	 * 处理View类型菜单
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealView(Map<String, String> reqMap) {
		return null;
	}

	/**
	 * 处理Click类型菜单
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealClick(Map<String, String> reqMap) {
		String key = reqMap.get("EventKey");
		switch (key) {
			//点击了一级级菜单
			case "1":
				//回复用户
				return new TextMessage(reqMap, "您点击了一级菜单");
			//点击了一级菜单的第三个子菜单
			case "13":
				//回复用户
			default:
				break;
		}
		return null;
	}

	/**
	 * 如果用户发送的是图片，则回复用户图文消息
	 * @param reqMap
	 */
	private static BaseMessage dealImage(Map<String, String> reqMap) {
		List<Article> articles = new ArrayList<Article>();
		articles.add(new Article("标题","15年老程序员自述：8个影响我职业生涯的重要技能", "https://mmbiz.qpic.cn/mmbiz_jpg/Pn4Sm0RsAuianWDokh5pic2LUZuQCxnFRxOUV19Uic1x3aiayowSxP6rb3juBgAfSbE2kqianWk1sRN3b33Vw4LW7jw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1", "http://mp.weixin.qq.com/s?__biz=MjM5MjAwODM4MA==&mid=2650737643&idx=2&sn=322109799b7e0401957734eb9ac3c803&chksm=bea77a3889d0f32edeeaaa9dc136f9296fd84905d60f7ce6bb8320115838e79fff260a1bf824&scene=0&xtrack=1#rd"));
		NewsMessage nm = new NewsMessage(reqMap, "1", articles);
		return nm;
	}

	/**
	 * 将bean对象转换为xml数据
	 * @param msg
	 * @return
	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream = new XStream();
		//添加需要处理注释的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(msg);
		return xml;
	}

	/**
	 * 处理文本消息
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealText(Map<String, String> reqMap) {
		//接收用户发送的消息
		String msg = reqMap.get("Content");
		//返回聊天内容
		String respMsg = robotChat(msg);
		TextMessage tMsg = new TextMessage(reqMap,respMsg);
		return tMsg;
	}

	/**
	 * 调用机器人接口
	 * @param msg
	 * @return
	 */
	private static String robotChat(String msg) {
		//拼接请求url
		String url = "http://op.juhe.cn/iRobot/index?info=" + msg + "&key=" + APPKEY;
		//请求url
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url(url).build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response resp) throws IOException {
				if(resp.code() == 200) {
					//响应成功
					String json = resp.body().string();
					//解析json数据
					JSONObject jsonObject = JSONObject.fromObject(json);
					int code = jsonObject.getInt("error_code");
					if(code == 0) {
						//机器人接口访问正常
						str = jsonObject.getJSONObject("result").getString("text");
					}
				}
			}
			
			@Override
			public void onFailure(Call call, IOException e) {
				
			}
		});
		return str;
	}
	
	/**
	 * 获取Token
	 * @return
	 */
	private static void getToken() {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET).build();
		try {
			Response response = client.newCall(request).execute();
			String json = response.body().string();
			//将其封装成对象
			JSONObject jsonObject = JSONObject.fromObject(json);
			String accessToken = jsonObject.getString("access_token");
			String expiresIn = jsonObject.getString("expires_in");
			aToken = new AccessToken(accessToken, expiresIn);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 获取AccessToken，若过期，则重新获取
	 */
	public static String getTokenIfExpired() {
		if(aToken == null || aToken.isExpire()) {
			//获取AccessToken
			getToken();
		}
		return aToken.getAccessToken();
	}
}
