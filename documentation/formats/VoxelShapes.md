# Voxel Shapes

A block's shape is three things at once: its collision box, its selection outline, and what it hides of
the blocks around it.

Boxes use the same coordinate space as block models — **0 to 16 per axis** — written as
`[x1, y1, z1, x2, y2, z2]`.

## Where it goes

Geometry lives in `information.shape` or `information.shapes` on a [block](./Blocks.md):

```json
{
  "information": {
    "shapes": [
      [0, 13, 0, 16, 16, 16],
      [2, 0, 2, 14, 13, 14]
    ]
  }
}
```

| Field | Meaning |
| --- | --- |
| `shape` | A single box. |
| `shapes` | Several boxes combined into one shape. Takes priority over `shape`. |

Copying the numbers out of your model's elements is usually right. Boxes that fully contain one another
can be merged — a stool's four legs and its central post are one box, not five.

## Facing

Shapes are authored **once, for the block facing north**, and rotated automatically to match the block's
horizontal facing. There is no `north_shape` / `south_shape` / `east_shape` / `west_shape`; a shape that
looks right on a north-facing bench is right on all four sides.

## Collision and outline

`collision_shape` and `outline_shape` pick which *kind* of shape to use, separately for each. The
geometry still comes from `shape` / `shapes`.

```json
{
  "information": {
    "collision_shape": { "collision_type": "NONE" },
    "shapes": [[0, 0, 7, 16, 12, 9]]
  }
}
```

| `collision_type` | Result |
| --- | --- |
| `FULL_BLOCK` | A full cube, ignoring the geometry. |
| `BOTTOM_SLAB` / `TOP_SLAB` | The lower or upper half. |
| `NONE` | Nothing — walk straight through. |
| `CUSTOM` | Use `shape` / `shapes`. |

Only `CUSTOM` reads the geometry, so the example above collides with nothing while still drawing a thin
outline. Omitting both objects behaves like `CUSTOM`.

### Vertical facings

Rotation cannot derive an up- or down-facing shape from a north-facing one, so `CUSTOM` accepts
`up_shape`, `down_shape`, `up_shapes` and `down_shapes` as overrides for those two directions only.

## Shapes and see-through blocks

The shape is also what the game uses to decide whether to hide the faces of neighbouring blocks. A block
with a custom model and **no** declared shape cannot be assumed solid, so it hides nothing; declaring a
shape makes that exact.

**If a custom-model block appears to make the terrain around it transparent, it is missing a shape.**

## Going outside the block

Coordinates may fall outside 0–16 for models that overhang their block, and they rotate correctly.
Minecraft only tests an entity against blocks near it, though, so collision more than roughly a block
away from the block itself is unreliable — a tall arch will not reliably stop a player at its top.
Genuinely multi-block structures want companion blocks, each with its own local shape, or a
[`fill_blocks`](../Events.md#positional-actions) event that lays down colliders on placement.

## See also

* [Blocks](./Blocks.md) — where shapes are declared.
* [Block Types](./BlockTypes.md) — types that bring their own shape.
