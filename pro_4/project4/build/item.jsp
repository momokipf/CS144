<%@ page import="edu.ucla.cs.cs144.Item"%>
<%@ page import="edu.ucla.cs.cs144.User"%>

<!-- public String itemid = null;

	public String name = null;
	public List<String> category = null;
	public String currently;
	public String buy_price;
	public String first_bid;
	public int number_of_Bids;
	public List<Bid> bids;
	public Location loc = null;
	public String country = null;
	public String started = null;
	public String ends = null;
	public User sel = null;
	public String description = null;
 -->

<html>
	<head></head>	
	<%
		Item item = (Item)request.getAttribute("item");
	%>
	<body>
		ItemId:<%=item.itemid%> <br>
		Name:<%=item.name%> <br>
		Currently:<%=item.currently%><br>

	</body>
</html>