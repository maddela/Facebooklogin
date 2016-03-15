package com.javapapers.java.social.facebook;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.google.gson.Gson;

import com.javapapers.java.social.facebook.FBUser;



public class MainMenu extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String code="";

	public void service(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {		
		code = req.getParameter("code");
		if (code == null || code.equals("")) {
			throw new RuntimeException(
					"ERROR: Didn't get code parameter in callback.");
		}
		FBConnection fbConnection = new FBConnection();
		String accessToken = fbConnection.getAccessToken(code);

		FBGraph fbGraph = new FBGraph(accessToken);
		String graph = fbGraph.getFBGraph();
		Map<String, String> fbProfileData = fbGraph.getGraphData(graph);
		ServletOutputStream out = res.getOutputStream();
		out.println("<h1>Facebook Login using Java</h1>");
		out.println("<h2>Application Main Menu</h2>");
		out.println("<div>Welcome "+fbProfileData.get("first_name"));
		out.println("<div>Your Email: "+fbProfileData.get("email"));
		out.println("<div>You are "+fbProfileData.get("gender"));		
	}
	
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		FBConnection fbConnection = new FBConnection();
		String accessToken = fbConnection.getAccessToken(code);

		FBGraph fbGraph = new FBGraph(accessToken);
		String graph = fbGraph.getFBGraph();
	
    Configuration cfg = new Configuration();
    cfg.configure("hibernate.cfg.xml");

    SessionFactory factory = cfg.buildSessionFactory();
    Session session = factory.openSession();
    Map<String, String> fb = fbGraph.getGraphData(graph);
    
    fb.get("first_name");
    fb.get("email");
    fb.get("gender");
    

    Transaction tx = session.beginTransaction();
    session.save(fb);
    System.out.println("Object saved successfully.....!!");
    tx.commit();
    session.close();
    factory.close();
    String json = new Gson().toJson(fb);
	response.setContentType("application/json");
	response.getWriter().write(json);
    
} 

}
