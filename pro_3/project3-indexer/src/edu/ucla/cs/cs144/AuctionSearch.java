package edu.ucla.cs.cs144;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
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
	// private final String[] query_index = {
	// 	"Name",
	// 	"Category",
	// 	"Description",
	// };


	
	public SearchResult[] basicSearch(String query, int numResultsToSkip, 
			int numResultsToReturn) {
		// TODO: Your code here!

		ArrayList<SearchResult> res = new ArrayList<SearchResult>(); 
		HashSet<String> itemidset = new HashSet<String>();
		//ArrayList<TopDocs> = new ArraList<TopDocs>;
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
			/*
			while(!rs.isAfterLast()){
            if(rs.isBeforeFirst())
                rs.next();
            String id = rs.getString("ItemID");
            Item_text item_t = new Item_text(id,rs.getString("Name"),rs.getString("Description"));
            while(rs.next()&&id.equals(rs.getString("ItemID"))){
                item_t.addCategory(rs.getString("Category"));  
                //System.out.println(id+ ' '+rs.getString("Category")); 
            }
            //System.out.println(id+ ' '+item_t.getCategorys());
            IndexItem_text(item_t);
            //System.out.println("Item id:" + id + " added");
            count++;
        	}
			*/
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
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
