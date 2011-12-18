package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Print {
    private static Logger logger = Logger.getLogger("Print");

    static{
        try {
            FileHandler fileHandler = new FileHandler("/Users/jperrot/github/fun/ants/ants/antLog.log",true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            log("printing to "+fileHandler.toString());
        } catch (IOException e) {
            logger=null;
        }
    }

    private static boolean debug = true;

    public static void println(String str) {
        System.err.println(str);
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