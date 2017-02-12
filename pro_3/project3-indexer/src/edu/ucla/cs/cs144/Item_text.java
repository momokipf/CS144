package edu.ucla.cs.cs144;

import java.io.*;
import java.text.*;
import java.util.*;




public class Item_text {

	private String itemid = null;

	private String name = null;
	private List<String> categorys = null;

	// private String location = null;
	// private String country = null;

	private String description = null;



	public Item_text(String id,String name,String des){
		this.itemid = id;
		this.name = name;
		// this.location = loc;
		// this.country = country;
		this.categorys = new ArrayList<String>();
		this.description = des;
	}


	public String getItem_id(){
		return this.itemid;
	}

	public String getName(){
		return this.name;
	}

	public String getCategorys(){
		String ret = new String();
		for(String cat:categorys){
			ret += cat + ' ';
		}
		return ret.substring(0,ret.length());
	}

	// public String getLocation(){
	// 	return this.location;
	// }


	// public String getCountry(){
	// 	return this.country;
	// }

	public String getDescription(){
		return this.description;
	}

	public void setItem_id(String id){
		this.itemid = id;
	}

	public void setname(String name){
		this.name = name;
	}

	public void addCategory(String cat){
		this.categorys.add(cat);
	}

	// public void setcountry(String country){
	// 	this.country = country;
	// }

	// public void setlocation(String loc){
	// 	this.location =loc;
	//}
	public void setdescription(String des){
		this.description=des;
	}
	public void addcategory(String category){
		categorys.add(category);
	}


	public String toString(){
		return "ID: "+
				getItem_id() +
				" Name: "+
				getName() +
				//" Country: "+
				// getCountry() +
				// " Location: "+
				// getLocation() + 
				 " Description: "+
				getDescription() ;
				
	}
}




