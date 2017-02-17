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
				//System.out.println(itemId);
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
				ret.append("\t<Name>"+repwithEscape(rs.getString("Name"))+"/Name>\n");
				for(String category:getcategory(conn,itemId)){
					ret.append("\t<Category>"+repwithEscape(category)+"/Category\n");
				}
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


	public String echo(String message) {
		return message;
	}

}
