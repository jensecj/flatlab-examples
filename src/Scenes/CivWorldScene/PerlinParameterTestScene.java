import flatlab.*;
import flatlab.util.*;
import flatlab.util.TextureLoader;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class PerlinParameterTestScene extends Scene {

    private final float SCREEN_WIDTH = 800;
    private final float SCREEN_HEIGHT = 600;

    private int _tile_size = 64;
    private int _tile_textureid;

    private final int WORLD_SIZE_X = 800;
    private final int WORLD_SIZE_Y = 600;

    private float _cam_x = 0;
    private float _cam_y = 0;
    private float _cam_x_wanted = (WORLD_SIZE_X / 2) * (_tile_size - (_tile_size / 4));
    private float _cam_y_wanted = (WORLD_SIZE_Y / 2) * _tile_size;

    private float _cam_zoom = 1;
    private float _cam_zoom_wanted = 0.01f;
    private float _cam_zoom_increment = 0.05f; // 5%

    private boolean[][] worldmap = new boolean[WORLD_SIZE_X][WORLD_SIZE_Y];

    private PerlinNoise _perlin;

    public PerlinParameterTestScene() {
        super(800,600);
    }

    public void key(int key, int action, int mods) {
        if (key == 'A') _cam_x_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'D') _cam_x_wanted += 10 * (1 / _cam_zoom);
        if (key == 'W') _cam_y_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'S') _cam_y_wanted += 10 * (1 / _cam_zoom);

        if (action == GLFW_PRESS) {
            if (key == '1') {
                if (mods == GLFW_MOD_CONTROL) _seed++;
                else if (mods == GLFW_MOD_SHIFT) _seed--;
                System.out.println("Set seed to " + _seed);
                make_world();
            }
            if (key == '2') {
                if (mods == GLFW_MOD_CONTROL) _frequency += 0.1f;
                else if (mods == GLFW_MOD_SHIFT) _frequency -= 0.1f;
                System.out.println("Set frequency to " + _frequency);
                make_world();
            }
            if (key == '3') {
                if (mods == GLFW_MOD_CONTROL) _gain += 0.01f;
                else if (mods == GLFW_MOD_SHIFT) _gain -= 0.01f;
                System.out.println("Set gain to " + _gain);
                make_world();
            }
            if (key == '4') {
                if (mods == GLFW_MOD_CONTROL) _lacunarity += 0.01f;
                else if (mods == GLFW_MOD_SHIFT) _lacunarity -= 0.01f;
                System.out.println("Set lacunarity to " + _lacunarity);
                make_world();
            }
            if (key == '5') {
                if (mods == GLFW_MOD_CONTROL) _octaves++;
                else if (mods == GLFW_MOD_SHIFT) _octaves--;
                System.out.println("Set octaves to " + _octaves);
                make_world();
            }
            if (key == '6') {
                if (mods == GLFW_MOD_CONTROL) _persistance += 0.1f;
                else if (mods == GLFW_MOD_SHIFT) _persistance -= 0.1f;
                System.out.println("Set persistance to " + _persistance);
                make_world();
            }
            if (key == '7') {
                if (mods == GLFW_MOD_CONTROL) _smoothingfactor += 0.1f;
                else if (mods == GLFW_MOD_SHIFT) _smoothingfactor -= 0.1f;
                System.out.println("Set smoothingfactor to " + _smoothingfactor);
                make_world();
            }
            if (key == '8') {
                if (mods == GLFW_MOD_CONTROL) _smoothingiterations++;
                else if (mods == GLFW_MOD_SHIFT) _smoothingiterations--;
                System.out.println("Set smoothingiterations to " + _smoothingiterations);
                make_world();
            }
            if (key == '9') {
                System.out.println("(" +
                                   _seed + ", " +
                                   _frequency + "f, " +
                                   _gain + "f, " +
                                   _lacunarity + "f, " +
                                   _octaves + ", " +
                                   _persistance + "f, " +
                                   _smoothingfactor + "f, " +
                                   _smoothingiterations +
                                   ")");
            }
        }
    }

    public void mouse_scroll(float x, float y) {
        if(y > 0)
            _cam_zoom_wanted *= 1 + _cam_zoom_increment;
        else
            if (_cam_zoom_wanted > 0f)
                _cam_zoom_wanted *= 1 - _cam_zoom_increment;
    }

    public void init() {
        _tile_textureid = TextureLoader.load("res/hexagon512.png");

        make_world();
    }

    public int _seed = 0;
    public float _frequency = 0.7f;
    public float _gain = 0.099f;
    public float _lacunarity = 3;
    public int _octaves = 4;
    public float _persistance = 4;
    // the next dont mean a whole lot in a tile world
    public float _smoothingfactor = 0;
    public int _smoothingiterations = 0;

    public void make_world() {
        _perlin = new PerlinNoise(_seed, _frequency, _gain, _lacunarity, _octaves, _persistance, _smoothingfactor, _smoothingiterations);

        for (int x = 0; x < WORLD_SIZE_X; x++)
            for (int y = 0; y < WORLD_SIZE_Y; y++)
                if (_perlin.get(x, y) < 0.8f)
                    worldmap[x][y] = true;
                else
                    worldmap[x][y] = false;
    }

    public void update(float dt) {
        if (_cam_zoom != _cam_zoom_wanted)
            _cam_zoom = Flatmath.lerp(_cam_zoom, _cam_zoom_wanted, 0.001f);

        if (_cam_x_wanted != _cam_x)
            _cam_x = Flatmath.lerp(_cam_x, _cam_x_wanted, 0.01f);

        if (_cam_y_wanted != _cam_y)
            _cam_y = Flatmath.lerp(_cam_y, _cam_y_wanted, 0.01f);
    }

    public void draw() {
        glPushMatrix();

        // transform to the camera
        glTranslatef(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        glScalef(_cam_zoom, _cam_zoom, 1);
        glTranslatef(-(SCREEN_WIDTH / 2), -(SCREEN_HEIGHT / 2), 0);

        glTranslatef(-_cam_x, -_cam_y, 0);

        draw_tiles();

        glPopMatrix();
    }

    private void draw_tiles() {
        glColor3f(0.941f, 0.875f, 0.686f); // zenburn yellow
        glBindTexture(GL_TEXTURE_2D, _tile_textureid);
        glBegin(GL_QUADS);

        float world_width = (SCREEN_WIDTH * (1 / _cam_zoom));
        float world_height = (SCREEN_HEIGHT * (1 / _cam_zoom));

        float min_x = _cam_x + (SCREEN_WIDTH / 2) - (world_width / 2);
        float max_x = _cam_x + (SCREEN_WIDTH / 2) + (world_width / 2);

        float min_y = _cam_y + (SCREEN_HEIGHT / 2) - (world_height / 2);
        float max_y = _cam_y + (SCREEN_HEIGHT / 2) + (world_height / 2);

        // the tiles overlap in size by (tile_size / 4) because of the way they are aligned
        int overlap = _tile_size / 4;
        int tile_padding = 0;

        int start_x = (int)(min_x / (_tile_size - overlap)) - tile_padding;
        int end_x = (int)(max_x / (_tile_size - overlap)) + tile_padding;

        int start_y = (int)(min_y / _tile_size) - tile_padding;
        int end_y = (int)(max_y / _tile_size) + tile_padding;

        for(int i = start_x; i < end_x; i++) {
            for(int j = start_y; j < end_y; j++) {
                int spacing_x = _tile_size;
                int spacing_y = _tile_size;

                // move tiles to the left by their index, to place then where they need to be
                int padding_x = -1 * i * (_tile_size / 4);
                int padding_y = 0;

                // every second column we move down by (tile_size / 2)
                if (i % 2 == 0)
                    padding_y = _tile_size / 2;

                if ( i > 0 && j > 0 && i < WORLD_SIZE_X && j < WORLD_SIZE_Y) {
                    float c = _perlin.get(i, j);
                    System.out.println(c);
                    glColor3f(c, c, c);
                }
                // if(worldmap[i][j])
                //     glColor3f(0, 0, 0);
                // else
                //     continue;// glColor3f(1, 1, 1);
                else
                    continue;

                glTexCoord2f(0, 0); // top left
                glVertex2f(i * spacing_x + padding_x, j * spacing_y + padding_y);
                glTexCoord2f(0, 1); // bottom left
                glVertex2f(i * spacing_x + padding_x, j * spacing_y + _tile_size + padding_y);
                glTexCoord2f(1, 1); // bottom right
                glVertex2f(i * spacing_x + padding_x + _tile_size, j * spacing_y + _tile_size + padding_y);
                glTexCoord2f(1, 0); // top right
                glVertex2f(i * spacing_x + padding_x + _tile_size, j * spacing_y + padding_y);
            }
        }
        glEnd();
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
