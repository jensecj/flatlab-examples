# Flatlab Demoes

contains demonstrations of how to use the [flatlab library](https://www.github.com/jensecj/flatlab-java).

The project uses `ant` for building.

To start, clone the repo and run:

```
ant resolve && ant run
```

# Examples

![Screenshot of CivWorldScene](examples/CivWorldScene.png?raw=true "Screenshot of CivWorldScene")

Some scenes have movement / zooming, usually bound to WASD and Z/X (or
mouse wheel).

Demo | Description
--- | ---
[CivWorldScene](src/Scenes/CivWorldScene/CivWorldScene.java) | A Civilization like 2D hexagonal world map generated using perlin noise
[DragonFractalScene](src/Scenes/DragonFractalScene.java) | Creates a [dragon fractal](https://en.wikipedia.org/wiki/Dragon_curve) by drawing points
[LangtonsAntScene](src/Scenes/LangtonsAntScene.java) | A bunch of [Langton's ants](https://en.wikipedia.org/wiki/Langton%27s_ant)
[GameOfLifeScene](src/Scenes/GameOfLifeScene.java) | Demo of [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)
