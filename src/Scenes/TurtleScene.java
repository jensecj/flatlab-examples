import flatlab.*;
import flatlab.util.*;

import java.util.*;
import java.lang.Math;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class TurtleScene extends Scene {

    public final class Color {
        public final float r, g, b;
        public Color(float r, float g, float b) {
            this.r = r; this.g = g; this.b = b;
        }
    }

    public final class Point {
        public final float x, y;
        public Point(float x, float y) {
            this.x = x; this.y = y;
        }
    }

    public final class Line {
        public final Point a, b;
        public final Color c;
        public Line(Point a, Point b, Color c) {
            this.a = a; this.b = b; this.c = c;
        }
    }

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 800;

    private Camera camera = new Camera(SCREEN_WIDTH, SCREEN_HEIGHT);

    private ArrayList<Line> lines = new ArrayList<Line>();

    float sin(float a) {
        return (float)Math.sin(a);
    }

    float cos(float a) {
        return (float)Math.cos(a);
    }

    float rad(float a) {
        return Flatmath.deg2rad(a);
    }

    int round(float f) {
        return (int)Math.round(f);
    }

    public TurtleScene() {
        super(SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    public void mouse_scroll(float x, float y) {
        camera.on_mouse_scroll(x,y);
    }

    public void key(int key, int action, int mods) {
        camera.on_key(key, action, mods);
    }

    public void init() {
        camera.set_zoom(0.040f);
    }

    int counter = 0;
    float time = 0;
    public void update(float dt) {
        time += dt;

        camera.update(dt);

        if (counter < 360*2) {
            time = 0;
            counter += 1;

            float low = 2500;
            float mid = low * 2;
            float high = mid * 2;

            Point a = new Point(low * sin(rad(counter)), mid * cos(rad(counter)));
            Point b = new Point(mid * sin(rad(counter)), high * cos(rad(counter)));
            Point c = new Point(high * sin(rad(counter)), low * cos(rad(counter)));

            float cr = sin((0.05f * Flatmath.PI/3) * counter + Flatmath.PI/3)   * 0.5f + (360*2)/counter;
            float cg = sin((0.05f * Flatmath.PI/3) * counter + 2*Flatmath.PI/3) * 0.5f + (360*2)/counter;
            float cb = sin((0.05f * Flatmath.PI/3) * counter + 4*Flatmath.PI/3) * 0.5f + (360*2)/counter;

            Color col = new Color(cr, cg, cb);

            lines.add(new Line(a, b, col));
            lines.add(new Line(b, c, col));
            lines.add(new Line(c, a, col));
        }
    }

    public void draw_lines() {
        glBegin(GL_LINES);

        for (Line l : lines) {
            glColor3f(l.c.r, l.c.g, l.c.b);
            glVertex2f(l.a.x, l.a.y);
            glVertex2f(l.b.x, l.b.y);
        }

        glEnd();
    }

    public void draw() {
        glPushMatrix();

        camera.draw();
        draw_lines();

        glPopMatrix();
    }
}
