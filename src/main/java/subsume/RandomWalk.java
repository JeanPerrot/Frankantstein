package subsume;

import ants.Aim;

public class RandomWalk extends Layer {


    private Aim current;
    private int times = 5;

    public RandomWalk(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        if (times-- == 0) {
            current = Aim.values()[Deterministic.next(4)];
            times = 5;
        }
        return Decision.move(current);
    }
}