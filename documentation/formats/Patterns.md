# Patterns

A shape of blocks that does something once it is built and activated.

Deliberately not called a ritual — the same matching drives a summoning altar, a machine's multiblock,
a lock built out of the right blocks, or a shape that has to be completed before a door opens. The
format describes the shape, what activating it costs, and what happens to it afterwards. What it *does*
is an ordinary [event list](../Events.md).

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/world/pattern/cheese_altar.json
```

The file name is the pattern id. It registers no block and no item — it recognises blocks the pack
already has.

## Example

```json
{
  "pattern": [
    ["ccc", "c#c", "ccc"],
    ["   ", " _ ", "   "]
  ],
  "keys": {
    "c": "examplepack:cheese_block",
    "#": "minecraft:gold_block"
  },
  "anchor": "#",
  "activator": "examplepack:cheese_wand",
  "consume_pattern": true,
  "cost": {
    "items": [{ "item": "examplepack:cheese_ingot", "count": 4 }],
    "experience_levels": 3
  },
  "events": {
    "on_activate": [
      { "action": "summon_entity", "entity": "examplepack:cheese_golem" },
      { "action": "play_sound_at", "sound": "minecraft:entity.wither.spawn" }
    ],
    "on_fail": [
      { "action": "play_sound_at", "sound": "minecraft:block.note_block.bass" }
    ]
  }
}
```

A 3×3 of cheese blocks around a gold block, with air above the middle. Click the gold block with the
wand, pay four ingots and three levels, and a golem appears.

## Fields

| Field | Default | Meaning |
| --- | --- | --- |
| `pattern` | required | The shape, bottom layer first. See [The shape](#the-shape). |
| `keys` | required | What each character means. |
| `anchor` | — | The character the player must click. Without one, any named block in the shape works. |
| `activator` | — | The item that activates it. Without one, an **empty hand** does. |
| `consume_activator` | `false` | Whether the activating item is used up. |
| `consume_pattern` | `false` | Whether the shape's blocks are removed on activation. |
| `replace_with` | — | What the blocks become instead of vanishing. Ignored unless `consume_pattern`. |
| `rotate` | `true` | Whether the shape matches at all four horizontal facings. |
| `cost` | — | What activating costs. See [Cost](#cost). |
| `events` | — | `on_activate` and `on_fail`. See [Events](#events). |

## The shape

`pattern` is a list of layers, **bottom layer first**. Each layer is a list of rows running north to
south, and each character in a row is one block running west to east.

```json
{
  "pattern": [
    ["ooo", "ooo", "ooo"],
    ["o o", "   ", "o o"]
  ]
}
```

That is a 3×3 floor with pillars at its corners.

Two characters are built in and need no entry in `keys`:

| Character | Means |
| --- | --- |
| `' '` (space) | Anything at all — the shape ignores this cell. |
| `'_'` | Must be **air**. Use it to demand clear space, e.g. where an entity will be summoned. |

Everything else is looked up in `keys`, which takes a block id or a `#`-prefixed block tag:

```json
{ "keys": { "c": "examplepack:cheese_block", "s": "#minecraft:stone_bricks" } }
```

A character with no entry in `keys` never matches, and says so in the log.

Layers do not have to be rectangular or the same size — a short row simply ends, and the cells past it
are treated as unspecified.

## Matching

The shape is authored once, facing north. With `rotate` on, it is also tried turned east, south and
west, so a player can build the altar any way round.

The click anchors the match. Without an `anchor`, every cell whose character is not a space is tried as
"the block you clicked", which means clicking any part of the shape activates it. With an `anchor`, only
that character is tried — useful when the shape has an obvious centre and you want clicking the outside
to do nothing.

Naming an `anchor` also makes matching cheaper, since far fewer positions are tested.

## Cost

```json
{
  "cost": {
    "items": [
      { "item": "examplepack:cheese_ingot", "count": 4 },
      { "item": "minecraft:diamond" }
    ],
    "experience_levels": 3,
    "charge_creative": false
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `items` | empty | Items taken from the activating player's inventory. `count` defaults to `1`. |
| `experience_levels` | `0` | Experience levels taken from the player. |
| `charge_creative` | `false` | Whether a creative player pays too. |

The cost is **all or nothing**: it is checked in full before anything is taken, so a player who is one
ingot short loses nothing and gets `on_fail` instead.

## Events

| Event | Fires when |
| --- | --- |
| `on_activate` | The shape matched and the cost was paid. |
| `on_fail` | The shape matched but the cost could not be paid. |

Both run with the shape's position, so positional actions — `summon_entity`, `set_block`, `spawn_loot`,
`play_sound_at`, `strike_lightning` — land at the shape rather than at the player. The player is the
one who clicked, so player-scoped actions work too.

The click is consumed either way once a shape matches, so a failed activation does not also place a
block or open whatever was clicked.

## Order of events

Activation happens in this order, which matters if the actions inspect the world:

1. The cost is checked, then taken.
2. `consume_pattern` removes or replaces the shape's blocks.
3. The activator is consumed, if `consume_activator`.
4. `on_activate` runs.

So an `on_activate` that places something where the shape stood sees clear space, not the shape.

## See also

* [Events](../Events.md) — the action list format.
* [Blocks](./Blocks.md) — the blocks a shape is built from.
* [Entities](./Entities.md) — what a summoning shape summons.
* [Item Stacks](./ItemStack.md) — the item format costs are written in.
