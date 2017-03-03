package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.text.SimpleDateFormat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.ucla.cs.cs144.DbManager;
import edu.ucla.cs.cs144.SearchRegion;
import edu.ucla.cs.cs144.SearchResult;

public class AuctionSearch implements IAuctionSearch {

	/* 
         * You will probably have to use JDBC to access MySQL data
         * Lucene IndexSearcher class to lookup Lucene index.
         * Read the corresponding tutorial to learn about how to use these.
         *
	 * You may create helper functions or classes to simplify writing these
	 * methods. Make sure that your helper functions are not public,
         * so that they are not exposed to outside of this class.
         *
         * Any new classes that you create should be part of
         * edu.ucla.cs.cs144 package and their source files should be
         * placed at src/edu/ucla/cs/cs144.
         *
         */
	//private static final Map<String,String> ESCAPMAP = createMap();
	// private static Map<String,String> createMap(){
	// 	HashMap<String,String> tmp = new HashMap<String,String>();
	// 	tmp.put(",","&apos;");
	// 	tmp.put("\"","&quot;");
	// 	tmp.put("&","&amp;");
	// 	tmp.put("<","&lt");
	// 	tmp.put(">","&gt");
	// 	return Collections.unmodifiableMap(tmp);
	// }

	private static SimpleDateFormat xmlformat = new SimpleDateFormat("MMM-dd-yy HH:mm:ss");  //Dec-10-01 09:26:52
	private static SimpleDateFormat sqlformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final String indent = "  ";
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!
		ArrayList<SearchResult> res = new ArrayList<SearchResult>(); 
		HashSet<String> itemidset = new HashSet<String>();
		//String[] fields = { "Name", "Category", "Description"}; 


		try{
			IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(Lindex_path.lucene_path))));
			// for(int i = 0 ; i < query_index.length ; ++i)
			//{
				QueryParser parser = new QueryParser("Content", new StandardAnalyzer());
				try{
					Query query_t = parser.parse(query);
					TopDocs topdocs = searcher.search(query_t,numResultsToSkip+numResultsToReturn);
					//System.out.println("Results found (  )"+ ": " + topdocs.totalHits);
					ScoreDoc[] hits = topdocs.scoreDocs;

					for(int j =0 ; j < hits.length;j++){
						if(j < numResultsToSkip)
							continue;
						Document doc = searcher.doc(hits[j].doc);
						if(!itemidset.contains(doc.get("ItemID")))
						{
							res.add(new SearchResult(doc.get("ItemID"),doc.get("Name")));
							itemidset.add(doc.get("ItemID"));
						}	

					}

				}catch(ParseException pe){
					System.out.println(pe);
				}
			//}
		}catch(IOException e){

			System.out.println(e);
		}
		return res.toArray(new SearchResult[res.size()]);

		//return new SearchResult[0];
	}



	

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		ArrayList<SearchResult> ret = new ArrayList<SearchResult>();

		HashSet<String> withinregion = spatialSearchINsql(region);
		SearchResult[] tmp = basicSearch(query,0,numResultsToReturn);
		int searched = 0 ;
		while(ret.size()<numResultsToReturn && tmp.length!=0){
			//ret.allAll(tmp);
			for(SearchResult sr:tmp){
				if(withinregion.contains(sr.getItemId())){
					ret.add(sr);
					if(ret.size()>=numResultsToReturn)
						break;
				}
				//System.out.println("not qualified "+ sr.getItemId());
			}
			searched += numResultsToReturn;
			tmp = basicSearch(query,searched,numResultsToReturn);
			System.out.println("Now length:"+ret.size());
		}




		return ret.toArray(new SearchResult[ret.size()]);
	}


	private HashSet<String> spatialSearchINsql(SearchRegion region){
		Connection conn = null;
		HashSet<String> ret = new HashSet<String>();

        // create a connection to the database to retrieve Items from MySQL
	    try {
	        conn = DbManager.getConnection(true);
	    } catch (SQLException ex) {
	        System.out.println(ex);
	    }


	    try{
	    	Statement s = conn.createStatement();

	    	ResultSet rs;

	    	//SET @range='Polygon((33.774 -118.63,33.774 -117.38,34.201 -117.38,34.201 -118.63,33.774 -118.63))';
	    	String setrange = "\'Polygon(("+
	    					region.getLx()+' '+region.getLy()+','+
	    					region.getLx()+' '+region.getRy()+','+
	    					region.getRx()+' '+region.getRy()+','+
	    					region.getRx()+' '+region.getLy()+','+
	    					region.getLx()+' '+region.getLy()+
	    					"))\'";


	    	String query = "Select ItemID from Item_loc where MBRContains(GeomFromText("+setrange+"),g)";
	    	System.out.println(query);
			rs = s.executeQuery(query);
			while(rs.next()){
				String itemId = rs.getString("ItemID");
				ret.add(itemId);
			}


			rs.close();
			s.close();

	    }catch(Exception ex){
	    	System.out.println(ex);
	    }

	       // close the database connection
	    try {
	        conn.close();
	    } catch (SQLException ex) {
	        System.out.println(ex);
	    }   
	    return ret;

	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		Connection conn = null;
		StringBuilder ret = new StringBuilder();
		int height = 0;
		try{
			conn = DbManager.getConnection(true);
			Statement s = conn.createStatement();
			ResultSet rs;
			String query = "Select * from Items where ItemID="+itemId;
			rs = s.executeQuery(query);
			if(rs.next()){
				ret.append("<ItemID=\""+itemId+"\">\n");
				ret.append(printindent(height+1)+"<Name>"+repwithEscape(rs.getString("Name"))+"</Name>\n");
				for(String category:getcategory(conn,itemId)){
					ret.append(printindent(height+1)+"<Category>"+repwithEscape(category)+"</Category>\n");
				}
				ret.append(printindent(height+1)+"<Currently>$" + Float.toString(rs.getFloat("Currently"))+"</Currently>\n");
				if(rs.getFloat("Buy_Price")!=0.0f)
					ret.append(printindent(height+1)+"<Buy_Price>$" + Float.toString(rs.getFloat("Buy_Price"))+"</Buy_Price>\n");
				ret.append(printindent(height+1)+"<First_Bid>$" + Float.toString(rs.getFloat("First_Bid"))+"</First_Bid>\n");
				ret.append(printindent(height+1)+"<Number_of_Bids>" + Integer.toString(rs.getInt("Number_of_Bids"))+"</Number_of_Bids>\n");
				if(getBid(conn,itemId,height+1).length()!=0)
				{
					ret.append(printindent(height+1)+"<Bids>\n");
					ret.append(getBid(conn,itemId,height+2));
					ret.append(printindent(height+1)+"</Bids>\n");
				}
				//Location_geo_lat Location_geo_long Location_content
				if(rs.getString("Location_geo_lat").length()!=0)
					ret.append(printindent(height+1)+"<Location Latitude=\""+rs.getString("Location_geo_lat") +"\" Longitude=\""+rs.getString("Location_geo_long")+ "\">"+repwithEscape(rs.getString("Location_content"))+"</Location>\n");
				else
					ret.append(printindent(height+1)+"<Location>"+repwithEscape(rs.getString("Location_content"))+"</Location>\n");
				ret.append(printindent(height+1)+"<Country>"+rs.getString("Country")+"</Country>\n");
				ret.append(printindent(height+1)+"<Started>"+sqlparseDate(rs.getString("Started"))+"</Started>\n");
				ret.append(printindent(height+1)+"<Ends>"+sqlparseDate(rs.getString("Ends"))+"</Ends>\n");
				ret.append(getuser(conn,rs.getString("sellerid"),height+1,true));
				ret.append(printindent(height+1)+"<Description>" + repwithEscape(rs.getString("Description")) + "</Description>\n");
				ret.append(printindent(height)+"</Item>");
			}

			rs.close();
			s.close();
			conn.close();



		}catch(SQLException ex){
			System.out.println("SQLException caught");
            System.out.println("---");
            while ( ex != null ){
                System.out.println("Message   : " + ex.getMessage());
                System.out.println("SQLState  : " + ex.getSQLState());
                System.out.println("ErrorCode : " + ex.getErrorCode());
                System.out.println("---");
                ex = ex.getNextException();
            }
		}

		 return ret.toString();
	}
	
	/*
	' is replaced with &apos;
	" is replaced with &quot;
	& is replaced with &amp;
	< is replaced with &lt;
	> is replaced with &gt;
	*/
	private String repwithEscape(String str){
		if(str.contains("'")){
			str=str.replace("'","&apos;");
		}
		if(str.contains("\"")){
			str=str.replace("\"","&quot;");
		}
		if(str.contains("&")){
			str=str.replace("&","&amp;");
		}
		if(str.contains("<")){
			str=str.replace("<","&lt;");
		}
		if(str.contains(">")){
			str=str.replace(">","&gt");
		}
		return str;
	}

	private String[] getcategory(Connection conn,String itemId)
	throws SQLException{
		if(conn==null)
			return new String[0];
		ArrayList<String> ret = new ArrayList<String>();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("Select Category from Categorys where ItemID="+itemId);
		while(rs.next()){
			ret.add(rs.getString("Category"));
		}
		return ret.toArray(new String[ret.size()]);
	}

	private String getBid(Connection conn,String itemId,int height)
	throws SQLException{
		if(conn==null)
			return new String();
		Statement s = conn.createStatement();
		StringBuilder ret = new StringBuilder();
		ResultSet rs = s.executeQuery("Select Time,UserID,Amount from Bider where ItemID="+itemId+";");
		//System.out.println("Select Time,UserID,Amount from Bider where ItemID="+itemId);
		while(rs.next()){
			ret.append(printindent(height)+"<Bid>\n");
			
			String username = repwithEscape(rs.getString("UserID"));
			String time = sqlparseDate(rs.getString("Time"));
			System.out.println(time);
			int amount = rs.getInt("Amount");
			ret.append(getuser(conn,username,height+1,false));
			ret.append(printindent(height+1)+"<Time>"+time+"</Time>\n");
			ret.append(printindent(height+1)+"<Amount>$"+Integer.toString(amount)+"</AMount>\n");
			ret.append(printindent(height)+"</Bid>\n");

		}
		return ret.toString();
	}

	public static String sqlparseDate(String time){

		String ret = null;
		try{
			Date parsed = sqlformat.parse(time);
			ret = xmlformat.format(parsed);
		}
		catch(Exception e){
			System.out.println("ERROR: Cannot parse \"" + time + "\"");
		}

		return ret;
	}

	private String getuser(Connection conn,String username,int height,boolean isseller)
	throws SQLException{
		if(conn==null)
			return new String();
		StringBuilder ret = new StringBuilder();
		Statement s = conn.createStatement();
		String query = null;
		if(isseller){
			query = "Select * from User where UserID=\"" + username + "\"" ;
		}
		else{
			query = "Select * from User where UserID= \"" + username + "\"";
		}
		//System.out.println(query);
		ResultSet rs= s.executeQuery(query);

		if(rs.next()){
			int buyrating = rs.getInt("Buyrate");
			int sellrate = rs.getInt("Sellrate");
			if(isseller){
				ret.append(printindent(height)+"<Seller Rating=\"" + Integer.toString(sellrate)+"\" UserID=\"" + username + "\" />\n");
			}
			else{
				ret.append(printindent(height)+"<Bidder Rating=\"" + Integer.toString(sellrate)+"\" UserID=\"" + username + "\">\n");
				ret.append(printindent(height+1)+"<Location>"+rs.getString("Location")+"</Location>\n");
				ret.append(printindent(height+1)+"<Country>"+rs.getString("Country")+"</Country>\n");
				ret.append(printindent(height)+"</Bidder>\n");
			}
		}
		return ret.toString();
	}

	private String printindent(int height){
		String ret = new String();
		for(int i=0;i<height;++i){
			ret += indent ;
		}
		return ret;
	}
	public String echo(String message) {
		return message;
	}

}
