package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Rotate;

/**
 * the meat of the q-learning algo
 */
public class QLearning {

    private QValueRepo repo = new QValueRepo();
//    private Gson gson = new Gson();


    private Double learningRate = 0.5;
    private Double discount = 0.9;

    public void learn(FightState state, Decision currentDecision, Double reward) {
        if (state == null) {
            //cute baby ant
            return;
        }
        for (Aim aim : Aim.values()) {
            CondensedState directionalState = state.getDirectionalState(aim);
            Decision rotated = Rotate.rotate(currentDecision, aim);
            learn(directionalState, rotated, reward);
        }
    }

    private void learn(CondensedState directionalState, Decision rotated, Double reward) {
//        Print.log(gson.toJson(new LearningTuple(directionalState, reward, rotated)));

//        Double existing = repo.getValue(directionalState, rotated);
//        Map<Double, Set<Decision>> bestDecisions = repo.getBestDecisions(directionalState);
//        Double bestFutureValue = bestDecisions.isEmpty() ? 0 : bestDecisions.keySet().iterator().next();
//        Double newValue = existing + learningRate * (reward + discount * bestFutureValue - existing);
//        repo.updateValue(directionalState, rotated, reward);
    }

    public static class LearningTuple {
        private CondensedState state;
        private Double reward;
        private Decision decision;

        private LearningTuple(CondensedState state, Double reward, Decision decision) {
            this.state = state;
            this.reward = reward;
            this.decision = decision;
        }

        public CondensedState getState() {
            return state;
        }

        public void setState(CondensedState state) {
            this.state = state;
        }

        public Double getReward() {
            return reward;
        }

        public void setReward(Double reward) {
            this.reward = reward;
        }

        public Decision getDecision() {
            return decision;
        }

        public void setDecision(Decision decision) {
            this.decision = decision;
        }
    }


}