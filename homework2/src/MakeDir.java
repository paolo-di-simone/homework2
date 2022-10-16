import java.io.IOException;

public class MakeDir {

    private static final String DIRECTORY_FILE = "C:\\Users\\user\\Desktop\\dir";
    private static final String WORD_LIST = "my_wordlist.txt";

    public static void main(String[] args) throws IOException {

        FileGenerator fileGenerator = new FileGenerator(DIRECTORY_FILE, WORD_LIST);

        // Per generare la wordlist che viene poi usata per generare il nome
        // dei file del corpus
        // fileGenerator.genWordListFromFile(WORD_LIST);

        // Per cambiare il nome a tutti i file del corpus
        // fileGenerator.changeFilesName();

        // Per contare il numero medio di parole dei file del corpus
        // float s = fileGenerator.averageNumberWords();
        // System.out.println("Numero medio di parole per file: " + s);

    }

}
