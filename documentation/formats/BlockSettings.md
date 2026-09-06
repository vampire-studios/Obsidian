# Block Settings

Block settings are the physical properties of a block: how hard it is, what it sounds like, how it
lights, how pistons treat it. They are the `block_properties` value on a [block](./Blocks.md), written
inline or shared as a named entry.

Named entries go in `block/property`, with the file name as the id:

```
obsidian_addons/ExamplePack/content/examplepack/block/property/stronk.json
```

```json
{
  "hardness": 3.0,
  "resistance": 3.0,
  "sound_group": "minecraft:stone",
  "map_color": "STONE",
  "luminance": 0
}
```

A block then references it, or spells the same object out inline:

```json
{
  "information": {
    "block_properties": "examplepack:stronk"
  }
}
```

## Fields

| Field                    | Default           | Meaning                                                                               |
|--------------------------|-------------------|---------------------------------------------------------------------------------------|
| `parent`                 | —                 | Id of another settings entry to inherit from.                                         |
| `hardness`               | `3.0`             | Time to break. `-1` makes the block unbreakable.                                      |
| `resistance`             | `3.0`             | Explosion resistance.                                                                 |
| `instant_break`          | `false`           | Breaks in one hit regardless of `hardness`.                                           |
| `sound_group`            | `minecraft:stone` | A vanilla sound group id, or the id of a custom one from `block/sound_group`.         |
| `map_color`              | `STONE`           | Colour on maps — a vanilla map colour name.                                           |
| `luminance`              | `0`               | Light emitted, 0–15.                                                                  |
| `powered_luminance`      | `-1`              | Light emitted while powered, for blocks with a `powered` state. `-1` means unused.    |
| `is_emissive`            | `false`           | Renders full-bright, like a texture that glows.                                       |
| `collidable`             | `true`            | Whether entities collide with it at all.                                              |
| `slipperiness`           | `0.6`             | Friction. Ice is `0.98`.                                                              |
| `velocity_modifier`      | `1.0`             | Multiplies movement speed on the block, like soul sand.                               |
| `jump_velocity_modifier` | `1.0`             | Multiplies jump height from the block, like honey.                                    |
| `randomTicks`            | `false`           | Receives random ticks. Required for the `on_random_tick` [event](./Blocks.md#events). |
| `dynamic_boundaries`     | `false`           | The shape changes at runtime, so it is not cached.                                    |
| `push_reaction`          | `NORMAL`          | Piston behaviour — see below.                                                         |
| `translucent`            | `true`            | Whether light and the faces behind it come through. Solid blocks should say `false`.  |
| `drop`                   | `minecraft:stone` | Legacy drop field; prefer a loot table.                                               |

### Mining, fire and physics

| Field                   | Default | Meaning                                                                        |
|-------------------------|---------|----------------------------------------------------------------------------------|
| `mineable`              | —       | The tool that mines it: `pickaxe`, `axe`, `shovel` or `hoe`.                      |
| `requires_tool`         | `false` | Only drops when broken with the right tool. Needs `mineable`; see below.          |
| `tool_tier`             | —       | Lowest tier that may mine it: `stone`, `iron` or `diamond`. Needs `requires_tool`.|
| `ignited_by_lava`       | `false` | Catches fire from lava next to it.                                               |
| `replaceable`           | `false` | Placing a block into it replaces it, the way grass and snow layers work.         |
| `liquid`                | `false` | Treated as a fluid for movement and rendering.                                   |
| `bounce_restitution`    | —       | How much of a bounce it gives. Slime is `0.8`.                                   |
| `fall_damage_reduction` | —       | How much fall damage it takes off. Hay bales take off `0.8`.                     |
| `no_terrain_particles`  | `false` | No breaking or footstep particles.                                               |
| `offset_type`           | `none`  | How the block is nudged in place: `none`, `xz` — as flowers are — or `xyz`.      |
| `instrument`            | —       | The sound a note block above it plays, e.g. `bass`, `bell`, `harp`.              |

#### Mining tools

The game never reads a tool off the block; it reads the block's tags. `mineable` puts the block in
`minecraft:mineable/<tool>`, which is what makes that tool break it quickly, and `tool_tier` puts it in
`minecraft:needs_<tier>_tool`, which is what makes anything weaker fail to drop it. Both tags are
generated for you and appended to vanilla's, so nothing you already have is replaced.

That makes `requires_tool` on its own a trap: with no `mineable` the block is in no tool tag, so **no**
tool is ever the correct one and the block drops nothing however it is broken. Obsidian warns about
this in the log rather than generating anything. A whole ore is all three together:

```json
{
  "hardness": 3.0,
  "resistance": 3.0,
  "mineable": "pickaxe",
  "requires_tool": true,
  "tool_tier": "iron"
}
```

Wood and gold are the bottom of the tier ladder and have no tag of their own — leave `tool_tier` out
for a block any tool tier can mine.

### Solidity

These four are left to the block's shape unless you say otherwise, which is usually what you want. Set
one only when the shape gives the wrong answer.

| Field                | Meaning                                                    |
|----------------------|--------------------------------------------------------------|
| `force_solid`        | Forces the game's idea of whether the block is solid.        |
| `redstone_conductor` | Whether redstone runs through it.                            |
| `suffocating`        | Whether standing inside it suffocates.                       |
| `view_blocking`      | Whether it blacks out the view of a player inside it.        |
| `spawnable`          | `false` stops mobs spawning on it.                           |

## Inheriting

`parent` names settings to start from — either an entry in `block/property` or, inline, another set
written out. Fields the child writes win; every field it leaves out keeps the parent's value:

```json
{
  "parent": "examplepack:stronk",
  "luminance": 15,
  "requires_tool": true
}
```

Chains are followed to a depth of 8, which is also what stops a set of settings that inherits from
itself.

### `push_reaction`

| Value       | Effect                        |
|-------------|-------------------------------|
| `NORMAL`    | Pushed and pulled normally.   |
| `BLOCK`     | Immovable.                    |
| `DESTROY`   | Breaks and drops when pushed. |
| `IGNORE`    | Ignored by pistons.           |
| `PUSH_ONLY` | Pushed, but not pulled.       |

An unrecognised value throws while the block is being registered, so the block does not load.

## Custom sound groups

`sound_group` accepts any namespace other than `minecraft`, resolved against the pack's
`block/sound_group` directory:

```json
{
  "break_sound": "examplepack:block.cheese.break",
  "step_sound": "examplepack:block.cheese.step",
  "place_sound": "examplepack:block.cheese.place",
  "hit_sound": "examplepack:block.cheese.hit",
  "fall_sound": "examplepack:block.cheese.fall"
}
```

All five sounds must resolve; a custom group naming a sound that does not exist fails the blocks using
it.

## Older spellings

Settings have been renamed more than once. The old names are still read — translated to the current
ones before anything else happens — and each one that turns up is named once in the log, so a pack
written against an older Obsidian keeps working while you bring it up to date. A file that uses both
spellings keeps the current one.

| Older name                                          | Read as                              |
|-----------------------------------------------------|--------------------------------------|
| `blast_resistance`, `explosion_resistance`          | `resistance`                         |
| `destroy_time`, `break_time`                        | `hardness`                           |
| `friction`                                          | `slipperiness`                       |
| `light_level`, `light_emission`                     | `luminance`                          |
| `map_colour`                                        | `map_color`                          |
| `piston_behavior`, `piston_behaviour`               | `push_reaction`                      |
| `random_ticks`, `ticks_randomly`                    | `randomTicks`                        |
| `requires_correct_tool`, `requires_correct_tool_for_drops` | `requires_tool`               |
| `blocks_movement`                                   | `collidable`                         |
| `blocks_light`                                      | `force_solid`                        |
| `burnable`, `flammable`                             | `ignited_by_lava`                    |
| `sound`                                             | `sound_group`                        |
| `solid`                                             | `translucent`, which means the opposite |

`liquid` and `replaceable` kept their names and are [real settings](#mining-fire-and-physics) again.

### Removed: block materials

Minecraft no longer has a `Material` class, so the old `blocks/materials` files are gone, along with
the `material` field that named one. A `material` is still read for the two things it stood for that
survive — the block's **sound group** and its **map colour** — for the vanilla materials `stone`,
`metal`, `wood`, `nether_wood`, `bamboo`, `wool`, `glass`, `ice`, `dirt`, `soil`, `grass`, `sand`,
`gravel`, `snow`, `plant`, `leaves`, `clay`, `water` and `lava`. Anything the file sets itself wins.
Everything else the material used to decide — face culling, movement blocking — now comes from the
block's [type](./BlockTypes.md) and [shape](./VoxelShapes.md).

`is_light_block` is gone: give the block `luminance` instead. `fireproof` was never a block setting;
it belongs in [item settings](./ItemSettings.md).

## See also

* [Blocks](./Blocks.md) — where settings are referenced from.
* [Item Settings](./ItemSettings.md) — the item-side equivalent.
