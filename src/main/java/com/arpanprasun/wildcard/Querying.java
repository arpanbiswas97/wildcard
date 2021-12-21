package com.arpanprasun.wildcard;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Querying {
    private static int numdocs;
    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = "indexedData";

        IndexSearcher searcher = createSearcher(indexPath);

        long startTime = System.currentTimeMillis();

        TopDocs foundArticles = wcSearch("*king", searcher);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("About " + foundArticles.totalHits + " (" + elapsedTime +" ms)");

        for(ScoreDoc sd : foundArticles.scoreDocs) {
            Document d = searcher.doc(sd.doc);
            System.out.println(d.get("word"));
            // System.out.println("Meaning : "+ d.get("meaning"));
        }

    }
    static TopDocs wcSearch(String searchText, IndexSearcher searcher) throws IOException, ParseException {
        Query wcQuery = new WildcardQuery(new Term("word", searchText));
        TopDocs hits = searcher.search(wcQuery, numdocs);
        return hits;
    }
    static IndexSearcher createSearcher(String path) throws IOException {
        Directory dir = FSDirectory.open(new File(path));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        numdocs = reader.numDocs();
        return searcher;
    }
}
