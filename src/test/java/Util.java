import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Util {

    public static void main(String[] args) throws IOException {
        MyBot bot=new MyBot();
        BufferedReader fileReader = new BufferedReader(new FileReader("ants/src/test/java/input.txt"));
        while(fileReader.ready()){
            bot.processLine(fileReader.readLine());
        }

    }

}