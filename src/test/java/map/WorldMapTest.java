package map;

import org.junit.Test;

public class WorldMapTest {

    @Test
    public void dontCrash(){
        WorldMap map = new WorldMap(60, 96);
        map.getIlk(59,80);
    }


}