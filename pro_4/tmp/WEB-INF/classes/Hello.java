


import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.PrintWriter;

public class Hello extends HttpServlet implements Servlet {
       
    public Hello() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        String pageTitle = "My Page Title";
        request.setAttribute("title", pageTitle);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}