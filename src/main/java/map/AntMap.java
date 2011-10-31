package map;

import ants.Tile;
import subsume.Ant;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AntMap {

    Map<Tile,Ant>antMap=new HashMap<Tile, Ant>();

    public Ant get(Tile tile) {
        return antMap.get(tile);
    }

    public void put(Tile tile, Ant ant){
        antMap.put(tile,ant);
    }

    public Collection<Tile> getTiles(){
        return antMap.keySet();
    }
}