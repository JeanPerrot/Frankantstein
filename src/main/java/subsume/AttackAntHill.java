package subsume;

import ants.Tile;

import java.util.ArrayList;
import java.util.Arrays;

public class AttackAntHill extends GoalSeek {

    public AttackAntHill(Ant ant) {
        super(ant);
    }

    @Override
    protected void pickGoals() {
        Tile closeHill = ant.getCloseHill();
        if (closeHill == null) {
            potentialGoals = new ArrayList<Tile>();
            return;
        }
        potentialGoals = Arrays.asList(closeHill);
    }
}