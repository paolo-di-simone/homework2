import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // Directory corpus: C:\\Users\\user\\Desktop\\dir
    // Directory index: C:\\Users\\user\\Desktop\\index

    private static final String DIRECTORY_FILE = "C:\\Users\\user\\Desktop\\dir";
    private static final String DIRECTORY_INDEX = "C:\\Users\\user\\Desktop\\index";

    public static void main(String[] args) throws IOException, ParseException {

        // Per indicizzare documenti
        indexDocs();

        // Test tempi di indicizzazione
        // testIndexing(10);

        // Query
        Scanner input = new Scanner(System.in).useDelimiter("\n");
        long start = 0, end = 0;
        while (true){
            System.out.print("Campo su cui fare query, nome file (1), contenuto (2): ");
            int a = input.nextInt();
            System.out.print("Cerca: ");
            String s = input.next();
            if(a == 1){
                start = System.currentTimeMillis();
                nameQuery(s, 10);
                end = System.currentTimeMillis();
            }else if (a == 2){
                start = System.currentTimeMillis();
                contentQuery(s, 10);
                end = System.currentTimeMillis();
            }
            System.out.println("Tempo di risposta query: " + ((end - start) / 1000F) + " (s), " + (end - start) + " (ns)");
        }

    }

    private static void testIndexing(int n) throws IOException {
        double sum = 0;
        for (int i = 0; i < n; i++){
            long start = System.currentTimeMillis();
            indexDocs();
            long end = System.currentTimeMillis();
            sum = sum + (end - start);
            System.out.println("Indicizzazione " + (i + 1) + " effettuata in: " + ((end - start) / 1000F) + "(s), " + (end - start) + "(ns)");
        }
        System.out.println("Tempo medio indicizzazione (" + n + " indicizzazioni effettuate): " + (sum / n));
    }

    private static void nameQuery(String q, int top) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(DIRECTORY_INDEX));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);

        CharArraySet stopWords = new CharArraySet(Arrays.asList("txt"), true);
        QueryParser parser = new QueryParser("name", new StopAnalyzer(stopWords));
        Query query = parser.parse(q);
        TopDocs hits = searcher.search(query, top);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Documento " + scoreDoc.doc + ": "+ doc.get("name") + " (" + scoreDoc.score +")");
        }
        directory.close();
    }

    private static void contentQuery(String q, int top) throws IOException, ParseException {
        Directory directory = FSDirectory.open(Paths.get(DIRECTORY_INDEX));
        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser("content", new EnglishAnalyzer());
        Query query = parser.parse(q);
        TopDocs hits = searcher.search(query, top);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("Documento " + scoreDoc.doc + ": "+ doc.get("name") + " (" + scoreDoc.score +")");
        }
        directory.close();
    }

    private static void indexDocs() throws IOException {

        Map<String, Analyzer> perFieldAnalyzers = new HashMap<>();

        CharArraySet stopWords = new CharArraySet(Arrays.asList("txt"), true);
        perFieldAnalyzers.put("name", new StopAnalyzer(stopWords));
        perFieldAnalyzers.put("content", new EnglishAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzers);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setCodec(new SimpleTextCodec());

        Directory directory = FSDirectory.open(Paths.get(DIRECTORY_INDEX));
        IndexWriter writer = new IndexWriter(directory, config);

        // Per test, si cancella l'indice precedente
        writer.deleteAll();

        File dir = new File(DIRECTORY_FILE);
        for (File f : dir.listFiles()){
            Document doc = new Document();
            // Anche StringField per campo name
            doc.add(new TextField("name", f.getName(), Field.Store.YES));
            String content = Files.readString(f.toPath());
            doc.add(new TextField("content", content, Field.Store.YES));
            writer.addDocument(doc);
        }

        writer.commit();
        writer.close();

    }

}
