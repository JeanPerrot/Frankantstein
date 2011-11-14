package util;

public class Print {
    private static boolean debug=true;
    public static void println(String str) {
        System.err.println(str);
    }

    public static void debug(String s) {
        if (debug){
            System.err.println(s);
        }
    }
}