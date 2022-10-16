import java.io.*;
import java.util.Random;

public class FileGenerator {

    private String directory;
    private String wordList;

    private static final int MAX_N_WORDS_FILE_NAME = 10;

    private Random random;

    // Classe utilizzata per la creazione del corpus

    public FileGenerator(String directory, String wordList) {
        this.directory = directory;
        this.wordList = wordList;
        this.random = new Random(System.currentTimeMillis());
    }

    public void genWordListFromFile(String fileName) throws IOException {
        File dir = new File(directory);
        File[] list = dir.listFiles();
        File f = list[random.nextInt(list.length)];
        BufferedReader reader = new BufferedReader(new FileReader(f));

        File out = new File(fileName);
        out.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(out));

        String[] tmp = null;
        String line = null;
        while ((line = reader.readLine()) != null) {
            line = line.replaceAll(",", "");
            line = line.replaceAll("\\.", "");
            tmp = line.split(" ");
            for (String s : tmp){
                writer.write(s.trim());
                writer.write("\n");
            }
        }
        writer.flush();
        writer.close();
        reader.close();
    }

    public float averageNumberWords() throws IOException {
        File dir = new File(directory);
        float n = 0;
        long sum = 0;
        for(File f : dir.listFiles()){
            n++;
            sum = sum + countWordsFile(f);
        }
        return sum / n;
    }

    private long countWordsFile(File file) throws IOException {
        long words = 0;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] tmp = null;
        String line = null;
        while ((line = reader.readLine()) != null) {
            tmp = line.split(" ");
            words = words + tmp.length;
        }
        reader.close();
        return words;
    }

    public void changeFilesName() throws IOException {
        File dir = new File(directory);
        int numLineWordList = countLineBufferedReader(wordList);
        int titleLength = 0;
        String newName = "";
        for (File f : dir.listFiles()){
            titleLength = random.nextInt(MAX_N_WORDS_FILE_NAME) + 1;
            newName = genFileName(titleLength, numLineWordList);
            f.renameTo(new File(dir.getAbsolutePath() + File.separator + newName + ".txt"));
        }
    }

    private String genFileName(int titleLength, int numLineWordList) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < titleLength; i++){
            if (random.nextBoolean()){
                sb.append(getRandomLineFromFile(wordList, numLineWordList));
            }else if(random.nextBoolean()){
                String str = getRandomLineFromFile(wordList, numLineWordList);
                sb.append(str.substring(0, 1).toUpperCase() + str.substring(1));
            }else{
                sb.append(getRandomLineFromFile(wordList, numLineWordList).toUpperCase());
            }
            if(random.nextBoolean() && random.nextBoolean() && random.nextBoolean()){
                sb.append(random.nextInt(100));
            }
            if (i < titleLength - 1){
                if(random.nextBoolean()){
                    sb.append(" ");
                }else if(random.nextBoolean()){
                    sb.append("-");
                }else if(random.nextBoolean()){
                    sb.append("_");
                }else{
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }

    private String getRandomLineFromFile(String fileName, int numFileLines) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        int n = random.nextInt(numFileLines);
        String line = null;
        int i = 0;
        while(((line = reader.readLine()) != null) && i < n){
            i++;
        }
        reader.close();
        return line;
    }

    private int countLineBufferedReader(String fileName) throws IOException {
        int lines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        while (reader.readLine() != null) lines++;
        reader.close();
        return lines;
    }

}
