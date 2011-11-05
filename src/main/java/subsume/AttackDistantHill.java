package subsume;

import ants.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class AttackDistantHill extends GoalSeek {

    public AttackDistantHill(Ant ant) {
        super(ant);
    }

    @Override
    protected void assign(Ant ant, Tile goal) {
    }

    @Override
    protected void pickGoals() {
        Set<Tile> enemyHills = this.ant.getWorldMap().getEnemyHills();
        if (enemyHills.isEmpty()) {
            return;
        }
        Tile closestHill = ant.ants.closest(ant.tile, enemyHills);
        //TODO magic numbers...
        if (ant.ants.getMyAnts().size() < 20 && ant.ants.getDistance(ant.tile, closestHill) > 50) {
            potentialGoals = new ArrayList<Tile>();
            goal = null;
            return;
        }
        potentialGoals = Arrays.asList(closestHill);
        goal = closestHill;
    }
}