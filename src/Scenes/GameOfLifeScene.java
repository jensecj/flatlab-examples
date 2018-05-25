import java.util.*;

import flatlab.*;
import flatlab.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class GameOfLifeScene extends Scene {

    public static final int SCREEN_WIDTH = 1000;
    public static final int SCREEN_HEIGHT = 800;

    private final int WORLD_SIZE_X = 500;
    private final int WORLD_SIZE_Y = 400;

    private int _tile_size = 64;
    private int _tile_textureid;

    private Camera cam = new Camera(SCREEN_WIDTH, SCREEN_HEIGHT);

    Random rng = new Random(42);

    boolean[][] world = new boolean[WORLD_SIZE_X][WORLD_SIZE_Y];

    boolean running = false;

    int alive_neighbours(int x, int y) {
        int alive = 0;

        if(x-1 >= 0 && y-1 >= 0) if(world[x-1][y-1]) alive++;;
        if(x-1 >= 0) if(world[x-1][y]) alive++;
        if(x-1 >= 0 && y+1 < WORLD_SIZE_Y) if(world[x-1][y+1]) alive++;

        if(y-1 >= 0) if(world[x][y-1]) alive++;
        if(y+1 < WORLD_SIZE_Y) if(world[x][y+1]) alive++;

        if(x+1 < WORLD_SIZE_X && y-1 >= 0) if(world[x+1][y-1]) alive++;
        if(x+1 < WORLD_SIZE_X) if(world[x+1][y]) alive++;
        if(x+1 < WORLD_SIZE_X && y+1 < WORLD_SIZE_Y) if(world[x+1][y+1]) alive++;

        return alive;
    }

    boolean[][] clone(boolean[][] map) {
        boolean[][] the_clone = new boolean[map.length][];
        for(int i = 0; i < map.length; i++) {
            boolean[] mat = map[i];
            int len = mat.length;
            the_clone[i] = new boolean[len];
            System.arraycopy(mat, 0, the_clone[i], 0, len);
        }

        return the_clone;
    }

    boolean[][] update_world(boolean[][] world) {
        boolean[][] next_world = clone(world);

        for(int x = 0; x < WORLD_SIZE_X; x++)
            for(int y = 0; y < WORLD_SIZE_Y; y++) {
                int alive = alive_neighbours(x, y);

                if(world[x][y]) {
                    if(alive < 2)
                        next_world[x][y] = false;
                    if(alive == 3 || alive == 2)
                        next_world[x][y] = true;
                    if(alive >= 4)
                        next_world[x][y] = false;
                } else {
                    if(alive == 3)
                        next_world[x][y] = true;
                }
            }

        return next_world;
    }

    public GameOfLifeScene() {
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void mouse_scroll(float x, float y) {
        cam.on_mouse_scroll(x,y);
    }

    public void key(int key, int action, int mods) {
        cam.on_key(key, action, mods);

        if (key == ',') {
            speed *= 0.9f;
            System.out.println("speed: " + speed);
        }
        if (key == '.') {
            speed *= 1.1f;
            System.out.println("speed: " + speed);
        }
        if (key == ' ' && action == 0) {
            running = !running;
            System.out.println("running: " + running);
        }
    }

    public void init() {
        _tile_textureid = TextureLoader.load("res/square512.png");

        for(int x = 0; x < WORLD_SIZE_X; x++) {
            for(int y = 0; y < WORLD_SIZE_Y; y++){
                int r = rng.nextInt(100);
                if(r < 50)
                    world[x][y] = true;
            }
        }

        cam.set_position((SCREEN_WIDTH / 4) * _tile_size,
                         (SCREEN_HEIGHT / 4) * _tile_size);

    }

    float acc = 0;
    float speed = 10f;
    public void update(float dt) {
        cam.update(dt);

        acc += dt;
        if(acc > speed) {
            acc = 0;

            if (running)
                world = update_world(world);
        }
    }

    private void draw_tiles() {
        float world_width = (SCREEN_WIDTH * (1 / cam.zoom()));
        float world_height = (SCREEN_HEIGHT * (1 / cam.zoom()));

        float min_x = cam.x() + (SCREEN_WIDTH / 2) - (world_width / 2);
        float max_x = cam.x() + (SCREEN_WIDTH / 2) + (world_width / 2);

        float min_y = cam.y() + (SCREEN_HEIGHT / 2) - (world_height / 2);
        float max_y = cam.y() + (SCREEN_HEIGHT / 2) + (world_height / 2);

        int tile_padding = 2;

        int start_x = (int)(min_x / _tile_size) - tile_padding;
        int end_x = (int)(max_x / _tile_size) + tile_padding;

        int start_y = (int)(min_y / _tile_size) - tile_padding;
        int end_y = (int)(max_y / _tile_size) + tile_padding;

        glColor3f(0.941f, 0.875f, 0.686f); // zenburn yellow

        glBindTexture(GL_TEXTURE_2D, _tile_textureid);
        glBegin(GL_QUADS);

        for(int i = start_x; i < end_x; i++) {
            for(int j = start_y; j < end_y; j++) {
                int spacing_x = _tile_size;
                int spacing_y = _tile_size;

                // dont draw tiles outside the bounds of our map
                if(i < 0 || j < 0 || i >= WORLD_SIZE_X || j >= WORLD_SIZE_Y)
                    continue;

                if(world[i][j])
                    glColor3f(0.941f, 0.875f, 0.686f);
                else
                    glColor3f(0.247f, 0.247f, 0.247f);

                glTexCoord2f(0, 0); // top left
                glVertex2f(i * spacing_x, j * spacing_y);
                glTexCoord2f(0, 1); // bottom left
                glVertex2f(i * spacing_x, j * spacing_y + _tile_size);
                glTexCoord2f(1, 1); // bottom right
                glVertex2f(i * spacing_x + _tile_size, j * spacing_y + _tile_size);
                glTexCoord2f(1, 0); // top right
                glVertex2f(i * spacing_x + _tile_size, j * spacing_y);
            }
        }
        glEnd();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void draw() {
        glPushMatrix();

        cam.draw();

        draw_tiles();

        glPopMatrix();
    }
}
