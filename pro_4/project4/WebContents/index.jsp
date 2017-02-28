
<%@ page import="edu.ucla.cs.cs144.SearchResult"%>
<html>
<head>
	Here is the search result<br>
</head>
<%
	SearchResult[] searchresult = (SearchResult[])request.getAttribute("result");
%>

<body>

	<%
	out.println("request.getServerName() = " + request.getServerName()+"<br>");
	out.println("request.getRequestURI() = " + request.getRequestURI()+"<br>");
	%>


	<table align='center' border='1' cellspacing='0'>
		<tr>
			<th> ItemID </th>
			<th> ItemName</th>
		</tr>
	<% for(SearchResult rs: searchresult) { %>
		<tr>
			<td>   <%=rs.getItemId()  %></td>
			<td>  <% 
					out.println("<a href=/eBay/item?itemid="+ rs.getItemId()+" > ");
					out.println(rs.getName());
					out.println("</a>");
					%> 
			</td>
		</tr>
		<%
		}
		%>
	</table>

	<br>
	<a href="?index=${index}&amp;query=${query}">next</a>
</body>
</html>