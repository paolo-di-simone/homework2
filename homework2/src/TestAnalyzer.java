import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestAnalyzer {

    private static final String SAMPLE_TEXT = "Esempio_di Nome-di File199_8-ok.txt";

    public static void main(String[] args) throws IOException {

        List<String> result1 = analyze(SAMPLE_TEXT, new StandardAnalyzer());
        printToken(result1, "StandardAnalyzer");

        CharArraySet stopWords = new CharArraySet(Arrays.asList("txt"), true);
        StopAnalyzer stop = new StopAnalyzer(stopWords);
        List<String> result3 = analyze(SAMPLE_TEXT, stop);
        printToken(result3, "StopAnalyzer");

    }

    private static void printToken(List<String> result, String analyzer){
        System.out.println(analyzer);
        for (String s : result){
            System.out.println(s);
        }
        System.out.println("\n");
    }

    private static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("FIELD_NAME", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }

}
