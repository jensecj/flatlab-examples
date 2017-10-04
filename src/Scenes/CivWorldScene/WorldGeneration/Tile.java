import flatlab.util.*;
import java.util.*;

public class Tile {
    public static List<Vector2f> Neighbours(int x, int y, TileType[][] map) {
        List<Vector2f> neighbours = new ArrayList<Vector2f>();

        int max_x = map.length - 1;
        int max_y = map [0].length - 1;

        if(x > 0) neighbours.add(new Vector2f(x-1, y));
        if(y > 0) neighbours.add(new Vector2f(x, y-1));

        if(x < max_x) neighbours.add(new Vector2f(x+1, y));
        if(y < max_y) neighbours.add(new Vector2f(x, y+1));

        if(x % 2 == 0) {
            if(x < max_x && y < max_y) neighbours.add(new Vector2f(x+1, y+1));
            if(x > 0 && y < max_y) neighbours.add(new Vector2f(x-1, y+1));
        } else {
            if(x > 0 && y > 0) neighbours.add(new Vector2f(x-1, y-1));
            if(y > 0 && x < max_x) neighbours.add(new Vector2f(x+1, y-1));
        }

        return neighbours;
    }
}
