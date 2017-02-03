package edu.ucla.cs144;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.List;

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

//import edu.ucla.cs144.Item.*;


public class MyParser {
    
    static final String columnSeparator = "|*|";
    static DocumentBuilder builder;

    static final String[] typeName = {
	"none",
	"Element",
	"Attr",
	"Text",
	"CDATA",
	"EntityRef",
	"Entity",
	"ProcInstr",
	"Comment",
	"Document",
	"DocType",
	"DocFragment",
	"Notation",
    };
    
	static final String[] outputTable ={
	"item_element.csv",
	"item_category.csv",
	"item_bidder.csv",
	"user.csv",
	};

    private static HashMap<String,Item> itemidmap = new HashMap<String,Item>();
    private static HashMap<String,User> useridmap = new HashMap<String,User>(); 
    

    static class MyErrorHandler implements ErrorHandler {
        
        public void warning(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void error(SAXParseException exception)
        throws SAXException {
            fatalError(exception);
        }
        
        public void fatalError(SAXParseException exception)
        throws SAXException {
            exception.printStackTrace();
            System.out.println("There should be no errors " +
                               "in the supplied XML files.");
            System.exit(3);
        }
        
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
    
    /* Process one items-???.xml file.
     */
    static void processFile(File xmlFile) {
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        catch (SAXException e) {
            System.out.println("Parsing error on file " + xmlFile);
            System.out.println("  (not supposed to happen with supplied XML files)");
            e.printStackTrace();
            System.exit(3);
        }
        
        /* At this point 'doc' contains a DOM representation of an 'Items' XML
         * file. Use doc.getDocumentElement() to get the root Element. */
        System.out.println("Successfully parsed - " + xmlFile);
        
        getItem(doc,0);

        /* Fill in code here (you will probably need to write auxiliary
            methods). */
        
        
        /**************************************************************/
        
        //recursiveDescent(doc, 0);
    }


    public static void getItem(Node n,int level){

    	String ntype = typeName[n.getNodeType()];
    	String nname = n.getNodeName();
    	String nvalue = n.getNodeValue();

    	//System.out.println("enter"+ nname);
    	if(nname=="Item"){
    		Item item = new Item();
    		setItemelement_attr(item,(Element)n);
    		return;
    	}
		// dump out attributes if any
        org.w3c.dom.NamedNodeMap nattrib = n.getAttributes();
        if(nattrib != null && nattrib.getLength() > 0)
            for(int i=0; i<nattrib.getLength(); i++)
                getItem(nattrib.item(i),  level+1);
        
        // now walk through its children list
        org.w3c.dom.NodeList nlist = n.getChildNodes();
        
        for(int i=0; i<nlist.getLength(); i++)
            getItem(nlist.item(i), level+1);
    }
    
    private static boolean setItemelement_attr(Item it,Element e){
    	if(e==null)
    		return false;
    	org.w3c.dom.NamedNodeMap nattrib = e.getAttributes();
    	if(nattrib!=null&&nattrib.getLength()>0){
    		for(int i=0; i<nattrib.getLength();++i){
    			if(nattrib.item(i).getNodeName().equals("ItemID")){
    				it.itemid = nattrib.item(i).getNodeValue();
    				if(itemidmap.containsKey(it.itemid)){
    					it = null;
    					return false;
    				}
    			}
    			//System.out.println(it.itemid);
    		}
    	}
    	it.name = getElementTextByTagNameNR(e,"Name");
    	Element[] es = getElementsByTagNameNR(e,"Category");
    	for(Element cat:es){
    		it.category.add(getElementText(cat));
    		//System.out.print(getElementText(cat)+" ");
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
    	it.buy_price = strip(getElementTextByTagNameNR(e,"First_Bid"));
    	it.number_of_Bids = Integer.parseInt(getElementTextByTagNameNR(e,"Number_of_Bids"));
    	it.country = getElementTextByTagNameNR(e,"Country");
    	it.started = getElementTextByTagNameNR(e,"Started");
    	it.ends = getElementTextByTagNameNR(e,"Ends");
    	it.description = getElementTextByTagNameNR(e,"Description");
    	Element loc = getElementByTagNameNR(e,"Location");
    	setLocationelement_attr(it,loc);
    	Element seller_e = getElementByTagNameNR(e,"Seller");
    	User seller = setUserlement_attr(it,seller_e,false);
    	it.sel = seller;
    	itemidmap.put(it.itemid,it);
    	//System.out.println(it);
    	return true;
    }
    private static void setLocationelement_attr(Item it,Element e){
    	if(e==null)
    		return ;
    	String content = null;
		String latitude = null;
		String longitude = null;
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
		org.w3c.dom.NodeList nlist = e.getChildNodes();
		if(nlist.getLength()>0){
			content = nlist.item(0).getNodeValue();
		}
		it.addLocation(latitude,longitude,content);
		}
    }

    private static User setUserlement_attr(Item it,Element e,boolean isbidder){
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
    			}
    		}
    		if(userid!=null){
	    		if(useridmap.containsKey(userid))
					user = useridmap.get(userid);
				else{
					user = new User(userid);
					useridmap.put(userid,user);
				}
				if(isbidder){
					user.buyrate = Integer.parseInt(rating);
					if(user.buyrate!=Integer.parseInt(rating))
						System.out.println("User "+userid+"'s rate is not consistent");
				}
				else{
					user.sellrate = Integer.parseInt(rating);
					if(user.sellrate!=Integer.parseInt(rating))
						System.out.println("User "+userid+"'s rate is not consistent");
				}
			}
    	}
    	return user;	
    }
    private static void setbidelement_attr(Item it,Element e){
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

    public static boolean converttoCvs(){

    	ArrayList<FileWriter> tablewriter = new ArrayList<FileWriter>();

    	try{

    		for(int i=0;i<outputTable.length;++i){
	    		FileWriter writer = new FileWriter(outputTable[i],true);
	    		tablewriter.add(writer);
	    	}
			Iterator<String> it = itemidmap.keySet().iterator();
			while(it.hasNext()){
				String itemid = it.next();
				Item item = itemidmap.get(itemid);
				CSVUtils.writeLine(tablewriter.get(0), Item.item_strlist(item));
				for(String cat:item.category){
					CSVUtils.writeLine(tablewriter.get(1), Arrays.asList(itemid,cat));
				}	

				for(int i = 0 ; i<item.bids.size();++i){
					CSVUtils.writeLine(tablewriter.get(2),Item.bid_strlist(item,item.bids.get(i)));
				}

			}

			it = useridmap.keySet().iterator();
			while(it.hasNext()){
				String userid = it.next();
				User user = useridmap.get(userid);
				CSVUtils.writeLine(tablewriter.get(3),User.user_strlist(user));
			}


			for(int i=0;i<outputTable.length;++i){
    			tablewriter.get(i).flush();
    			tablewriter.get(i).close();
    		}
    	}catch(IOException e){
    		System.out.println("csv output error");
    		System.exit(1);
    	}

        System.out.println(Integer.toString(itemidmap.size())+" Items have been recorded");
        System.out.println(Integer.toString(useridmap.size())+" Users have been recorded");

    	return true;
    }

    public static void recursiveDescent(Node n, int level) {
        // adjust indentation according to level
        for(int i=0; i<4*level; i++)
            System.out.print(" ");
        
        // dump out node name, type, and value  
        String ntype = typeName[n.getNodeType()];
        String nname = n.getNodeName();
        String nvalue = n.getNodeValue();
        
        System.out.println("Type = " + ntype + ", Name = " + nname + ", Value = " + nvalue);
        
        // dump out attributes if any
        org.w3c.dom.NamedNodeMap nattrib = n.getAttributes();
        if(nattrib != null && nattrib.getLength() > 0)
            for(int i=0; i<nattrib.getLength(); i++)
                recursiveDescent(nattrib.item(i),  level+1);
        
        // now walk through its children list
        org.w3c.dom.NodeList nlist = n.getChildNodes();
        
        for(int i=0; i<nlist.getLength(); i++)
            recursiveDescent(nlist.item(i), level+1);
    }  
    

    public static void main (String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java MyParser [file] [file] ...");
            System.exit(1);
        }
        
        /* Initialize parser. */
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);      
            builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new MyErrorHandler());
        }
        catch (FactoryConfigurationError e) {
            System.out.println("unable to get a document builder factory");
            System.exit(2);
        } 
        catch (ParserConfigurationException e) {
            System.out.println("parser was unable to be configured");
            System.exit(2);
        }
        
        /* Process all files listed on command line. */
        for (int i = 0; i < args.length; i++) {
            File currentFile = new File(args[i]);
            processFile(currentFile);
        }
        converttoCvs();
    }
}