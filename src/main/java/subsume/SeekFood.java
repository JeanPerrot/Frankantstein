package subsume;

import ants.Ilk;

public class SeekFood extends GoalSeek {

    public SeekFood(Ant ant) {
        super(ant);
    }


    @Override
    protected void pickGoals() {
        if (goal == null || goal.equals(ant.tile) || !ant.ants.getIlk(goal).equals(Ilk.FOOD)) {
            potentialGoals=ant.getCloseFoodTiles();
        }
    }
}