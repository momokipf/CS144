package edu.ucla.cs.cs144;

import java.lang.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProxyServlet extends HttpServlet implements Servlet {
       
    public ProxyServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
    	ServletContext context = getServletContext();

    	String query = URLEncoder.encode((String)(request.getParameter("q")));
		StringBuffer res = null;
    	try{
    		URL googleSuggest = new URL( "http://google.com/complete/search?output=toolbar&q="+query) ;
    		HttpURLConnection connection = (HttpURLConnection)googleSuggest.openConnection();
    		connection.setRequestMethod("GET");
    		connection.connect();
	    	BufferedReader in = new BufferedReader(
			        new InputStreamReader(connection.getInputStream()));
			String inputLine;
			res = new StringBuffer();	
			while ((inputLine = in.readLine()) != null) {
				res.append(inputLine);
			}
			in.close();
		}catch (MalformedURLException e) {
    		context.log("URL error:" + "http://google.com/complete/search?output=toolbar&q="+query);
    	}

    	response.setContentType("text/xml;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write(res.toString());



    }
}
