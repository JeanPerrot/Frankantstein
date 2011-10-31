package subsume;

import ants.Aim;

public class Wander extends Layer {

    Aim direction;


    public Wander(Ant ant) {
        super(ant);
    }

    @Override
    public Aim output() {
        return randomWalk();
    }

    private Aim randomWalk() {
        //change direction only every so often
        boolean changeDirection = (direction == null || Deterministic.next(5) >= 4);
        if (changeDirection) {
            direction = Aim.values()[Deterministic.next(4)];
        }
        return direction;
    }

}