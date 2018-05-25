import java.util.*;

import flatlab.*;
import flatlab.util.*;
import flatlab.util.Flatdraw.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class DragonFractalScene extends Scene {
    float px = 400;
    float py = 300;

    float zoom = 1;

    /*
     *     3
     *  2  x  0
     *     1
     */
    int direction = 0;

    final int iterations = 5000000;

    float[] _points = new float[iterations * 2];

    public DragonFractalScene() {
        super(800,600);
    }

    public void key(int key, int action, int mods) {
        if (key == 'A')
            px += 10;

        if (key == 'D')
            px -= 10;

        if (key == 'W')
            py += 10;

        if (key == 'S')
            py -= 10;

        if (key == 'Z')
            if (zoom > 0)
                zoom -= 0.01f;

        if (key == 'X')
            zoom += 0.01f;
    }

    public void init() {}

    public void update(float dt) {
        float x = px;
        float y = py;

        for (int i = 0; i < _points.length; i++) {
            boolean turn = (((i & -i) << 1) & i) != 0;

            if (turn) // turn right
                direction = (direction + 1) % 4;
            else // turn left
                direction = (direction + 3) % 4;

            switch (direction) {
            case 0:
                x++;
                break;
            case 1:
                y++;
                break;
            case 2:
                x--;
                break;
            case 3:
                y--;
                break;
            }

            _points[i] = x;
            _points[++i] = y;
        }
    }

    public void draw() {
        glColor3f(0.941f, 0.875f, 0.686f); // zenburn yellow

        glTranslatef(400, 300, 0);
        glScalef(zoom, zoom, 1);
        glTranslatef(-400, -300, 0);

        Flatdraw.batch(GL_POINTS, () -> {
                for(int i = 0; i < _points.length; i++)
                    Flatdraw._draw_point(new Vector2f(_points[i], _points[++i]));
            });
    }
}
