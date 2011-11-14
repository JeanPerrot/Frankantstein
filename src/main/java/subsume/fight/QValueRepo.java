package subsume.fight;

import subsume.Decision;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class QValueRepo {
    private static Double defaultValue = 0d;

    private Map<CondensedState, Map<Decision, Double>> repo = new HashMap<CondensedState, Map<Decision, Double>>();

    public Double getValue(CondensedState state, Decision decision) {
        Map<Decision, Double> valuesMap = repo.get(state);
        if (valuesMap == null) {
            return defaultValue;
        }
        Double value = valuesMap.get(decision);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void updateValue(CondensedState state, Decision decision, Double value) {
        Map<Decision, Double> valuesMap = repo.get(state);
        if (valuesMap == null) {
            valuesMap = new HashMap<Decision, Double>();
            repo.put(state, valuesMap);
        }
        valuesMap.put(decision, value);
    }


    //TODO
    public Set<Decision> getBestDecisions(CondensedState inAim) {
        Map<Decision, Double> decisions = repo.get(inAim);
        for (Decision decision:decisions.keySet()){
        }
        return null;
    }
}