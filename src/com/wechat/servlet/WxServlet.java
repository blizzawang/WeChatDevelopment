package com.wechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.wechat.service.WxService;

@WebServlet("/WxServlet")
public class WxServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		//校验请求
		if(WxService.check(timestamp,nonce,signature)) {
			System.out.println("接入成功");
			//原样返回echostr参数
			PrintWriter out = response.getWriter();
			out.print(echostr);
			out.flush();
			out.close();
		}else {
			System.out.println("接入失败");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		Map<String, String> reqMap = WxService.parseRequest(request.getInputStream());
		System.out.println(reqMap);
		//回复用户
		String respXml = WxService.getData(reqMap);
		PrintWriter out = response.getWriter();
		out.print(respXml);
		out.flush();
		out.close();
	}
}
