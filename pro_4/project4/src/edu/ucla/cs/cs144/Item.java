package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;




public class Item {

	public String itemid = null;

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


	private static SimpleDateFormat xmlformat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");  //Dec-10-01 09:26:52
	private static SimpleDateFormat sqlformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Item(){
		category = new ArrayList<String>(); 
		bids = new ArrayList<Bid>();
	}


	class Bid{
		public User bidder = null;
		public String time = null;
		public String amount =null;
		public Bid(User bidder,String time,String amount){
			this.bidder = bidder ;

			try{
				Date parsed = xmlformat.parse(time);
				this.time = sqlformat.format(parsed);
			}
			catch(ParseException e){
				System.out.println("ERROR: Cannot parse \"" + time + "\"");
			}
			this.amount = amount;
		}

		public String toString(){
			return '\t'+bidder.toString()+'\n'+" time:"+time+' '+" amount:"+amount; 
		}
	}

	class Location{
		public String content = null;
		public String latitude =null;
		public String longitude = null;

		public Location(String latitude, String longitude,String content)
		{
			this.latitude = (latitude==null)?"":latitude;
			this.longitude = (longitude==null)?"":longitude;
			this.content = (content==null)?"":content;
			//System.out.println("new location: "+this.latitude+" "+this.longitude+this.content);
		}

		public List<String> getlat_long(){
			ArrayList<String> ret = new ArrayList<String>();
			ret.add(latitude);
			ret.add(longitude);

			return ret;
		}
		public String toString()
		{
			return content+':'+latitude+':'+longitude;
		}
	}

	public void addLocation(String latitude,String longitude,String c){
		loc = new Location(latitude,longitude,c);
		//System.out.println(latitude+longitude+c);
	}
	
	public void addbid(User u,String time,String amount){
		Bid b = new Bid(u,time,amount);
		bids.add(b);
	}

	public static String parseDate(String time){

		String ret = null;
		try{
		Date parsed = xmlformat.parse(time);
		ret = sqlformat.format(parsed);
		}
		catch(ParseException e){
				System.out.println("ERROR: Cannot parse \"" + time + "\"");
			}
		return ret;
	}

	public static List<String> item_strlist(Item i)
	{
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(i.itemid);
		ret.add(i.name);
		ret.add((i.currently==null)?"NULL":i.currently);
		ret.add((i.buy_price==null)?"-1":i.buy_price);
		ret.add((i.first_bid==null)?"NULL":i.first_bid);
		ret.add(Integer.toString(i.number_of_Bids));
		if(i.loc!=null){
			ret.add((i.loc.content==null)?"NULL":i.loc.content);
			for(String locinfo:i.loc.getlat_long())
			{
				ret.add(locinfo);
			}
		}
		else{
			ret.add("");
			ret.add("");
			ret.add("");
		}
		ret.add(i.country);
		ret.add(i.started);
		ret.add(i.ends);
		ret.add(i.sel.userid);
		ret.add(i.description);
		return ret;
	}


	public static List<String> bid_strlist(Item i,Bid b){
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(i.itemid);
		ret.add(b.time);
		ret.add(b.bidder.userid);
		ret.add(b.amount);
		return ret;
	}
	public String toString(){
		return "Item:[itemid:" + this.itemid + 
						  " name:" + this.name+
						  " currently:" + this.currently +
						  " buy_price:" + this.buy_price + 
						  " first_bid" + this.first_bid +
						  "]";
	}
}




