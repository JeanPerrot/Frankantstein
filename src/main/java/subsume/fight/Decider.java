package subsume.fight;

import ants.Aim;
import subsume.Decision;
import subsume.Deterministic;

import java.util.*;

//that's where the reinforcement learning action resides? NOT.

//the Q-learning should occur at the beginning of a new turn:
// - for each ant, give awards according to the new state
// - having remembered the decision made by each ant, update the corresponding Q-value using the Q-value iterative update formula

//let's have the random choice done on a completely... random basis.

public class Decider {

    private static Random random = new Random(0);

    private static float chanceOfRandom = 0.2f;
    private boolean suicideOk;

    public Decider(boolean suicideOk) {
        this.suicideOk = suicideOk;
    }

    public static void setRandomness(float chance) {
        chanceOfRandom = chance;
    }

    //TODO glue
    private QValueRepo qValueRepo;

    //apply greedy policy or random choice
    public DirectionalDecisions decide(CondensedState inAim) {
//        return decideReinforcementLearning(inAim);
        return new FightHeuristics(inAim).applyPolicy(suicideOk);
    }


    private Collection<Decision> decideReinforcementLearning(CondensedState inAim) {
        if (random.nextFloat() < chanceOfRandom) {
            return Collections.singleton(randomDecision());
        }
        return greedyPolicy(inAim);
    }

    //get the decision that maximizes the value...
    private Set<Decision> greedyPolicy(CondensedState inAim) {
        Map<Double, Set<Decision>> bestDecisions = qValueRepo.getBestDecisions(inAim);
        if (bestDecisions.isEmpty()) {
            return new HashSet<Decision>();
        }
        return bestDecisions.values().iterator().next();
    }

    private Decision randomDecision() {
        float rdm = random.nextFloat() * 10;
        if (rdm < 2) {
            return Decision.STAY;
        }
        if (rdm < 3) {
            return Decision.DONTKNOW;
        }
        return Decision.move(Aim.values()[Deterministic.next(4)]);
    }
}