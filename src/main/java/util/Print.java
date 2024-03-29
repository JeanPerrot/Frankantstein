package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Print {
    private static Logger logger = Logger.getLogger("Print");
    private static boolean debug = true;

    static{
        try {
            FileHandler fileHandler = new FileHandler("/Users/jperrot/github/fun/ants/ants/antLog.log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger=null;
        }
    }


    public static void println(String str) {
        if (debug){
            System.err.println(str);
        }
    }

    public static void debug(String s) {
        if (debug) {
            System.err.println(s);
        }
    }

    public static void log(String s) {
        if (logger!=null)
            logger.log(Level.INFO, s);
    }
}