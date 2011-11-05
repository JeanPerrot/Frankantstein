package subsume;

import ants.Tile;

import java.util.ArrayList;
import java.util.Arrays;

public class AttackCloseHill extends GoalSeek {

    public AttackCloseHill(Ant ant) {
        super(ant);
    }

    @Override
    protected void assign(Ant ant, Tile goal) {
    }

    @Override
    protected void pickGoals() {
        Tile closeHill = getCloseHill();
        if (closeHill == null) {
            goal=null;
            potentialGoals = new ArrayList<Tile>();
            return;
        }
        potentialGoals = Arrays.asList(closeHill);
    }

    private Tile getCloseHill() {
        return ant.getCloseHill();
    }
}