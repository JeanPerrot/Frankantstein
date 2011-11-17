package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Rotate;

import java.util.Map;
import java.util.Set;

/**
 * the meat of the q-learning algo
 */
public class QLearning {

    private QValueRepo repo;

    private Double learningRate = 0.5;
    private Double discount = 0.9;

    public void learn(FightState state, Decision currentDecision, Double reward) {

        for (Aim aim : Aim.values()) {
            CondensedState directionalState = state.getDirectionalState(aim);
            Decision rotated = Rotate.rotate(currentDecision, aim);
            learn(directionalState, rotated, reward);
        }

        //To change body of created methods use File | Settings | File Templates.
    }

    private void learn(CondensedState directionalState, Decision rotated, Double reward) {
        Double existing = repo.getValue(directionalState, rotated);
        Map<Double, Set<Decision>> bestDecisions = repo.getBestDecisions(directionalState);
        Double bestFutureValue = bestDecisions.isEmpty() ? 0 : bestDecisions.keySet().iterator().next();
        Double newValue = existing + learningRate * (reward + discount * bestFutureValue - existing);
        repo.updateValue(directionalState, rotated, newValue);
    }

}