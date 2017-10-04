import java.util.*;

public class Tile {
    public TileType type;
    public boolean ocean;

    public int x = 0;
    public int y = 0;

    public float elevation = 0;
    public float moisture = 0;
    public float heat = 0;

    public Tile(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public List<Tile> Neighbours(Tile[][] map) {
        List<Tile> neighbours = new ArrayList<Tile>();

        int max_x = map.length - 1;
        int max_y = map[0].length - 1;

        if(x > 0) neighbours.add(map[x-1][y]);
        if(y > 0) neighbours.add(map[x][y-1]);

        if(x < max_x) neighbours.add(map[x+1][y]);
        if(y < max_y) neighbours.add(map[x][y+1]);

        if(x % 2 == 0) {
            if(x < max_x && y < max_y) neighbours.add(map[x+1][y+1]);
            if(x > 0 && y < max_y) neighbours.add(map[x-1][y+1]);
        } else {
            if(x > 0 && y > 0) neighbours.add(map[x-1][y-1]);
            if(y > 0 && x < max_x) neighbours.add(map[x+1][y-1]);
        }

        return neighbours;
    }
}
