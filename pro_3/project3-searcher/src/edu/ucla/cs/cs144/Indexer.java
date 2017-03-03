package edu.ucla.cs.cs144;

import java.io.IOException;
import java.io.StringReader;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Indexer {

    private final String str_getitem = "SELECT i.ItemID,Name,Category,Description FROM Items i join Categorys c on i.ItemID=c.ItemID" ;


    
    /** Creates a new instance of Indexer */

    public Indexer() {
    }
 
    private IndexWriter indexWriter = null;
    
    private IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File(Lindex_path.lucene_path));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
   }


    public void rebuildIndexes() {


        Connection conn = null;

        // create a connection to the database to retrieve Items from MySQL
    try {
        conn = DbManager.getConnection(true);
    } catch (SQLException ex) {
        System.out.println(ex);
    }


    /*
     * Add your code here to retrieve Items using the connection
     * and add corresponding entries to your Lucene inverted indexes.
         *
         * You will have to use JDBC API to retrieve MySQL data from Java.
         * Read our tutorial on JDBC if you do not know how to use JDBC.
         *
         * You will also have to use Lucene IndexWriter and Document
         * classes to create an index and populate it with Items data.
         * Read our tutorial on Lucene as well if you don't know how.
         *
         * As part of this development, you may want to add 
         * new methods and create additional Java classes. 
         * If you create new classes, make sure that
         * the classes become part of "edu.ucla.cs.cs144" package
         * and place your class source files at src/edu/ucla/cs/cs144/.
     * 
     */

    try{
        Statement s = conn.createStatement();

        ResultSet rs;
        int count = 0;

        rs = s.executeQuery(str_getitem);
        while(!rs.isAfterLast()){
            if(rs.isBeforeFirst())
                rs.next();
            String id = rs.getString("ItemID");
            Item_text item_t = new Item_text(id,rs.getString("Name"),rs.getString("Description"));
            while(rs.next()&&id.equals(rs.getString("ItemID"))){
                item_t.addCategory(rs.getString("Category"));  
            }
            IndexItem_text(item_t);
            count++;
        }


        rs.close();
        s.close();
        System.out.println(count + "s Item has been add to lucene index");
        IndexWriter writer = getIndexWriter(false);
        writer.close();
    }catch(Exception ex){
        System.out.println(ex);
    }

        // close the database connection
    try {
        conn.close();
    } catch (SQLException ex) {
        System.out.println(ex);
    }
    }    





    public void IndexItem_text(Item_text item_t)
        throws IOException{
        //System.out.println("Indexing item: " +  item_t);
        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        doc.add(new StringField("ItemID",item_t.getItem_id(),Field.Store.YES));
        doc.add(new TextField("Name",item_t.getName(),Field.Store.YES));
        // doc.add(new StringField("Country",item_t.getCountry(),Field.Store.NO));
        doc.add(new TextField("Category",item_t.getCategorys(),Field.Store.NO));
        //System.out.println(item_t.getCategorys());
        doc.add(new TextField("Description",item_t.getDescription(),Field.Store.NO));
        doc.add(new TextField("Content",item_t.getName()+' '+item_t.getCategorys()+' '+item_t.getDescription(),Field.Store.NO));

        writer.addDocument(doc);
    }




    public static void main(String args[]) {
        Indexer idx = new Indexer();
        idx.rebuildIndexes();
    }   
}
