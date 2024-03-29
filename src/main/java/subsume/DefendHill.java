package subsume;

import ants.Aim;
import ants.Ilk;
import ants.Tile;
import map.HillEnemiesCache;
import subsume.algo.AStar;

public class DefendHill extends Layer {

    public static int defenders = 0;

    private boolean defending = false;
    private Tile defenseSpot;

    private Fight fight;


    public DefendHill(Ant ant) {
        super(ant);
        fight = new Fight(ant);
    }

    @Override
    protected Decision output() {
        if (shouldDefend()) {
            defenseSpot = getDefenseSpot();
            if (defenseSpot == null) {
                return Decision.DONTKNOW;
            }
            defending = true;
            defenders++;
        }
        if (shouldStopDefending()) {
            defending = false;
            defenders--;
            defenseSpot = null;
            return Decision.DONTKNOW;
        }
        if (defending) {
            if (areEnemiesVisible()) {
                Decision fightDecision = fight.output();
                if (!fightDecision.equals(Decision.DONTKNOW)) {
                    return fightDecision;
                }
            }

            Aim aim = findPath(ant.tile, defenseSpot);
            if (aim == null) {
                return Decision.STAY;
            }
            return Decision.move(aim);
        } else {
            return Decision.DONTKNOW;
        }
    }

    private boolean shouldDefend() {
        return !defending && defenders < getDesiredDefenders();
    }

    @Override
    public String explain() {
        return "(" + lastDecision + ")";
    }

    private boolean shouldStopDefending() {
        //stop defending if we dont have enough ants
        return (defenders > getDesiredDefenders());
    }

    private boolean areEnemiesVisible() {
        for (Tile tile : this.ant.ants.getVisionOffsets()) {
            if (tile.equals(ant.getTile())) continue;
            if (ant.getWorldMap().getIlk(tile.getRow(), tile.getCol()).equals(Ilk.ENEMY_ANT)) {
                return true;
            }
        }
        return false;
    }


    private Tile getDefenseSpot() {
        Tile myHill = getMyHill();
        if (myHill == null) return null;
        if (!near(myHill)) return null;
        return findDefenseSpot(myHill, getDesiredDefenders());
    }

    private Aim findPath(Tile tile, Tile defenseSpot) {
        return AStar.firstStep(ant.ants, tile, defenseSpot);
    }

    private Tile findDefenseSpot(Tile myHill, int defenders) {
        if (defenders == 0) {
            return null;
        }
        outer:
        for (int distance = 1; distance < 5; distance++) {
            for (int i = -distance; i <= distance; i++) {
                for (int j = -distance; j <= distance; j++) {
                    if (i == 0 || j == 0) {
                        continue;
                    }
                    Ilk ilk = ant.ants.getMap().getIlk(myHill.getRow() + i, myHill.getCol() + j);
                    if (ilk.isPassable() && !ilk.equals(Ilk.MY_ANT)) {
                        return ant.ants.getMap().getTile(myHill.getRow() + i, myHill.getCol() + j);
                    } else defenders--;
                    if (defenders == 0) {
                        break outer;
                    }
                }
            }
        }
        return null;
    }

    private boolean near(Tile myHill) {
        return ant.ants.getDistance(ant.tile, myHill) < 60;
    }

    private Tile getMyHill() {
        return ant.ants.closest(ant.tile, ant.ants.getMyHills());
    }

    private int getDesiredDefenders() {
        int myNums = ant.ants.getMyAnts().size();
        int enemyNear = HillEnemiesCache.get(ant.ants).enemiesNearHill(getMyHill());
        return enemyNear + Math.min(myNums / 10, 2);
    }


}