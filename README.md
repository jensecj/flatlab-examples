# Flatlab Demoes

contains demonstrations of how to use the [flatlab library](https://www.github.com/jensecj/flatlab-java).

The project uses `ant` for building.

To start, clone the repo and run:

```
ant resolve && ant run
```

Some scenes have movement / zooming, usually bound to WASD and Z/X (or
mouse wheel).

Demo | Description
--- | ---
CivWorldScene | A Civilization like 2D hexagonal world map generated using perlin noise
DragonFractalScene | Creates a dragon fractal by drawing points

![Screenshot of CivWorldScene](src/Scenes/CivWorldScene/scene.png?raw=true "Screenshot of CivWorldScene")
