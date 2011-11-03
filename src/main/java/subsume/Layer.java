package subsume;

public abstract class Layer {

    Ant ant;

    int activation=0;

    Layer nextLayer;

    Decision lastDecision;


    public Layer(Ant ant) {
        this.ant = ant;
    }

    public final Decision decide(){
        lastDecision=output();
        return lastDecision;
    }

    protected abstract Decision output();

    public  String explain(){
        return "("+lastDecision+")";
    }

}