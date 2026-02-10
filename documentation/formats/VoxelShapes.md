# Voxel Shape Definitions

Voxel shapes are used by blocks to define collision and outline shapes.

These definitions live in the `information.collision_shape` and `information.outline_shape` sections of a block definition.

## Basic structure

```json
{
  "collision_shape": {
    "collision_type": "CUSTOM",
    "advanced": false,
    "full_shape": [0, 0, 0, 16, 16, 16]
  },
  "outline_shape": {
    "collision_type": "CUSTOM",
    "advanced": true,
    "north_shape": [0, 0, 0, 16, 16, 16],
    "south_shape": [0, 0, 0, 16, 16, 16],
    "east_shape": [0, 0, 0, 16, 16, 16],
    "west_shape": [0, 0, 0, 16, 16, 16],
    "up_shape": [0, 0, 0, 16, 16, 16],
    "down_shape": [0, 0, 0, 16, 16, 16]
  }
}
```

## "collision_type"

Controls how Obsidian interprets the shape. Values come from `BlockInformation.BoundingBox.CollisionType`:

* `FULL_BLOCK`
* `BOTTOM_SLAB`
* `TOP_SLAB`
* `CUSTOM`
* `NONE`

## "advanced"

When `true`, per-direction shapes are used instead of a single `full_shape` definition.

## Shape arrays

Shape arrays are in block model coordinates (0-16). Each array is:

```
[minX, minY, minZ, maxX, maxY, maxZ]
```

`full_shape` is used when `advanced` is `false`. When `advanced` is `true`, the directional shape arrays are used instead.