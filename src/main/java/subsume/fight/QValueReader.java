package subsume.fight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//TODO
public class QValueReader {

    private String fileName;

    public QValueReader(String fileName) {
        this.fileName = fileName;
    }

    public QValueRepo readFromFile() {
        return null;
    }

    public void writeToFile(QValueRepo repo) throws IOException {
        FileWriter writer = new FileWriter(fileName);
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        repo.writeTo(bufferedWriter);
        bufferedWriter.flush();
        bufferedWriter.close();

    }


}