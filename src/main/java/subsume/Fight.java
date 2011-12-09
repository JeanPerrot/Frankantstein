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
        return resolver.resolve();
    }

    private boolean isSuicideOk() {
        //TODO - suicide is not ok if I have companions nearby
        if (areFriendsNearby()) {
            return true;
        }

        return false;
    }

    private boolean areFriendsNearby() {
        for (Tile tile : this.ant.ants.getAttackOffsets()) {
            if (tile.equals(ant.getTile())) continue;
            if (ant.getWorldMap().getIlk(tile.getRow(), tile.getCol()).equals(Ilk.MY_ANT)) {
                return true;
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