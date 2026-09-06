# Blocks

Blocks placed in the world, and the block item that places them.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/block/cheese_block.json
```

**The file name is the id** — that file registers `examplepack:cheese_block`. Subdirectories of `block/`
are not scanned.

## Example

```json
{
  "block_type": "block",
  "information": {
    "name": "Cheese Block",
    "block_properties": "examplepack:soft",
    "item_properties": { "item_group": "examplepack:cheese" },
    "shapes": [[0, 0, 0, 16, 14, 16]]
  },
  "additional_information": {
    "stairs": true,
    "slab": true
  },
  "events": {
    "on_interact": [{ "action": "play_sound_at", "sound": "minecraft:block.slime_block.step" }]
  }
}
```

## Fields

| Field                                                                        | Required | Meaning                                                                              |
|------------------------------------------------------------------------------|----------|--------------------------------------------------------------------------------------|
| `information`                                                                | yes      | Name, settings, geometry, state. See [below](#information).                          |
| `block_type`                                                                 | no       | Which kind of block to build. Default `"block"`. See [Block Types](./BlockTypes.md). |
| `template`                                                                   | no       | Inherit from an entry in `block/template`. See [Templates](#templates).              |
| `additional_information`                                                     | no       | Extra variants and behaviour flags. See [Variants](#variants).                       |
| `components`                                                                 | no       | Data components for the block item.                                                  |
| `events`                                                                     | no       | Behaviour. See [Events](../Events.md).                                               |
| `menu_config`                                                                | no       | Crate menu and loot pool, opened by `open_crate`. See [Menu Config](./CustomMenu.md).|
| `rendering`                                                                  | no       | Display information. Also accepted as `display`.                                     |
| `drop_information`                                                           | no       | What the block drops, and whether its loot table is generated. See ["drop_information"](#drop_information). |
| `food_information`                                                           | no       | Makes the block edible.                                                              |
| `growable`, `can_plant_on`, `particle_type`                                  | no       | Plant growth, valid supports, interaction particles.                                 |
| `bush_properties`                                                            | no       | Berry bush growth, picking and damage. See [Bushes](./BlockTypes.md#bushes).          |
| `behaviour`                                                                  | no       | Placement restriction, redstone output, seats, storage and locks. See [Placement restriction](#placement-restriction), [Redstone output](#redstone-output), [Seats](#seats), [Containers](#containers) and [Locks](#locks). |
| `campfire_properties`, `oxidizable_properties`, `painting_table_information` | no       | Settings for those block types.                                                      |
| `is_multi_block`, `multi_block_information`                                  | no       | Structures built from one placement.                                                 |

## "information"

| Field                                | Meaning                                                                                   |
|--------------------------------------|-------------------------------------------------------------------------------------------|
| `name`                               | Display text. See [Names](./Names.md).                                                    |
| `block_properties`                   | [Block settings](./BlockSettings.md) — inline, or the id of an entry in `block/property`. |
| `item_properties`                    | [Item settings](./ItemSettings.md) for the block item.                                    |
| `parent_block`                       | Copy base properties from an existing block.                                              |
| `shape` / `shapes`                   | Collision and outline geometry. See [Voxel Shapes](./VoxelShapes.md).                     |
| `collision_shape` / `outline_shape`  | Which *kind* of shape to use.                                                             |
| `placement_shapes`                   | Per-placement geometry overrides.                                                         |
| `block_set_type`                     | Required by `door`, `trapdoor` and `button` — controls sounds and open/close behaviour.   |
| `wood_type`                          | Required by `fence_gate` and the wooden variants.                                         |
| `vanilla_properties`, `defaultValue` | Extra block state properties. See [Block state](#block-state).                            |
| `powerable`, `toggleable`            | Adds a `powered` state the block reacts to. See [Powered blocks](#powered-blocks).         |
| `has_item`                           | Default `true`. `false` registers the block with no block item.                           |
| `cake_slices`, `wooden_button`       | Type-specific tuning.                                                                     |

## Shapes

A block's collision box, selection outline, and what it hides of its neighbours, in model coordinates
(0–16):

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

Shapes are authored once for the north facing and rotated automatically.

**A block with a custom model and no shape hides nothing** — if one appears to make the terrain around
it transparent, that is the missing shape. [Voxel Shapes](./VoxelShapes.md) covers collision types,
vertical facings and overhanging geometry.

## Variants

`additional_information` generates companion blocks from the same settings. Each is registered as
`<block id>_<variant>`, with its own blockstate, models, item definition, name and loot table:

```json
{
  "additional_information": {
    "variants": {
      "stairs": true,
      "slab": true,
      "wall": true
    }
  }
}
```

| Variant          | Registers         | Creative tab    |
|------------------|-------------------|-----------------|
| `slab`           | `_slab`           | Building blocks |
| `stairs`         | `_stairs`         | Building blocks |
| `wall`           | `_wall`           | Building blocks |
| `fence`          | `_fence`          | Building blocks |
| `fence_gate`     | `_fence_gate`     | Redstone        |
| `button`         | `_button`         | Redstone        |
| `pressure_plate` | `_pressure_plate` | Redstone        |
| `door`           | `_door`           | Redstone        |
| `trapdoor`       | `_trapdoor`       | Redstone        |

Spelling is forgiving — `walls`, `fenceGate` and `pressurePlate` all land on the same variants.

### Customizing a variant

`true` takes every default. An object instead says how that one variant differs:

```json
{
  "additional_information": {
    "variants": {
      "stairs": true,
      "wall": {
        "textures": { "all": "tutorial:block/tutorial_brick_top" }
      },
      "trapdoor": {
        "id": "tutorial_brick_hatch",
        "name": "Tutorial Brick Hatch",
        "item_group": "minecraft:functional_blocks",
        "loot_table": "tutorial:blocks/tutorial_brick_hatch"
      }
    }
  }
}
```

| Field                 | Default                             | Meaning                                                        |
|-----------------------|-------------------------------------|------------------------------------------------------------------|
| `enabled`             | `true`                              | `false` drops a variant an earlier flag turned on.               |
| `id`                  | base id + the variant's suffix      | The variant's whole id path, in the block's namespace.           |
| `suffix`              | the variant's own (`_stairs`, …)    | What is appended to the base id instead.                         |
| `name`                | the block's name + the variant word | Display name, in any of the forms [names](./Names.md) take.      |
| `item_group`          | the tab in the table above          | Creative tab.                                                    |
| `block_model`         | the base block's                    | Model to build this variant's from — same forms as `block_model`.|
| `block_properties`    | the block's own                     | [Block settings](./BlockSettings.md) for this variant alone, written out or named. |
| `textures`            | the base block's                    | Shorthand for a `block_model` that is only textures.             |
| `loot_table`          | —                                   | Use an existing table. See [Referencing a loot table](#referencing-a-loot-table). |
| `generate_loot_table` | `true`                              | `false` for a variant that should drop nothing.                  |

The older spelling — a flag per variant, at the top of `additional_information` — still works and means
the same as `true`:

```json
{
  "additional_information": {
    "stairs": true,
    "slab": true,
    "walls": true
  }
}
```

A variant named in `variants` wins over its flag, so `"trapdoor": true` up top and
`"trapdoor": { "enabled": false }` in `variants` leaves no trapdoor.

Two more things to know:

* **`extraBlocksName` renames the family.** A block called `cheese` with `"extraBlocksName": "cheese_brick"`
  produces `cheese_brick_stairs`, not `cheese_stairs`. A variant's own `id` overrides even that.
* **`overworldLike` (default), `netherLike` and `bambooLike`** pick the wood type behind the generated
  variants, which is what decides their sounds and their door/button behaviour.

`waterloggable`, `dyable` and `sittable` in the same section add behaviour to the block types that
support them — `dyable` and `sittable` on `block` and `horizontal_directional`, `waterloggable` on the
plant types.

`path`, `lantern`, `barrel`, `leaves`, `chains` and `cake_like` are a **dead fallback**. They only run
when the block has no type at all, but `block_type` defaults to `"block"`, so in practice they never
fire. Use the equivalent [block type](./BlockTypes.md) instead.

## Placement variants

A block can carry a `placement` state — `floor`, `wall` or `ceiling` — and take a different shape and
model for each, the way a torch or a lantern does. One definition covers all three; there is no need for
separate blocks.

```json
{
  "information": {
    "placement_shapes": {
      "floor":   { "shapes": [[6, 0, 6, 10, 10, 10]] },
      "wall":    { "shapes": [[6, 3, 11, 10, 13, 16]] },
      "ceiling": { "shapes": [[6, 6, 6, 10, 16, 10]] }
    }
  },
  "rendering": {
    "placement_models": {
      "floor": "examplepack:block/cheese_lamp",
      "wall": "examplepack:block/cheese_lamp_wall",
      "ceiling": "examplepack:block/cheese_lamp_hanging"
    }
  }
}
```

**Declaring geometry or a model is what creates the variant.** The property's values are the union of the
two, so the shape lookup and the generated blockstate always agree on which variants exist. A block that
declares only `floor` and `wall` has a two-value property and no ceiling form.

On placement, the clicked face decides: the top face gives `floor`, the bottom `ceiling`, the sides
`wall`. A face whose variant the block does not declare falls back to the nearest one it does, so a
floor-only block still places from any face.

Only `block`, `wood`, `directional` and `horizontal_directional` read the property — along with
powerable, toggleable and sittable blocks built on them. Types with placement logic of their own (stairs,
doors, slabs) and dyeable plain blocks do not.

## Powered blocks

`information.powerable` makes a block follow the redstone signal around it; `information.toggleable`
makes it flip when a player uses it. Either one puts a `powered` state on the block, and both can be set
at once.

```json
{
  "block_type": "rotated_pillar",
  "information": {
    "powerable": true,
    "block_properties": { "luminance": 0, "powered_luminance": 15 }
  },
  "rendering": {
    "block_model": { "parent": "block/cube_column", "textures": { "end": "block/redstone_lamp", "side": "block/redstone_lamp" } },
    "powered_model": { "parent": "block/cube_column", "textures": { "end": "block/redstone_lamp_on", "side": "block/redstone_lamp_on" } }
  }
}
```

`powered_luminance` on the [block settings](./BlockSettings.md) is the light it gives while powered —
which, with `luminance` at `0`, is a lamp. It applies to any block carrying the state.

`rendering.powered_model` is what the block draws while powered, in the same forms as `block_model`: a
model id, or a parent and textures to build one from. The generated blockstate covers the block's own
states *and* `powered`, so a powered pillar keeps its axis and a powered directional block keeps its
facing. Without a `powered_model` the block simply draws the same either way, and only the light changes.

Powered works on the whole-block types: `block`, `wood`, `horizontal_directional`, `directional` and
`rotated_pillar`. The building shapes — stairs, slabs, walls, fences — deliberately do not take it.

## Block state

`vanilla_properties` adds vanilla state properties to a custom block:

```json
{
  "information": {
    "vanilla_properties": ["facing", "lit"],
    "defaultValue": { "lit": "false" }
  }
}
```

Any property on vanilla's `BlockStateProperties` can be named. Use the plain name (`facing`,
`waterlogged`, `powered`) when it is unambiguous; where variants share a name — `age` exists with several
maximums — use the field name instead (`age_3`, `age_15`).

Obsidian fills in `facing`, `horizontal_facing` and `axis` on placement. **Every other property is state
only, with no behaviour attached**: adding `waterlogged` will not make the block hold water, and adding
`powered` will not make it answer redstone. Drive those from [events](../Events.md) with
`set_block_property`.

Naming a property the block already has is harmless — a `powerable` block already carries `powered`, and
listing it again is ignored rather than registered twice.

## Placement restriction

`behaviour.placement` says which surfaces a block may be placed against. Declaring the section at all
opts the block in; without one, a block goes anywhere, as before.

```json
{
  "behaviour": {
    "placement": { "floor": false, "wall": false, "ceiling": true }
  }
}
```

Each surface defaults to `false`, so the example above is a ceiling-only block: clicking the underside of
a block hangs it there, and clicking a floor or a wall does nothing and leaves the item in hand. A section
with no surface enabled would make the block unplaceable, so it is warned about in the log and ignored.

Which face is which follows the click: the **top** of a block puts yours on the `floor`, the **underside**
hangs it from the `ceiling`, and the sides mount it on a `wall`.

A restricted block also has to keep the surface it was placed against — break the block above a
ceiling-only block and it drops. That check needs to know which surface the block is on, which Obsidian
knows in two cases: the block declares the [`placement` variants](#placement-variants) property and
records it, or only one surface is allowed in the first place. A block that allows several surfaces
without recording which it used is left standing instead of broken on a guess.

Restriction and variants are independent, and compose:

| Declared | Result |
| --- | --- |
| `behaviour.placement` only | One look, restricted to the allowed surfaces. |
| `placement_shapes` / `placement_models` only | A shape or model per surface, placeable anywhere. |
| Both | A surface has to be allowed to be reachable, and gets its own shape or model. |

Only the plain, horizontal-facing and directional block types read this — the same set that supports the
`placement` property. Block types with placement logic of their own, such as stairs, doors and torches,
keep theirs.

## Redstone output

Two behaviours make a block *emit* redstone. They are the counterpart to `information.powerable` and
`information.toggleable`, which make a block *listen* for it; a block may do both.

### `power_source`

A block that emits a constant signal, like a block of redstone.

```json
{
  "behaviour": {
    "power_source": { "value": 15 }
  }
}
```

| Field   | Default | Meaning                                       |
|---------|---------|-----------------------------------------------|
| `value` | `15`    | Signal strength, clamped to 0–15, on all sides. |

It powers what touches it but does not power *through* a block, the same as a block of redstone.

### `repeater`

A block that takes a signal in the back and passes it out the front after a delay.

```json
{
  "block_type": "horizontal_directional",
  "behaviour": {
    "repeater": { "delay": 2, "loss": 0 }
  }
}
```

| Field   | Default | Meaning                                                              |
|---------|---------|-----------------------------------------------------------------------|
| `delay` | `0`     | Ticks before the output catches up. Values below 1 are treated as 1. |
| `loss`  | `0`     | Subtracted from the incoming strength.                               |

`loss: 0` passes the signal on **at full strength**, which is the thing vanilla redstone cannot do — a
vanilla repeater always restores to 15, and wire always decays. A `loss` of 1 makes an attenuator that
fades a signal one step per block.

The block needs a facing to have a front, so give it `horizontal_directional` or `directional`. One
without a facing logs a warning and does nothing. Facing works the way a vanilla repeater's does: place it
looking in the direction you want the signal to travel. A repeater powers through the block it points at,
again like the vanilla one.

The output strength lives in the block's `power` state property, which Obsidian adds for you — listing
`power` in [`vanilla_properties`](#block-state) as well is harmless, and lets a model vary with it.

## Seats

`behaviour.seat` is a list, so one furniture block can carry several independent passengers. Offsets are
written in the local frame of a north-facing block and rotate with `facing` or `horizontal_facing`:

```json
{
  "behaviour": {
    "seat": [
      {
        "offset": { "x": 0.0, "y": 0.45, "z": 0.0 },
        "direction": 0,
        "pose": "sitting",
        "lock_rotation": false,
        "dismount_offset": { "x": 0.0, "y": 0.0, "z": 1.0 },
        "check_space": true,
        "skip_night": false,
        "reset_phantoms": false
      }
    ]
  }
}
```

| Seat field         | Default       | Meaning                                                                                     |
|--------------------|---------------|---------------------------------------------------------------------------------------------|
| `offset`           | `(0, 0.4, 0)` | Exact passenger attachment point relative to the block centre and bottom.                  |
| `direction`        | `0`           | Clockwise yaw added after the seat rotates with its block.                                  |
| `pose`             | `sitting`     | `sitting`, or `lying` for beds, loungers and similar furniture.                             |
| `lock_rotation`    | `false`       | Keeps the passenger facing `direction`; automatically enabled by `lying`.                   |
| `dismount_offset`  | automatic     | Preferred local point to try before adjacent collision-tested dismount positions.           |
| `check_space`      | `true`        | Rejects a seat whose passenger volume is obstructed. Tagged bypass blocks are ignored.      |
| `skip_night`       | `false`       | For a lying seat, counts the player toward vanilla's sleeping percentage.                   |
| `reset_phantoms`   | `false`       | For a lying seat, resets the player's time-since-rest stat when they lie down.               |

The legacy `additional_information.sittable` flag is equivalent to one seat with all defaults. Sneaking
bypasses seating so a block can be placed against the furniture. Lying seats use vanilla's sleeping model;
night skipping and phantom resets are opt-in, and seats never set a spawn point.

The furniture's own blocks never count against `check_space`. For anything else that should not block a
seat — a table top over a stool, a canopy over a bed — add it to the `obsidian:above_bypasses_seat_check`
block tag from the pack's `data` directory. The tag ships empty.

## Containers

`behaviour.container` gives a block storage of its own — a chest, a crate, a cabinet, a mailbox.

```json
{
  "block_type": "horizontal_directional",
  "information": { "name": "Cheese Crate" },
  "behaviour": {
    "container": {
      "name": "Cheese Crate",
      "size": 27
    }
  }
}
```

| Field             | Default      | Meaning                                                                                        |
|-------------------|--------------|------------------------------------------------------------------------------------------------|
| `name`            | block's name | Title shown at the top of the screen. Accepts any [name](./Names.md) form.                       |
| `size`            | `27`         | Slot count. Either `5`, for a hopper-shaped screen, or a multiple of 9 from 9 to 81.             |
| `purge`           | `false`      | Throws the contents away once the last viewer closes the screen.                                 |
| `drop_contents`   | `true`       | Scatters the contents on the floor when the block is broken.                                     |
| `open_animation`  | —            | Reserved. Parsed, but nothing plays it yet.                                                      |
| `close_animation` | —            | Reserved. Parsed, but nothing plays it yet.                                                      |

A size that is neither `5` nor a full row of nine is rounded **up** to the next row, with a warning in the
log, rather than costing the pack its block. Nine rows is the largest screen Obsidian registers; anything
above `81` is clamped to it.

Declaring a container changes which block class is registered, so it is only available on `block`, `wood`
and `horizontal_directional` blocks — the two shapes storage furniture actually needs. Other block types
ignore the section. Every other Obsidian block stays free of a block entity, which is why the container
is opt-in per block rather than a flag every block carries.

Containers and [seats](#seats) coexist on one block: the seat is offered first, and a sneaking player
falls through to the container, so a chest-bench keeps both interactions.

`purge` is for loot crates and one-shot dispensers. Do not put it on anything a player stores things in —
it empties on close, not on break.

`drop_contents: false` keeps the contents out of the world when the block breaks. Obsidian does **not**
copy them into the block item the way a shulker box does; without a pack-supplied component to carry
them, they are simply gone.

## Locks

`behaviour.lock` puts a block behind a key item. Until it is opened the block does nothing at all —
no events, no seat, no container.

```json
{
  "information": { "name": "Vault Door" },
  "behaviour": {
    "lock": {
      "key": "examplepack:brass_key",
      "consume_key": false,
      "discard": true,
      "command": "playsound minecraft:block.vault.open block @a"
    }
  }
}
```

| Field              | Default | Meaning                                                                                    |
|--------------------|---------|--------------------------------------------------------------------------------------------|
| `key`              | —       | The item that opens it, held in the main hand.                                              |
| `consume_key`      | `false` | Takes one key from the stack on each successful unlock. Creative players never lose theirs.  |
| `discard`          | `false` | Opens the lock for good the first time. Otherwise the key is needed every time.             |
| `command`          | —       | Command run when the lock opens. See [The command](#the-command).                           |
| `unlock_animation` | —       | Reserved. Parsed, but nothing plays it yet.                                                 |

Clicking without the key gives vanilla's locked-container feedback: the rattle, and the block's name in
the action bar. Clicking **with** it opens the lock and the same click carries on to whatever the block
normally does — a locked chest opens, a locked chair seats you.

A lock with no `key` can never be opened by anyone. That is a valid way to make a block purely
decorative, but it is easy to write by accident.

### discard

`discard: true` writes an `unlocked` block state property, so the block stays open across saves and for
every player afterwards — the one-time vault door. It is the only case that costs the block a state
property; a lock without it stores nothing.

Because the property only exists when `discard` is set, adding it later does not retroactively unlock
blocks already placed, and removing it re-locks them.

### The command

The command runs as the server, at the block's centre, with the unlocking player as the executing
entity, at gamemaster permission and with its output suppressed. `@s` is the player who opened the
lock, and `~ ~ ~` is the block.

```json
{ "command": "setblock ~ ~1 ~ minecraft:air" }
```

It runs on **every** successful unlock, so on a lock without `discard` it fires each time the key is
used, not once.

The API comment describing an NBT override at `Lock.Command` does not apply: a locked block has no block
entity unless it is also a [container](#containers), and the command is read from the definition.

## Templates

`block/template` holds entries with the same shape as a block. Referencing one fills in what the block
leaves out:

```json
{
  "template": "examplepack:stone_like",
  "information": { "name": "Cheese Stone" }
}
```

How each field merges:

| Field | Merge |
| --- | --- |
| `behaviour`, `components`, `rendering`, `drop_information`, `functions`, and the other object sections | Taken from the template only if the block omits them **entirely**. |
| `events` | Merged per key; the block's entry wins. |
| `lore`, `can_plant_on` | Taken from the template when the block's is missing or empty. |
| `information` | Merged field by field. |
| `information.name` | **Never inherited.** Every block keeps its own identity. |

Fields with a non-null default are the one rough edge. JSON cannot distinguish a field left out from one
written with its default value, so `block_type`, `has_item`, `wooden_button`, `powerable`, `toggleable`
and `cake_slices` are inherited **only while the block still holds the format default**. In practice this
means a block cannot re-assert a default that its template overrides — a block under a `"block_type":
"stairs"` template cannot ask to be a plain `block`. Split the template instead.

Templates are never registered as blocks themselves, so a template needs no name and produces no item.

## Events

Blocks use the same [event format](../Events.md) as items, with their own event names and the extra
positional actions:

```json
{
  "events": {
    "on_interact": [
      { "action": "play_sound_at", "sound": "minecraft:block.note_block.bell" },
      { "action": "set_block_property", "property": "lit", "value": "true" }
    ]
  }
}
```

## "convertible"

Declares that an item converts this block into another one on right-click.

```json
{
  "additional_information": {
    "convertible": {
      "transformed_block": "examplepack:polished_block",
      "conversion_item": { "tag": "minecraft:pickaxes" },
      "sound": "minecraft:item.axe.scrape",
      "reversible": true,
      "reversal_item": { "item": "minecraft:stick" }
    }
  }
}
```

Declaring `convertible` is the whole opt-in; the old `is_convertible` flag is no longer read.

| Field                          | Meaning                                                          |
|--------------------------------|------------------------------------------------------------------|
| `parent_block`                 | Block converted *from*. Defaults to the block declaring this.    |
| `transformed_block`            | Block converted *to*.                                            |
| `conversion_item`              | An [ingredient](./Ingredient.md) — what performs the conversion. |
| `sound`                        | Sound played on conversion. Optional.                            |
| `reversible` + `reversal_item` | Registers the inverse conversion on the reversal item.           |

State properties carry over, so a convertible stair keeps its facing.

Conversion compiles into vanilla's `minecraft:block_transformer` component on the conversion item,
**merged** with any transformer that item already has — pointing `conversion_item` at a vanilla axe adds
your conversion without breaking stripping.

`convertible` also takes every [shared conversion option](./ChiselMappings.md#shared-conversion-options)
— `particle`, `disallowed_faces`, `loot`, `drop_strategy`, `update_from_neighbors`, `transform_type`,
`consume_on_use` and `item_damage_per_use`.

`dropped_item` is parsed but ignored, with a warning; use `loot` with a loot table.

## "drop_information"

Obsidian writes a loot table for every block it registers, and for each companion block from
`additional_information` — without one a block drops nothing when broken. The default is vanilla's:
the block itself, once, destroyed by explosions; a slab drops two when the broken block was a double
slab.

```json
{
  "drop_information": {
    "generate_loot_table": false
  }
}
```

| Field                    | Default | Meaning                                                                                    |
|--------------------------|---------|--------------------------------------------------------------------------------------------|
| `generate_loot_table`    | `true`  | Set `false` for a block that should drop nothing, or whose loot table your data pack ships. |
| `loot_table`             | —       | Use an existing loot table by id. See [Referencing a loot table](#referencing-a-loot-table).|
| `companion_loot_tables`  | —       | The same, per companion block, keyed by which one.                                          |
| `drops`                  | —       | Drop these items instead of the block. See [Drops](#drops).                                 |
| `silk_touch_drops_block` | `false` | Mining with Silk Touch gives the block itself; anything else rolls `drops`. The ore idiom.  |
| `survives_explosion`     | `true`  | Whether the drop survives being blown up.                                                   |
| `xp_drop_amount`         | `1`     | Experience dropped on break.                                                                |

The table is written at whatever loot table the registered block asks for, so a block registered with
no loot table at all is left alone. A block with `"has_item": false` has nothing to drop and is skipped
unless it declares `drops`.

### Referencing a loot table

`loot_table` points the block at a table that already exists — vanilla's, another mod's, or one your
own data pack ships — instead of describing its drops here:

```json
{
  "drop_information": {
    "loot_table": "minecraft:blocks/diamond_ore"
  }
}
```

The block is pointed straight at that table, so nothing is generated for it and `drops` is ignored.
Nothing is written at that id either, so referencing a vanilla table borrows it rather than replacing
it for everyone.

The id is not checked when the block is registered — loot tables are not loaded that early — so an id
that turns out not to exist leaves the block dropping nothing.

`loot_table` covers the block itself. A companion block's own table is best declared next to the rest
of that variant, as [`variants.<variant>.loot_table`](#customizing-a-variant); `companion_loot_tables`
here says the same thing from the drops side, and any companion named in neither keeps its own
generated table:

```json
{
  "additional_information": {
    "stairs": true,
    "slab": true,
    "walls": true
  },
  "drop_information": {
    "companion_loot_tables": {
      "stairs": "tutorial:blocks/tutorial_brick_stairs",
      "slab": "tutorial:blocks/tutorial_brick_slab"
    }
  }
}
```

The keys are `slab`, `stairs`, `wall`, `fence`, `fence_gate`, `button`, `pressure_plate`, `door` and
`trapdoor`. Spelling is forgiving: the `additional_information` flag's own name works too, so `walls`
and `fenceGate` are understood as well.

A reference is the *whole* table, so it drops whatever that table drops — pointing a block at another
block's table makes it drop that other block, not itself. Conditions inside the table still name the
block it was written for too: a vanilla slab table doubles its drop when *that* slab is broken as a
double slab, not when yours is. Reference tables you wrote for these blocks; borrow another block's
only when dropping its items is what you meant.

### Drops

Each entry in `drops` is one item the block can drop:

| Field                 | Default | Meaning                                                                         |
|-----------------------|---------|----------------------------------------------------------------------------------|
| `name`                | —       | The item id. Required.                                                           |
| `count`               | `1`     | A number, or `{"min": 2, "max": 5}` for a random amount.                          |
| `fortune`             | —       | How an enchantment adds to `count`. A formula name, or an object (below).        |
| `drops_if_silk_touch` | `false` | Only drops when the tool has Silk Touch.                                         |
| `when`                | —       | Only drops in these block states. See [Dropping by state](#dropping-by-state).   |

Every drop that passes its conditions is given, so a block with three drops gives all three.

### Dropping by state

`when` limits a drop to the block states it names, keyed by state property. A value is either exact or
a range with either bound left out:

```json
"when": { "age": 7 }
"when": { "age": { "min": 2 } }
"when": { "age": { "min": 3, "max": 6 } }
"when": { "half": "upper" }
```

This is what separates a crop's produce from its seed — wheat only from a grown crop, seeds from any
stage:

```json
{
  "drop_information": {
    "drops": [
      { "name": "minecraft:wheat", "when": { "age": 7 } },
      { "name": "minecraft:wheat_seeds", "when": { "age": { "max": 6 } } }
    ]
  }
}
```

It works on any property the block has, not just `age`. A drop takes one condition, so `when` and
`drops_if_silk_touch` on the same drop clash — `when` is applied and the clash is warned about.

`silk_touch_drops_block` is the one place drops are not all given: it is the ore idiom, where Silk
Touch takes the block *instead* of the drops, so the drops are alternatives to one another. Declare a
single drop alongside it; more than one is warned about, since only one of them is rolled.

`fortune` takes the three formulas vanilla has, as a string — `"ore_drops"`,
`"uniform_bonus_count"`, `"binomial_with_bonus_count"` — or as an object when a formula needs
settings, or when the bonus should come from an enchantment other than Fortune:

```json
{ "formula": "uniform_bonus_count", "bonus_multiplier": 1 }
{ "formula": "binomial_with_bonus_count", "extra": 3, "probability": 0.5 }
{ "formula": "ore_drops", "enchantment": "minecraft:looting" }
```

`ore_drops` is what vanilla's ores use: Fortune multiplies the drop by a random 1..(level + 1),
weighted so higher rolls are rarer. `uniform_bonus_count` adds a flat `0..level × bonus_multiplier`,
and `binomial_with_bonus_count` rolls `level + extra` times at `probability` each.

A whole ore, then — Silk Touch gives the block, a pickaxe gives two to five diamonds, and Fortune
raises that the way vanilla's diamond ore does:

```json
{
  "drop_information": {
    "silk_touch_drops_block": true,
    "drops": [
      {
        "name": "minecraft:diamond",
        "count": { "min": 2, "max": 5 },
        "fortune": "ore_drops"
      }
    ],
    "xp_drop_amount": 5
  }
}
```

Blocks that drop a quantity get vanilla's `explosion_decay`, which thins the stack when they are blown
up, rather than the all-or-nothing `survives_explosion` a block dropping itself gets.

## "description"

The Bedrock-style `description` object (`identifier`, `register_to_creative_menu`) belongs to the
**Bedrock** block format, which is loaded from its own directory. Obsidian-format blocks parse it and
ignore it: the id comes from the file name, and the creative tab comes from the block's creative tab
component if it has one, otherwise from `item_properties` → `item_group`. Old files carrying a
`description` still work; the section just does nothing.

## See also

* [Block Types](./BlockTypes.md) — every value `block_type` accepts.
* [Block Settings](./BlockSettings.md) — hardness, sounds, light, piston behaviour.
* [Voxel Shapes](./VoxelShapes.md) — geometry in detail.
* [Chisel Mappings](./ChiselMappings.md) — the item-side of block conversion.
