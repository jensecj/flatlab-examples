import java.util.*;

public class HexagonalWorldGenerator {
    public static TileType[][] Create(int seed, int WORLD_SIZE_X, int WORLD_SIZE_Y) {
        long timer_start = System.currentTimeMillis();

        // initialize the map
        TileType[][] map = new TileType[WORLD_SIZE_X][WORLD_SIZE_Y];
        for (int x = 0; x < map.length; x++)
            for (int y = 0; y < map[x].length; y++) {
                map[x][y] = TileType.Ocean;
            }

        // create the world map
        PerlinNoise elevation_map = new PerlinNoise(seed, 0.9f, 0.089f, 2.96f, 4, 4f, 0f, 0);
        PerlinNoise moisture_map = new PerlinNoise(seed, 1.1f, 0.089f, 3.02f, 4, 4.0f, 0.0f, 0);
        PerlinNoise heat_map = new PerlinNoise(seed, 0.19f, 0.089f, 2.92f, 4, 4.1f, 0.099f, 0);

        for (int x = 0; x < map.length; x++)
            for (int y = 0; y < map[x].length; y++)
                map[x][y] = get_biome(1 - elevation_map.get(x, y),
                                      1 - moisture_map.get(x, y),
                                      1 - heat_map.get(x, y));

        long timer_end = System.currentTimeMillis();
        long elapsed = timer_end - timer_start;
        System.out.println("World generation took " + elapsed + "ms");

        return map;
    }

    private static TileType get_biome(float elevation, float moisture, float heat) {
        if(elevation < 0.10f) return TileType.Ocean;
        if(elevation < 0.15 && elevation > 0.10f)
            return TileType.LowWater;
        if(elevation > 0.15f && elevation < 0.19f && heat > 0.01f)
            return TileType.Beach;
        if(elevation > 0.455f && moisture > 0.20f && heat < 0.60f)
            return TileType.Snow;
        if(elevation > 0.20f && heat > 0.325f && moisture < 0.20f)
            return TileType.Desert;
        if(elevation > 0.25f && elevation < 0.39f && heat < 0.25f && moisture < 0.450f)
            return TileType.Forest;
        if(elevation > 0.2f && elevation < 0.3f && heat > 0.2f)
            return TileType.RockyHills;
        if(elevation > 0.40f && elevation < 0.44f && heat < 0.30f && moisture < 0.50f)
            return TileType.Plains;
        if(elevation > 0.44f && heat < 0.25f && moisture < 0.60f)
            return TileType.Tundra;
        if(elevation > 0.25f && elevation < 0.40f && moisture > 0.30f && heat > 0.30f)
            return TileType.Swamp;
        if(elevation > 0.15f)
            return TileType.Land;

        return TileType.Missing;
    }
}
