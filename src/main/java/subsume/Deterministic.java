package subsume;

public class Deterministic {
    static int count=0;
    public static int next(){
        return count++;
    }

    public static int next(int upTo){
        return count++%upTo;
    }

}