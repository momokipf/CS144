package edu.ucla.cs144;

import java.io.*;
import java.text.*;
import java.util.*;


public class User{
	public String userid = null;
	public int sellrate = -3;
	public int buyrate = -3;
	public String location = null;
	public String country=null;

	public User(String userid)
	{
		this.userid = userid;
	}

	public User(String userid , String location,String country){
		this.userid = userid;
		this.location=location;
		this.country = country;
	}

	
	public String toString(){
		return "userid: "+userid + " location:" + location + " country" + country;
	}

	public static  List<String> user_strlist(User u){
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(u.userid);
		// ret.add((u.sellrate==0)?"-1":Integer.toString(u.sellrate));
		// ret.add((u.buyrate==0)?"-1":Integer.toString(u.buyrate));
		ret.add(Integer.toString(u.sellrate));
		ret.add(Integer.toString(u.buyrate));
		ret.add(u.location);
		ret.add(u.country);
		return ret;
	}
}