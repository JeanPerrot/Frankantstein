package subsume;

import util.Print;

public abstract class Layer {

    Ant ant;

    Decision lastDecision;


    public Layer(Ant ant) {
        this.ant = ant;
    }

    public final Decision decide(){
        long time=System.nanoTime();
        lastDecision=output();
        long elapsed = (System.nanoTime() - time)/1000;
        if (elapsed>10000)
        {
            Print.debug(this.getClass().getSimpleName() + " took " + elapsed + "micros");
        }
        return lastDecision;
    }

    protected abstract Decision output();

    public  String explain(){
        return "("+lastDecision+")";
    }



}