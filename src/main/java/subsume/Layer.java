package subsume;

import ants.Aim;

public abstract class Layer {

    Ant ant;

    int activation=0;

    Layer nextLayer;

    public Layer(Ant ant) {
        this.ant = ant;
    }

    public Aim decide(){
        return null;
    }

    public abstract Aim output();

}