package subsume;

import ants.Ilk;
import ants.Owner;
import ants.Tile;
import subsume.fight.CondensedResolver;
import subsume.fight.FightState;
import subsume.fight.HeatMap;

public class Fight extends Layer {

    FightState lastState;

    public Fight(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        FightState state = getFightState();
        lastState = state;
        return pickDecision(state);
    }

    private Decision pickDecision(FightState state) {
        CondensedResolver resolver = new CondensedResolver(state, isSuicideOk());
        Decision resolve = resolver.resolve();
        if (resolve.action == Decision.Action.DONTKNOW) {
            if (areEnemiesNearby()) {
                return Decision.STAY;
            }
        }
        return resolve;
    }

    private boolean isSuicideOk() {
        //being more aggressive with friends around
        if (countFriendsNearby() > 10) {
            return true;
        }

        return false;
    }

    private int countFriendsNearby() {
        int count = 0;
        for (Tile tile : this.ant.ants.getAttackOffsets()) {
            if (tile.equals(ant.getTile())) continue;
            if (ant.getWorldMap().getIlk(tile.getRow(), tile.getCol()).equals(Ilk.MY_ANT)) {
                count++;
            }
        }
        return count;
    }

    private boolean areEnemiesNearby() {
        HeatMap enemiesHeat = getEnemiesHeat();
        int expRadius = 4;
        for (int i = -expRadius; i < expRadius; i++) {
            for (int j = -expRadius; j < expRadius; j++) {
                int exposure = enemiesHeat.exposure(ant.tile.getRow() + i, ant.getTile().getCol() + j);
                if (exposure > 0) {
                    return true;
                }
            }
        }
        return false;
    }


    private FightState getFightState() {
        //get the fight state given maps...
        HeatMap myEnemies = getEnemiesHeat();
        HeatMap myAnts = getMyHeat();

        return FightState.fromHeatMaps(ant, myAnts, myEnemies, ant.getWorldMap());
    }

    private HeatMap getMyHeat() {
        return HeatMap.getMyHeat(ant.ants);
    }

    private HeatMap getEnemiesHeat() {
        return HeatMap.getHeat(ant.ants, Owner.ME);
    }

    public FightState getLastState() {
        return lastState;
    }
}