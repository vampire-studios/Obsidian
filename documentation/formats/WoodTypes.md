# Block Set Types and Wood Types

The two vanilla groupings that decide how doors, trapdoors, buttons, pressure plates, fence gates and
signs sound and behave. Oak, spruce and iron are vanilla's.

A block referencing one of these does so through `information.block_set_type` or
`information.wood_type` — see [Blocks](./Blocks.md#information).

## Where they go

```
obsidian_addons/ExamplePack/content/examplepack/block/block_set_type/cheese.json
obsidian_addons/ExamplePack/content/examplepack/block/wood_type/cheese.json
```

The file name is the id, unless the file sets `id` itself.

## Block set types

A block set type is the *mechanical* half: whether a door can be opened by hand, what a button reacts
to, and which sound each of those actions makes.

```json
{
  "can_open_by_hand": true,
  "can_open_by_wind_charge": true,
  "can_button_be_activated_by_arrows": true,
  "pressure_plate_sensitivity": "EVERYTHING",
  "sound_type": "minecraft:wood",
  "door_open": "minecraft:block.wooden_door.open",
  "door_close": "minecraft:block.wooden_door.close",
  "trapdoor_open": "minecraft:block.wooden_trapdoor.open",
  "trapdoor_close": "minecraft:block.wooden_trapdoor.close",
  "pressure_plate_click_on": "minecraft:block.wooden_pressure_plate.click_on",
  "pressure_plate_click_off": "minecraft:block.wooden_pressure_plate.click_off",
  "button_click_on": "minecraft:block.wooden_button.click_on",
  "button_click_off": "minecraft:block.wooden_button.click_off"
}
```

| Field | Meaning |
| --- | --- |
| `id` | The registry id. Defaults to the file name. |
| `can_open_by_hand` | Whether a player can open the door or trapdoor by clicking it. Iron sets this `false`. |
| `can_open_by_wind_charge` | Whether a wind charge opens it. |
| `can_button_be_activated_by_arrows` | Whether arrows press the button. Wooden buttons set this `true`. |
| `pressure_plate_sensitivity` | `EVERYTHING` or `MOBS`. Wood is `EVERYTHING`, stone is `MOBS`. |
| `sound_type` | The [sound group](./BlockSettings.md#custom-sound-groups) used by blocks of this set. |
| `door_open`, `door_close` | Sound events for doors. |
| `trapdoor_open`, `trapdoor_close` | Sound events for trapdoors. |
| `pressure_plate_click_on`, `pressure_plate_click_off` | Sound events for pressure plates. |
| `button_click_on`, `button_click_off` | Sound events for buttons. |

`pressure_plate_sensitivity` is matched **case-sensitively** against vanilla's enum, so it has to be
written in capitals. Anything else fails the whole entry.

## Wood types

A wood type is the *presentation* half, layered on top of a block set type. Signs and hanging signs need
one; so does a fence gate, for its sounds.

```json
{
  "set_type": "examplepack:cheese",
  "sound_type": "minecraft:wood",
  "hanging_sign_sound_type": "minecraft:hanging_sign",
  "fence_gate_open_sound_type": "minecraft:block.fence_gate.open",
  "fence_gate_close_sound_type": "minecraft:block.fence_gate.close"
}
```

| Field | Meaning |
| --- | --- |
| `id` | The registry id. Defaults to the file name. |
| `set_type` | The block set type this wood type builds on. Required. |
| `sound_type` | Sound group for the wood's own blocks. |
| `hanging_sign_sound_type` | Sound group for hanging signs made from it. |
| `fence_gate_open_sound_type`, `fence_gate_close_sound_type` | Sound events for fence gates. |

Sound events named here are registered on demand if the pack has not already declared them, so a wood
type can point at a sound the pack ships in `assets/<namespace>/sounds.json` and nothing else is needed
to make the id exist.

## Order matters

A wood type resolves its `set_type` while it is being registered, so the block set type has to exist
first. Obsidian loads `block/block_set_type` before `block/wood_type`, which is enough for entries in the
same pack; across packs, the pack defining the set type has to load first.

## See also

* [Blocks](./Blocks.md) — where `block_set_type` and `wood_type` are referenced.
* [Block Types](./BlockTypes.md) — the block types that require them.
* [Pack Structure](../PackStructure.md#assets-and-data) — where the sound files themselves live.
