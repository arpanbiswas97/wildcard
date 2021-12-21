package com.arpanprasun.wildcard;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.json.simple.parser.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CreatePermutermIndex {
    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = "permutermIndexData";
        String inputFile = "dictionary.json";

        Directory dir = FSDirectory.open(new File(indexPath));
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
                Tokenizer tokenizer = new WhitespaceTokenizer(reader);
                TokenFilter filters = new PermutermTokenFilter(tokenizer);
                return new TokenStreamComponents(tokenizer, filters);
            }
        };
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LATEST, analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(dir, iwc);

        indexDict(writer, inputFile);
        writer.close();

    }
    static void indexDict(final IndexWriter writer, String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject jsonObj = (JSONObject)parser.parse(new FileReader(path));
        for(Iterator iterator = jsonObj.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            Document doc = new Document();
            doc.add(new TextField("word", key, Field.Store.YES));
            writer.addDocument(doc);
        }
    }
}
