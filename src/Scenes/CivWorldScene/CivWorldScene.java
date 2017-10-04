import flatlab.*;
import flatlab.util.*;

import java.lang.Math;
import java.util.*;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class CivWorldScene extends Scene {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;

    private int _tile_size = 64;
    private int _tile_textureid;

    private final int WORLD_SIZE_X = 800;
    private final int WORLD_SIZE_Y = 600;

    private float _cam_x = 0;
    private float _cam_y = 0;
    private float _cam_x_wanted = (WORLD_SIZE_X / 2) * (_tile_size - (_tile_size / 4));
    private float _cam_y_wanted = (WORLD_SIZE_Y / 2) * _tile_size;

    private float _cam_zoom = 1;
    private float _cam_zoom_wanted = 0.1f;
    private float _cam_zoom_increment = 0.05f; // 5%

    public TileType[][] _worldmap;

    public CivWorldScene() {
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void key(int key, int action, int mods) {
        if (key == 'A') _cam_x_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'D') _cam_x_wanted += 10 * (1 / _cam_zoom);
        if (key == 'W') _cam_y_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'S') _cam_y_wanted += 10 * (1 / _cam_zoom);
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

        HexagonalWorldGenerator gen = new HexagonalWorldGenerator(0);

        _worldmap = gen.Create(WORLD_SIZE_X, WORLD_SIZE_Y);
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
        int tile_padding = 2;

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

                // dont draw tiles outside the bounds of our map
                if(i < 0 || j < 0 || i >= WORLD_SIZE_X || j >= WORLD_SIZE_Y)
                    continue;

                switch (_worldmap[i][j]) {
                case Land: glColor3f(0.2f, 0.7f, 0.4f); break;
                case Ocean: glColor3f(0.02f, 0.15f, 0.5f); break;
                case LowWater: glColor3f(0.5f, 0.6f, 8f); break;
                case Desert: glColor3f(1f, 0.9f, 0.7f); break;
                case Forest: glColor3f(0f, 0.5f, 0.1f); break;
                case Beach: glColor3f(095f, 0.85f, 0.65f); break;
                case Snow: glColor3f(0.9f, 0.96f, 0.9f); break;
                case Plains: glColor3f(0.71f, 0.69f, 0.631f); break;
                case Tundra: glColor3f(0.68f, 0.78f, 0.8f); break;
                case Swamp: glColor3f(0.4f, 0.5f, 0.15f); break;
                case RockyHills: glColor3f(0.7f, 0.7f, 0.7f); break;
                case Missing: glColor3f(1f,1f,1f); break;
                }

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
