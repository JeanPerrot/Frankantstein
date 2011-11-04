import ants.Tile;
import subsume.TurnCount;

import java.io.*;

public class Util {

    public static void main(String[] args) throws IOException {
        nullSysOut();
        TurnCount.turnStop = 112;
        TurnCount.tilestop = new Tile(21, 6);

        MyBot bot = new MyBot();
        BufferedReader fileReader = new BufferedReader(new FileReader("src/test/java/input.txt"));
        while (fileReader.ready()) {
            bot.processLine(fileReader.readLine());
        }

    }

    private static void nullSysOut() {
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
            }
        }));
    }

}