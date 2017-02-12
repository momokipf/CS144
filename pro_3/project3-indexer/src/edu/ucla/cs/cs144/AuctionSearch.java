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
					System.out.println("Results found (  )"+ ": " + topdocs.totalHits);
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
		//return res.toArray(new SearchResult[res.size()]);

		return new SearchResult[0];
	}

	public SearchResult[] spatialSearch(String query, SearchRegion region,
			int numResultsToSkip, int numResultsToReturn) {
		// TODO: Your code here!
		return new SearchResult[0];
	}

	public String getXMLDataForItemId(String itemId) {
		// TODO: Your code here!
		return "";
	}
	
	public String echo(String message) {
		return message;
	}

}
