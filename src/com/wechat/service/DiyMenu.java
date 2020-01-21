package com.wechat.service;

import java.io.IOException;

import com.wechat.entity.Button;
import com.wechat.entity.ClickButton;
import com.wechat.entity.SubButton;
import com.wechat.entity.ViewButton;

import net.sf.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DiyMenu {
	
	public static void main(String[] args) {
		//�����˵�����
		Button btn = new Button();
		//������һ��һ���˵����Ӳ˵�
		SubButton sButton = new SubButton("��ϵ����");
		sButton.getSub_button().add(new ViewButton("һ��ѧAI","https://edu.csdn.net/topic/ai30?utm_source=csdncd"));
		sButton.getSub_button().add(new ViewButton("�������","https://mp.weixin.qq.com/s/-aP6f0efBEMFcyEQZITT1g"));
		sButton.getSub_button().add(new ViewButton("Ͷ����֪","https://mp.weixin.qq.com/s/M1eD8KkOTKQEhR0NkqPpVQ"));
		sButton.getSub_button().add(new ViewButton("ת����֪","https://mp.weixin.qq.com/s/rywCAd1U1zbzZr_yo3G2ig"));
		sButton.getSub_button().add(new ViewButton("��Դ|��Ӧ��|С����|loT","https://mp.weixin.qq.com/mp/homepage?__biz=MjM5MjAwODM4MA==&hid=7&sn=6804bfb21efd6e4b1fc9499cb56fd612&scene=18"));
		btn.getButton().add(sButton);
		//�����ڶ���һ���˵�
		btn.getButton().add(new ClickButton("��ѡ��Ŀ", "1"));
		//����������һ���˵�
		btn.getButton().add(new ClickButton("CSDN", "1"));
		//���Զ���˵�����תΪjson����
		JSONObject jsonObject = JSONObject.fromObject(btn);
		String json = jsonObject.toString();
		
		//����Post����
		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
		RequestBody requestBody = RequestBody.create(mediaType, json);
		//�����ַ
		String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + WxService.getTokenIfExpired();
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
}
