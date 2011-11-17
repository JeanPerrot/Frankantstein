package subsume;

import ants.Owner;
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
        CondensedResolver resolver = new CondensedResolver(state);
        return resolver.resolve();
    }


    private FightState getFightState() {
        //get the fight state given maps...
        HeatMap myEnemies = getEnemiesHeat();
        HeatMap myAnts = getMyHeat();

        return FightState.fromHeatMaps(ant,myAnts, myEnemies);
    }

    private HeatMap getMyHeat() {
        return HeatMap.getMyHeat(ant.ants);
    }

    private HeatMap getEnemiesHeat() {
        return HeatMap.getHeat(ant.ants, Owner.ME);  //To change body of created methods use File | Settings | File Templates.
    }

    public FightState getLastState() {
        return lastState;
    }
}