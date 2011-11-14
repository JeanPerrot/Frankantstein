package subsume;

import ants.Owner;
import subsume.fight.CondensedResolver;
import subsume.fight.FightState;
import subsume.fight.HeatMap;

public class Fight extends Layer {

    public Fight(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        FightState state = getFightState();
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

        return FightState.fromHeatMaps(myAnts, myEnemies);
    }

    //TODO
    private HeatMap getMyHeat() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private HeatMap getEnemiesHeat() {
        return HeatMap.getHeat(ant.ants, Owner.ME);  //To change body of created methods use File | Settings | File Templates.
    }

}