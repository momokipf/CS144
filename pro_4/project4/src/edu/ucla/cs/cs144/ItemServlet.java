package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;


public class ItemServlet extends HttpServlet implements Servlet {
       
    public ItemServlet() {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        // your codes here

        ServletContext context = getServletContext( );
        AuctionSearch as = new AuctionSearch();

        context.log("getQueryString: " + request.getQueryString());
    	context.log("getRequestURI: " + request.getRequestURI());

        String itemid = (String)request.getParameter("itemid");

        Item item = getItem(as.getXMLDataForItemId(itemid));
        if(item!=null)
        	context.log(item.toString());

        if(item!=null){
        	request.setAttribute("item",item);
        	context.log("none NULL pointer");
        }
       	else{
       		context.log("Null pointer");
       	}

        request.getRequestDispatcher("/item.jsp").forward(request,response);
    }


    private Item getItem(String itemxml){

    	Document document = null;
    	try{
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
    	DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		document = builder.parse(new InputSource(new StringReader(itemxml)));

    	}catch(Exception e){
    		//System.out.println("parser was unable to be configured");
            System.exit(2);
    	}

    	org.w3c.dom.NodeList nlist = document.getChildNodes();

    	Item item  = null;
    	for(int i=0; i<nlist.getLength(); i++)
    	{
    		if(nlist.item(i).getNodeName().equals("Item"))
            	item = getItem((Element)nlist.item(i));
        }

    	return item;
    }


    private Item getItem(Element e){
    	if(e==null)
    		return null;
        String itemid = null;
        Item it = new Item();
    	org.w3c.dom.NamedNodeMap nattrib = e.getAttributes();
    	if(nattrib!=null&&nattrib.getLength()>0){
    		for(int i=0; i<nattrib.getLength();++i){
    			if(nattrib.item(i).getNodeName().equals("ItemID")){
    				itemid = nattrib.item(i).getNodeValue();
    			}
    			//System.out.println(it.itemid);
    		}
        	it.itemid = itemid;
    	}
    	it.name = getElementTextByTagNameNR(e,"Name");
    	Element[] es = getElementsByTagNameNR(e,"Category");
    	for(Element cat:es){
    		it.category.add(getElementText(cat));
    	}
    	Element bids = getElementByTagNameNR(e,"Bids");
    	if(bids!=null){
    		es = getElementsByTagNameNR(bids,"Bid");
    		//System.out.println(Integer.toString(es.length));
    		for(Element bid:es){
    			//System.out.println("new Bid");
    			setbidelement_attr(it,bid);
    		}
    	}
    	it.currently = strip(getElementTextByTagNameNR(e,"Currently"));
        if(getElementTextByTagNameNR(e,"Buy_Price")!=null){
    	   it.buy_price = strip(getElementTextByTagNameNR(e,"Buy_Price"));
        }
        it.first_bid = strip(getElementTextByTagNameNR(e,"First_Bid"));
    	it.number_of_Bids = Integer.parseInt(getElementTextByTagNameNR(e,"Number_of_Bids"));
    	it.country = getElementTextByTagNameNR(e,"Country");
    	it.started = Item.parseDate(getElementTextByTagNameNR(e,"Started"));
    	it.ends = Item.parseDate(getElementTextByTagNameNR(e,"Ends"));
    	it.description = getElementTextByTagNameNR(e,"Description");
    	Element loc = getElementByTagNameNR(e,"Location");
    	setLocationelement_attr(it,loc);
    	Element seller_e = getElementByTagNameNR(e,"Seller");
    	User seller = setUserlement_attr(it,seller_e,false);
    	it.sel = seller;

    	return it;
    }

    private void setLocationelement_attr(Item it,Element e){
    	if(e==null)
        {
            System.out.println("escape location");
    		return ;
        }
		String latitude = null;
		String longitude = null;
        String content = null;
		org.w3c.dom.NamedNodeMap nattrib = e.getAttributes();
		if(nattrib!=null&&nattrib.getLength()>0){
		for(int i=0; i<nattrib.getLength();++i){
			if(nattrib.item(i).getNodeName().equals("Latitude")){
				latitude = nattrib.item(i).getNodeValue();
			}
			if(nattrib.item(i).getNodeName().equals("Longitude")){
				longitude = nattrib.item(i).getNodeValue();
			}
		  }
        }
    }

    private  User setUserlement_attr(Item it,Element e,boolean isbidder){
    	if(e==null)
    		return null;
    	org.w3c.dom.NamedNodeMap nattrib = e.getAttributes();
    	User user = null;

    	if(nattrib!=null&&nattrib.getLength()>0){
			//System.out.println("bid attr length"+Integer.toString(nattrib.getLength()));
			String userid  = null;
    		String rating = null;
    		for(int i=0; i<nattrib.getLength();++i){
    			if(nattrib.item(i).getNodeName().equals("UserID")){
    				userid = nattrib.item(i).getNodeValue();
    			}
    			if(nattrib.item(i).getNodeName().equals("Rating")){
    				rating= nattrib.item(i).getNodeValue();

                    // if(Integer.parseInt(rating)==-2)
                    //     System.out.println("this rate is -2" + rating);
    			}
    		}
    		user = new User(userid);
    		if(userid!=null){
				if(isbidder){
					user.buyrate = Integer.parseInt(rating);
					// if(user.buyrate!=Integer.parseInt(rating))
					// 	System.out.println("User "+userid+"'s rate is not consistent");
				}
				else{
					user.sellrate = Integer.parseInt(rating);
					// if(user.sellrate!=Integer.parseInt(rating))
					// 	System.out.println("User "+userid+"'s rate is not consistent");
				}
			}
    	}

    	return user;
    }

    private void setbidelement_attr(Item it,Element e){
    	if(e==null)
    		return ;
    	Element bidder = getElementByTagNameNR(e,"Bidder");
    	String location = null;
    	String country = null;
    	User buyer = null;
    	if(bidder!=null){
    		buyer = setUserlement_attr(it,bidder,true);
    		location = getElementTextByTagNameNR(bidder,"Location");
    		country = getElementTextByTagNameNR(bidder,"Country");
    		buyer.location = location;
    		buyer.country = country;
    	}

    	String time = getElementTextByTagNameNR(e,"Time");
    	String amount = strip(getElementTextByTagNameNR(e,"Amount"));
    	it.addbid(buyer,time,amount);
    }

    /* Non-recursive (NR) version of Node.getElementsByTagName(...)
     */
    static Element[] getElementsByTagNameNR(Element e, String tagName) {
        Vector< Element > elements = new Vector< Element >();
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
            {
                elements.add( (Element)child );
            }
            child = child.getNextSibling();
        }
        Element[] result = new Element[elements.size()];
        elements.copyInto(result);
        return result;
    }
    
    /* Returns the first subelement of e matching the given tagName, or
     * null if one does not exist. NR means Non-Recursive.
     */
    static Element getElementByTagNameNR(Element e, String tagName) {
        Node child = e.getFirstChild();
        while (child != null) {
            if (child instanceof Element && child.getNodeName().equals(tagName))
                return (Element) child;
            child = child.getNextSibling();
        }
        return null;
    }
    
    /* Returns the text associated with the given element (which must have
     * type #PCDATA) as child, or "" if it contains no text.
     */
    static String getElementText(Element e) {
        if (e.getChildNodes().getLength() == 1) {
            Text elementText = (Text) e.getFirstChild();
            return elementText.getNodeValue();
        }
        else
            return "";
    }
    
    /* Returns the text (#PCDATA) associated with the first subelement X
     * of e with the given tagName. If no such X exists or X contains no
     * text, "" is returned. NR means Non-Recursive.
     */
    static String getElementTextByTagNameNR(Element e, String tagName) {
        Element elem = getElementByTagNameNR(e, tagName);
        if (elem != null)
            return getElementText(elem);
        else
            return "";
    }
    
    /* Returns the amount (in XXXXX.xx format) denoted by a money-string
     * like $3,453.23. Returns the input if the input is an empty string.
     */
    static String strip(String money) {
        if (money.equals(""))
            return money;
        else {
            double am = 0.0;
            NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
            try { am = nf.parse(money).doubleValue(); }
            catch (ParseException e) {
                System.out.println("This method should work for all " +
                                   "money values you find in our data.");
                System.exit(20);
            }
            nf.setGroupingUsed(false);
            return nf.format(am).substring(1);
        }
    }
}
