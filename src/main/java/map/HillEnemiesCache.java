package map;

import ants.Ants;
import ants.Ilk;
import ants.Tile;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HillEnemiesCache {
    private Map<Tile, Integer> enemiesNear = new HashMap<Tile, Integer>();
    public static final int HILL_RANGE = 6;

    private Ants ants;
    private static HillEnemiesCache instance;

    public static HillEnemiesCache get(Ants ants) {
        if (instance == null) {
            instance = new HillEnemiesCache(ants);
        }
        return instance;
    }


    private HillEnemiesCache(Ants ants) {
        this.ants = ants;
    }

    public int enemiesNearHill(Tile myHill) {
        if (myHill == null) {
            return 0;
        }
        Integer retValue = enemiesNear.get(myHill);
        if (retValue == null) {
            retValue = computeIt(myHill);
            enemiesNear.put(myHill, retValue);
        }
        return retValue;

    }

    public void clear() {
        enemiesNear.clear();
    }

    private int computeIt(Tile myHill) {
        final AtomicInteger counter = new AtomicInteger();
        TrueWalk.Action action = new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                if (ants.getIlk(tile).equals(Ilk.ENEMY_ANT)) {
                    counter.incrementAndGet();
                }
            }
        };

        new TrueWalk(ants.getMap()).nearWalk(myHill, HILL_RANGE, action);
        return counter.get();
    }


}