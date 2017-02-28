package edu.ucla.cs.cs144;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet implements Servlet {
       
    public SearchServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here
    	ServletContext context = getServletContext( );

    	AuctionSearch as = new AuctionSearch();

    	context.log("getQueryString: " + request.getQueryString());
    	context.log("getRequestURI: " + request.getRequestURI());

    	String query = (String)(request.getParameter("query"));
    	
    	int index = 0;
    	try{
    		index = Integer.parseInt((String)request.getParameter("index"));
    	}catch(NumberFormatException e){
    		index =0;
    	}
    	context.log(query);
    	SearchResult[] basicResults = as.basicSearch(query,index,20);
    	//context.log("result.size() = " + basicResults.length);
    	request.setAttribute("result",basicResults);
    	request.setAttribute("query",query);
    	request.setAttribute("index",index+basicResults.length);
    	request.getRequestDispatcher("/index.jsp").forward(request,response);

    }
}
