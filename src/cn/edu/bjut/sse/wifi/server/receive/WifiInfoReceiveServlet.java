package cn.edu.bjut.sse.wifi.server.receive;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.bjut.sse.wifi.server.model.LocationDetails;
import cn.edu.bjut.sse.wifi.server.service.WifiService;

import com.alibaba.fastjson.JSONObject;

public class WifiInfoReceiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");

		System.out.println("hahh");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

       
        out.write(new String("这是一个测试字符！"));

        out.flush();

        out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		WifiService wifiService = new WifiService();
		
		resp.setContentType("text/html");

        resp.setCharacterEncoding("UTF-8");
        
        
        String wifiJsonObject = req.getParameter("wifiJson");
        
        LocationDetails ld = (LocationDetails) JSONObject.parseObject(wifiJsonObject,LocationDetails.class);
        
        wifiService.saveLocationInfo(ld);
        
        
        //里面的list还需要做其他的转化
        
        //fastJson才可以转化复合对象
        
        System.out.println(ld.getX()+","+ld.getY()+":"+ld.getWifiDetails().get(0).getRSS());
        
        //int result = dbo.saveLocationDetails(ld, connection);
        
        
        PrintWriter out = resp.getWriter();

        
        out.write(new String("locationId is:"+1+"\n"));

        out.flush();

        out.close();
		
	}

}
