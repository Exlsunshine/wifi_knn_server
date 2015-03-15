package cn.edu.bjut.sse.wifi.server.matching;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.edu.bjut.sse.wifi.server.matching.service.MatchingService;
import cn.edu.bjut.sse.wifi.server.model.LocationDetails;

import com.alibaba.fastjson.JSONObject;

public class MatchingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public MatchingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		MatchingService matchingService = new MatchingService();
		
		response.setContentType("text/html");

        response.setCharacterEncoding("UTF-8");
        
        
        String wifiJsonObject = request.getParameter("wifiJson");
        
        LocationDetails ld = (LocationDetails) JSONObject.parseObject(wifiJsonObject,LocationDetails.class);
        
        LocationDetails opsition = matchingService.matching(ld);
        
        
        PrintWriter out = response.getWriter();

        
        
        
        out.write(new String("location is: x is"+opsition.getX()+", y is"+opsition.getY()+"\n"));

        out.flush();

        out.close();
	}

}
