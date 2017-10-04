import java.util.*;

class Vec3 {
    public float x;
    public float y;
    public float z;

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

public class PerlinNoise {

    public int _seed;

    public float _frequency;
    public float _gain;
    public float _lacunarity;
    public int _octaves;
    public float _persistance;
    public float _smoothingfactor;
    public int _smoothingiterations;

    private Vec3[] _grad;
    private int[] _perm;

    public PerlinNoise(int seed, float frequency, float gain, float lacunarity , int octaves, float persistance, float smoothingfactor, int smoothingiterations) {
        _seed = seed;
        _frequency = frequency;
        _gain = gain;
        _lacunarity = lacunarity;
        _octaves = octaves;
        _persistance = persistance;
        _smoothingfactor = smoothingfactor;
        _smoothingiterations = smoothingiterations;

        init();
    }

    public void init() {
        _grad = new Vec3[] {
            new Vec3(1, 1, 0),   new Vec3(-1, 1, 0),  new Vec3(1, -1, 0),
            new Vec3(-1, -1, 0), new Vec3(1, 0, 1),   new Vec3(-1, 0, 1),
            new Vec3(1, 0, -1),  new Vec3(-1, 0, -1), new Vec3(0, 1, 1),
            new Vec3(0, -1, 1),  new Vec3(0, 1, -1),  new Vec3(0, -1, -1)
        };

        int[] p = new int[256];
        for (int i = 0; i < p.length; i++) {
            p[i] = i;
        }

        Random rng = new Random(_seed);
        for (int i = 0; i < p.length; i++) {
            int j = rng.nextInt(256);

            int tmp = p[i];
            p[i] = p[j];
            p[j] = tmp;
        }

        _perm = new int[512];
        for(int i = 0; i < _perm.length; i++) {
            _perm[i] = p[i % 255];
        }
    }

    public float get(float x, float y) {
        x = x * _frequency;
        y = y * _frequency;

        float noise = 0;
        float accumulatedPersistance = 1;

        for (int i = 0; i < _octaves; i++) {
            noise += ((raw(x, y) + 1) / 2) * accumulatedPersistance;

            x /= _lacunarity;
            y /= _lacunarity;

            accumulatedPersistance *= _persistance;
        }

        noise = noise / _octaves * _gain;

        return noise;
    }

    public float raw(float x, float y) {
        int i = x > 0 ? (int)x : (int)x - 1;
        int j = y > 0 ? (int)y : (int)y - 1;

        x = x - i;
        y = y - j;

        i = i & 255;
        j = j & 255;

        int gll = _perm[i + _perm[j]] % 12;
        int glh = _perm[i + _perm[j + 1]] % 12;
        int ghl = _perm[i + 1 + _perm[j]] % 12;
        int ghh = _perm[i + 1 + _perm[j + 1]] % 12;

        float nll = _grad[gll].x * x + _grad[gll].y * y;
        float nlh = _grad[glh].x * x + _grad[glh].y * (y - 1);
        float nhl = _grad[ghl].x * (x - 1) + _grad[ghl].y * y;
        float nhh = _grad[ghh].x * (x - 1) + _grad[ghh].y * (y - 1);

        float u = x * x * x * (x * (x * 6 - 15) + 10);
        float v = y * y * y * (y * (y * 6 - 15) + 10);

        float nyl = nll + (nhl - nll) * u;
        float nyh = nlh + (nhh - nlh) * u;

        float nxy = nyl + (nyh - nyl) * v;

        return nxy;
    }
}
