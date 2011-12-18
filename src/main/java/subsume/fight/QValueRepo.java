package subsume.fight;

import com.google.gson.Gson;
import subsume.Decision;
import util.Print;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;

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

    //TODO this doesn't update, it overwrites. not cool
    public void updateValue(CondensedState state, Decision decision, Double value) {
        Map<Decision, Double> valuesMap = repo.get(state);
        if (valuesMap == null) {
            valuesMap = new HashMap<Decision, Double>();
            repo.put(state, valuesMap);
        }
        valuesMap.put(decision, value);

        //TODO value and decision
//        Print.debug(gson.toJson(new LearningTuple(state,value,decision)));
    }


    public Map<Double, Set<Decision>> getBestDecisions(CondensedState inAim) {
        Map<Decision, Double> decisions = repo.get(inAim);
        if (decisions == null) {
            return new HashMap<Double, Set<Decision>>();
        }
        Map<Double, Set<Decision>> sorted = new HashMap<Double, Set<Decision>>();
        for (Map.Entry<Decision, Double> decision : decisions.entrySet()) {
            Set<Decision> sortedDecisions = sorted.get(decision.getValue());
            if (sortedDecisions == null) {
                sortedDecisions = new HashSet<Decision>();
                sorted.put(decision.getValue(), sortedDecisions);
            }
            sortedDecisions.add(decision.getKey());
        }
        List<Double> sortedKeys = new ArrayList<Double>(sorted.keySet());
        Collections.sort(sortedKeys);
        Double max = sortedKeys.get(sortedKeys.size() - 1);
        Map<Double, Set<Decision>> retValue = new HashMap<Double, Set<Decision>>();
        retValue.put(max, sorted.get(max));
        return retValue;
    }

    public void writeTo(BufferedWriter writer) throws IOException {
        for (Map.Entry<CondensedState, Map<Decision, Double>> entry : repo.entrySet()) {
            writer.write(entry.getKey().asChars());
            writer.newLine();
            for (Map.Entry<Decision, Double> qValue : entry.getValue().entrySet()) {
                writer.write(qValue.getKey().toString());
                //TODO...

            }
        }
    }



   }