import java.util.*;

import flatlab.*;
import flatlab.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    private int screen_width = 1000;
    private int screen_height = 800;

    private float _cam_x = 0;
    private float _cam_y = 0;
    private float _cam_speed = 0.5f;
    private float _cam_x_wanted = 0;
    private float _cam_y_wanted = 0;

    private float _cam_zoom = 0.1f;
    private float _cam_zoom_wanted = 0.05f;
    private float _cam_zoom_increment = 0.15f; // 5%
    private float _cam_zoom_speed = 0.05f;

    public float zoom() { return _cam_zoom; }
    public float x() { return _cam_x; }
    public float y() { return _cam_y; }

    public Camera(int screen_width, int screen_height) {
        this.screen_width = screen_width;
        this.screen_height = screen_height;
    }

    public void set_position(int x, int y) {
        _cam_x_wanted = x;
        _cam_y_wanted = y;
    }

    public void set_zoom(float f) {
        _cam_zoom_wanted = f;
    }

    public void on_mouse_scroll(float x, float y) {
        if(y > 0)
            _cam_zoom_wanted *= 1 + _cam_zoom_increment;
        else
            if (_cam_zoom_wanted > 0f)
                _cam_zoom_wanted *= 1 - _cam_zoom_increment;
    }

    public void on_key(int key, int action, int mods) {
        if (key == 'A') _cam_x_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'D') _cam_x_wanted += 10 * (1 / _cam_zoom);
        if (key == 'W') _cam_y_wanted -= 10 * (1 / _cam_zoom);
        if (key == 'S') _cam_y_wanted += 10 * (1 / _cam_zoom);
    }

    public void update(float dt) {
        if (_cam_zoom != _cam_zoom_wanted)
            _cam_zoom = Flatmath.lerp(_cam_zoom, _cam_zoom_wanted, _cam_zoom_speed);

        if (_cam_x_wanted != _cam_x)
            _cam_x = Flatmath.lerp(_cam_x, _cam_x_wanted, _cam_speed);

        if (_cam_y_wanted != _cam_y)
            _cam_y = Flatmath.lerp(_cam_y, _cam_y_wanted, _cam_speed);
    }

    public void draw() {
        // transform to the camera
        glTranslatef(screen_width / 2, screen_height / 2, 0);
        glScalef(_cam_zoom, _cam_zoom, 1);
        glTranslatef(-(screen_width / 2), -(screen_height / 2), 0);

        glTranslatef(-_cam_x, -_cam_y, 0);
    }
}
