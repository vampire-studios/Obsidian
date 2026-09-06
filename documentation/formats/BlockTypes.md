# Block Types

`block_type` picks which kind of block to build. It decides the block's shape, its state properties and
how it behaves — a `stairs` block is a real stairs block, with the vanilla corner logic and waterlogging
that comes with it.

```json
{ "block_type": "stairs" }
```

Values are case-insensitive; `"stairs"` and `"STAIRS"` are the same. The default is `block`.

An unrecognized value fails the whole file with `Failed to register block` in the log, naming the value
it did not know.

## Basic shapes

| Type                        | Behaves like                                                 | State properties                                   |
|-----------------------------|--------------------------------------------------------------|----------------------------------------------------|
| `block`                     | A plain full block.                                          | —                                                  |
| `directional`               | Placeable facing any of the six directions.                  | `facing`                                           |
| `horizontal_directional`    | Placeable facing the four horizontal directions.             | `facing`                                           |
| `eight_directional_block`   | Placeable at eight rotations, 45° apart.                     | `rotation` (0–7)                                   |
| `sixteen_directional_block` | Placeable at sixteen rotations, 22.5° apart, like a sign.    | `rotation` (0–15)                                  |
| `rotated_pillar`            | Log-style axis rotation.                                     | `axis`                                             |
| `slab`                      | Slab, with waterlogging.                                     | `type`, `waterlogged`                              |
| `stairs`                    | Stairs, with corners and waterlogging. Needs `parent_block`. | `facing`, `half`, `shape`, `waterlogged`           |
| `wall`                      | Wall, with waterlogging.                                     | `up`, `north`/`south`/`east`/`west`, `waterlogged` |
| `fence`                     | Fence.                                                       | `north`/`south`/`east`/`west`, `waterlogged`       |
| `fence_gate`                | Fence gate. Takes `wood_type`.                               | `open`, `powered`, `in_wall`                       |
| `pane`                      | Glass-pane connection behaviour.                             | connection states                                  |
| `carpet`                    | Thin, walkable layer.                                        | —                                                  |

## Doors, buttons and redstone

| Type                             | Notes                                                                                                                    |
|----------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| `door`                           | Takes `block_set_type` — that is what supplies the open/close sounds.                                                    |
| `trapdoor`                       | Takes `block_set_type`.                                                                                                  |
| `button`                         | Takes `block_set_type`. `information.wooden_button` (default `true`) sets the press duration to 30 ticks rather than 20. |
| `pressure_plate`                 | Takes `block_set_type`.                                                                                                  |
| `lever`                          | Flips between powered and unpowered, mountable on floor, wall or ceiling.                                                |

`block_set_type` is optional on all of these, and `wood_type` on `fence_gate`: a block that declares
neither gets oak's sounds and behaviour rather than failing to register.

## Nature

| Type                             | Notes                                                                                  |
|----------------------------------|----------------------------------------------------------------------------------------|
| `log`, `stem`, `wood`            | Axis-rotating natural pillars.                                                         |
| `leaves`                         | Leaves, with distance and persistence.                                                 |
| `plant`                          | Small plant. No collision, breaks instantly.                                           |
| `horizontal_facing_plant`        | Plant that faces a direction.                                                          |
| `double_plant`                   | Two blocks tall.                                                                       |
| `horizontal_facing_double_plant` | Two tall and facing.                                                                   |
| `hanging_double_leaves`          | Hangs downward, two blocks tall.                                                       |
| `sapling`                        | Grows into a configured tree.                                                          |
| `path`                           | Dirt-path shape.                                                                       |
| `crop`                           | Grows through age states on farmland. See [Crops](#crops).                             |
| `bush`                           | Berry bush: grows, is picked by hand, scratches. See [Bushes](#bushes).                |
| `climbable`                      | Ladder-shaped and wall-mounted. See [Climbable blocks](#climbable-blocks).             |
| `oxidizing_block`                | A copper-style chain. Uses `oxidizable_properties`, and registers one block per stage. |

The plant types honour `waterloggable` in `additional_information`.

## Furniture and stations

| Type                                  | Notes                                                                                                                                    |
|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `crafting_table`, `loom`, `barrel`    | Working vanilla stations.                                                                                                                |
| `furnace`, `blast_furnace`, `smoker`  | Working, with their block entities wired up.                                                                                             |
| `bed`, `campfire`, `beehive`, `cake`  | Working, each with its own properties section.                                                                                           |
| `painting_table`                      | Obsidian's own station, configured with `painting_table_information`.                                                                    |
| `torch`, `lantern`, `chain`, `ladder` | Light sources and climbables.                                                                                                            |
| `candle`                              | One to four per block, lightable and waterloggable. `luminance` in the settings is one candle's worth; vanilla scales it with the count. |
| `rod`                                 | Thin rod on any of the six faces, like an end rod. Shape comes from vanilla.                                                             |
| `dyeable`                             | Sixteen colour variants of one block.                                                                                                    |

## Crops

A `crop` grows on farmland and drops its seed when broken early. Its stages come from `growable`:

```json
{
  "block_type": "crop",
  "growable": { "max_age": 3, "seed": "examplepack:cheese_seeds" },
  "can_plant_on": ["minecraft:farmland", "minecraft:soul_soil"]
}
```

| Field                        | Default | Meaning                                                                       |
|------------------------------|---------|---------------------------------------------------------------------------------|
| `growable.max_age`           | `7`     | The last stage the crop grows to, 1–7. Four-stage crops use `3`.                |
| `growable.seed`              | own item| What is planted to get it.                                                      |
| `growable.harvest_on_interact` | `false` | Right-click a grown crop to take its drops and reset it, without replanting.  |
| `can_plant_on`               | farmland| Replaces the farmland requirement with your own list of supports.               |

Growth itself comes from the [growth settings](#growth-settings) below, which bushes share.

**Drops.** A crop registered with nothing declared drops its own block item at every stage, which is
almost never what you want. Use [`when`](./Blocks.md#dropping-by-state) to separate produce from seed:

```json
"drop_information": {
  "drops": [
    { "name": "examplepack:cheese", "when": { "age": 7 } },
    { "name": "examplepack:cheese_seeds", "when": { "age": { "max": 6 } } }
  ]
}
```

`harvest_on_interact` gives exactly those drops, so a right-click harvest and a break give the same
thing.

## Growth settings

Crops read these from `growable`, berry bushes from `bush_properties`. Left out, each block type keeps
the vanilla behaviour for its own shape.

| Field            | Default | Meaning                                                                              |
|------------------|---------|----------------------------------------------------------------------------------------|
| `min_light`      | `9`     | The light the block needs before it grows at all.                                       |
| `growth_chance`  | —       | One in this many random ticks advances a stage.                                         |
| `bonemeal_min`   | `2` / `1` | Fewest stages one bone meal advances. Bushes default to `1`, crops to `2`.            |
| `bonemeal_max`   | `5` / `1` | Most stages one bone meal advances.                                                   |
| `bonemeal_chance`| `1.0`   | The chance one bone meal does anything at all.                                           |
| `shapes_by_age`  | —       | The block's shape at each stage. See below.                                              |

Left out, `growth_chance` means each type keeps its own rate — for a crop that is vanilla's farmland
formula, where well-watered soil in a tended row grows faster than dry ground. **Declaring it replaces
that formula outright**, so a crop with `"growth_chance": 5` grows at the same speed on any soil.
Moving only `min_light` also leaves the formula behind, falling back to roughly its dry-ground rate.

`shapes_by_age` is one box set per stage, in the same 0–16 model space as
[voxel shapes](./VoxelShapes.md). A list shorter than the age range keeps its last entry for the rest:

```json
"shapes_by_age": [
  [[5, 0, 5, 11, 4, 11]],
  [[4, 0, 4, 12, 9, 12]],
  [[2, 0, 2, 14, 15, 14]]
]
```

The age property is always vanilla's `age` 0–7 — it is built before the block's own configuration is
reachable — so `max_age` caps how far the crop advances rather than shrinking the property. The
blockstate therefore has to name all eight ages, or the ones it leaves out have no model and render as
the missing-model cube. Generated blockstates draw the ages past `max_age` with the last stage's model;
a blockstate you ship yourself has to cover them too.

Crops force no collision, instant breaking, and random ticks, since without random ticks they cannot
grow.

## Bushes

A `bush` is vanilla's sweet berry bush shape: it grows through a few stages on its own, is **picked**
by right-clicking once it is ripe rather than broken, drops back a stage when it is picked, and slows
and scratches whatever pushes through it. Everything about it comes from `bush_properties`, and every
field there defaults to the sweet berry bush's own number — so `"bush_properties": {}` is already a
working bush.

```json
{
  "block_type": "bush",
  "bush_properties": {
    "berry": "examplepack:tutorial_berries",
    "max_age": 3,
    "ripe_age": 2
  },
  "can_plant_on": ["minecraft:grass_block", "minecraft:dirt"]
}
```

| Field             | Default                                  | Meaning                                                                |
|-------------------|------------------------------------------|--------------------------------------------------------------------------|
| `max_age`         | `3`                                      | The last growth stage, 1–7.                                              |
| `ripe_age`        | `2`                                      | The first stage that can be picked. Below it the bush is bare.           |
| `berry`           | the bush's own item                      | What picking gives.                                                      |
| `min_berries`     | `1`                                      | Fewest berries one picking gives.                                        |
| `max_berries`     | `2`                                      | Most berries one picking gives.                                          |
| `bonus_when_ripe` | `1`                                      | Extra berries when the bush is fully grown, not merely ripe.             |
| `picked_age`      | below `ripe_age`                         | The stage a picked bush drops back to.                                   |
| `damage`          | `1.0`                                    | Damage to something moving through a grown bush. `0` turns it off.       |
| `damage_type`     | `minecraft:sweet_berry_bush`             | The kind of damage that is. Any damage type, yours included.             |
| `immune_entities` | fox and bee                              | Entities the bush never hurts and never slows. `[]` makes it hurt all.   |
| `slowdown`        | `[0.8, 0.75, 0.8]`                       | How much it slows movement, per axis. A single number does all three; `1` is no slowing. |
| `pick_sound`      | `block.sweet_berry_bush.pick_berries`    | The sound picking makes.                                                 |

Growth and bone meal come from the [growth settings](#growth-settings) bushes share with crops, where a
bush defaults to a growth chance of 5 and a single stage per bone meal.

Picking drops the bush back to `picked_age`, so a bush is picked over and over rather than once. Foxes
and bees are left alone by default, and standing still in a bush is safe — it is moving through that
scratches.

A bush honours `waterloggable` in `additional_information` like the other plant types, and breaking one
drops whatever `drop_information` says — picking and breaking are separate.

Bushes force no collision, instant breaking and random ticks, since without random ticks they cannot
grow. A bush with `max_age` up to 3 uses vanilla's `age` 0–3; a taller one uses `age` 0–7. Ages past
`max_age` are drawn with the last stage's model, so the blockstate is complete either way.

Textures are named per stage — `stage0` through `stage3` — and a bush that names only `all` draws the
same texture at every stage:

```json
"rendering": {
  "block_model": {
    "textures": {
      "stage0": "examplepack:block/tutorial_bush_stage0",
      "stage1": "examplepack:block/tutorial_bush_stage1",
      "stage2": "examplepack:block/tutorial_bush_stage2",
      "stage3": "examplepack:block/tutorial_bush_stage3"
    }
  }
}
```

Picking is not the same as breaking: what a broken bush drops still comes from `drop_information` like
any other block.

## Climbable blocks

The `climbable` type gives a block the ladder's shape, wall placement and waterlogging. **Climbing
itself comes from the vanilla `minecraft:climbable` block tag**, which the block type cannot set on its
own — add the block to the tag from your pack's `data` directory:

```
ExamplePack/data/minecraft/tags/block/climbable.json
```

```json
{ "replace": false, "values": ["examplepack:rope"] }
```

A `climbable` block without that entry looks right and places right, but the player slides down it. See
[Pack Structure](../PackStructure.md#assets-and-data) for how the `data` directory is loaded.

## Rotation blocks

`eight_directional_block` and `sixteen_directional_block` carry a `rotation` number rather than a
facing, so they turn with the player instead of snapping to a block face.

**Shapes do not rotate with them.** A sixteenth of a turn is not a direction, and the shorthand shapes
only know how to rotate to one. Keep the shape symmetrical, or make it cover every angle.

### Models

One model is usually enough:

```json
{
  "block_type": "eight_directional_block",
  "rendering": { "block_model": "examplepack:block/toy_car" }
}
```

These blocks are **drawn by a renderer rather than baked into the chunk mesh**, because a baked model can
only be turned in quarter turns and these stand at 45° and 22.5°. The renderer turns the block's own
model, so any model works — including ones a rotated copy could never be generated from.

That has one cost worth knowing: a rendered block is redrawn each frame, where a baked one is built into
the chunk once. It is cheap per block, but a room filled with them is not free the way ordinary blocks
are.

A block placed by the player faces them, on the assumption the model is drawn facing north like every
other block type. A model drawn facing another way is corrected with `rendering.model_rotation_offset`,
which applies here as it does to the facing blocks.

The generated blockstate is deliberately **unturned** — one entry per rotation value, all pointing at the
model as drawn. The renderer applies the angle, and a rotation in the blockstate would be applied on top
of it.

A rotation may take a model of its own, turned like any other:

```json
{
  "rendering": {
    "block_model": "examplepack:block/statue",
    "rotation_models": { "4": "examplepack:block/statue_seated" }
  }
}
```

Packs that ship their own blockstate (`has_assets`) supply their own entries; keep them unturned for the
same reason.

## Variants

Walls, pressure plates and buttons can also be generated as [variants](./Blocks.md#variants) of a base
block, which is less work when you want the whole family from one texture.

## See also

* [Blocks](./Blocks.md) — the block format itself.
* [Block Settings](./BlockSettings.md) — hardness, sounds, light.
* [Voxel Shapes](./VoxelShapes.md) — custom geometry, for when no type fits.
