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
	
	//����Ƿ��ȡ��AccessToken
	private static boolean getAccessToken = false;
	
	//���ڴ洢AccessToken
	private static AccessToken aToken;
	
	private static String str = null;	//�����˽ӿڷ��ص�����
	
	public static boolean check(String timestamp,String nonce,String signature) {
		//��token��timestamp��nonce�����ֵ�����
		String[] strs = new String[] {TOKEN,timestamp,nonce};
		Arrays.sort(strs);
		//�����������ַ���ƴ�ӳ�һ���ַ�������sha1����
		String str = strs[0] + strs[1] + strs[2];
		String mySig = sha1(str);
		return mySig.equals(signature);
	}

	/**
	 * sha1����
	 * @param str
	 * @return
	 */
	private static String sha1(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("sha1");
			//����
			byte[] digest = md.digest(str.getBytes());
			char[] chars = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
			StringBuilder sb = new StringBuilder();
			//������ܽ��
			for(byte b : digest) {
				//�������λ
				sb.append(chars[(b>>4) & 15]);
				//�������λ
				sb.append(chars[b & 15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	//������Ϣ���¼�����
	public static Map<String,String> parseRequest(InputStream in) {
		Map<String, String> map = new HashMap<String, String>();
		//����XML���ݰ�
		SAXReader reader = new SAXReader();
		try {
			//��ȡ����������ȡ�ĵ�����
			Document document = reader.read(in);
			//��ȡ�����
			Element root = document.getRootElement();
			//��ȡ�����������ӽ��
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
	 * ���ڴ������е��¼�����Ϣ�ظ�
	 * @param reqMap
	 * @return
	 */
	public static String getData(Map<String, String> reqMap) {
		BaseMessage msg = null;
		String msgType = reqMap.get("MsgType");
		switch (msgType) {
			case "text":
				//�����ı���Ϣ
				msg = dealText(reqMap);
				break;
			case "image":
				//����û����͵���ͼƬ����ظ��û�ͼ����Ϣ
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
		//��bean����ת��Ϊxml����
		if(msg != null) {
			return beanToXml(msg);
		}else {
			return null;
		}
	}
	
	/**
	 * �����¼�����
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
	 * ����View���Ͳ˵�
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealView(Map<String, String> reqMap) {
		return null;
	}

	/**
	 * ����Click���Ͳ˵�
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealClick(Map<String, String> reqMap) {
		String key = reqMap.get("EventKey");
		switch (key) {
			//�����һ�����˵�
			case "1":
				//�ظ��û�
				return new TextMessage(reqMap, "�������һ���˵�");
			//�����һ���˵��ĵ������Ӳ˵�
			case "13":
				//�ظ��û�
			default:
				break;
		}
		return null;
	}

	/**
	 * ����û����͵���ͼƬ����ظ��û�ͼ����Ϣ
	 * @param reqMap
	 */
	private static BaseMessage dealImage(Map<String, String> reqMap) {
		List<Article> articles = new ArrayList<Article>();
		articles.add(new Article("����","15���ϳ���Ա������8��Ӱ����ְҵ���ĵ���Ҫ����", "https://mmbiz.qpic.cn/mmbiz_jpg/Pn4Sm0RsAuianWDokh5pic2LUZuQCxnFRxOUV19Uic1x3aiayowSxP6rb3juBgAfSbE2kqianWk1sRN3b33Vw4LW7jw/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1", "http://mp.weixin.qq.com/s?__biz=MjM5MjAwODM4MA==&mid=2650737643&idx=2&sn=322109799b7e0401957734eb9ac3c803&chksm=bea77a3889d0f32edeeaaa9dc136f9296fd84905d60f7ce6bb8320115838e79fff260a1bf824&scene=0&xtrack=1#rd"));
		NewsMessage nm = new NewsMessage(reqMap, "1", articles);
		return nm;
	}

	/**
	 * ��bean����ת��Ϊxml����
	 * @param msg
	 * @return
	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream = new XStream();
		//�����Ҫ����ע�͵���
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
	 * �����ı���Ϣ
	 * @param reqMap
	 * @return
	 */
	private static BaseMessage dealText(Map<String, String> reqMap) {
		//�����û����͵���Ϣ
		String msg = reqMap.get("Content");
		//������������
		String respMsg = robotChat(msg);
		TextMessage tMsg = new TextMessage(reqMap,respMsg);
		return tMsg;
	}

	/**
	 * ���û����˽ӿ�
	 * @param msg
	 * @return
	 */
	private static String robotChat(String msg) {
		//ƴ������url
		String url = "http://op.juhe.cn/iRobot/index?info=" + msg + "&key=" + APPKEY;
		//����url
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url(url).build();
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(Call call, Response resp) throws IOException {
				if(resp.code() == 200) {
					//��Ӧ�ɹ�
					String json = resp.body().string();
					//����json����
					JSONObject jsonObject = JSONObject.fromObject(json);
					int code = jsonObject.getInt("error_code");
					if(code == 0) {
						//�����˽ӿڷ�������
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
	 * ��ȡToken
	 * @return
	 */
	private static void getToken() {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().get().url("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APPID+"&secret="+APPSECRET).build();
		try {
			Response response = client.newCall(request).execute();
			String json = response.body().string();
			//�����װ�ɶ���
			JSONObject jsonObject = JSONObject.fromObject(json);
			String accessToken = jsonObject.getString("access_token");
			String expiresIn = jsonObject.getString("expires_in");
			aToken = new AccessToken(accessToken, expiresIn);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * ��ȡAccessToken�������ڣ������»�ȡ
	 */
	public static String getTokenIfExpired() {
		if(aToken == null || aToken.isExpire()) {
			//��ȡAccessToken
			getToken();
		}
		return aToken.getAccessToken();
	}
}
