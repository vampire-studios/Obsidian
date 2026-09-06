# Chisel Mappings

A tool that converts one block into another on right-click.

Obsidian compiles the mappings into vanilla's `minecraft:block_transformer` item component — the same
mechanism axes, shovels and hoes use for stripping, paths and tilling — so the conversion is handled by
the game rather than at runtime.

## Where it goes

On a tool in `item/tool`, with `"tool_type": "CHISEL"`.

## Example

```json
{
  "tool_type": "CHISEL",
  "chisel_mappings": [
    {
      "from": "minecraft:stone_bricks",
      "to": "minecraft:chiselled_stone_bricks",
      "sound": "minecraft:item.axe.scrape"
    }
  ]
}
```

| Field | Meaning |
| --- | --- |
| `from` | What to convert. A block id, or a `BlockPredicate` object. |
| `to` | What to convert into. A block id, or a `BlockStateProvider` object. |
| `sound` | Sound played on conversion. Optional. |

When `to` is a plain block id, the existing block's state properties are **carried over** — chiselling a
stair keeps its facing and half.

## Advanced `from`

Anything that isn't a string is decoded with vanilla's `BlockPredicate` format, so tags and boolean
combinators work:

```json
{
  "from": { "type": "minecraft:matching_block_tag", "tag": "minecraft:base_stone_overworld" },
  "to": "minecraft:cobblestone"
}
```

## Advanced `to`

Anything that isn't a string is decoded with vanilla's `BlockStateProvider` format. Every provider the
game ships with is available:

| Provider | Effect |
| --- | --- |
| `minecraft:simple_state_provider` | A fixed state. Does **not** copy properties. |
| `minecraft:weighted_state_provider` | Weighted random choice between states. |
| `minecraft:randomized_int_state_provider` | Randomises an integer property on the result. |
| `minecraft:rotated_block_provider` | Orients the result, optionally to a fixed direction. |
| `minecraft:noise_provider`, `minecraft:dual_noise_provider`, `minecraft:noise_threshold_provider` | Result varies with world position. |
| `minecraft:rule_based_state_provider` | Nested predicate → provider rules with a fallback. |

```json
{
  "from": "minecraft:stone_bricks",
  "to": {
    "type": "minecraft:weighted_state_provider",
    "entries": [
      { "weight": 4, "data": { "Name": "minecraft:cracked_stone_bricks" } },
      { "weight": 1, "data": { "Name": "minecraft:mossy_stone_bricks" } }
    ]
  }
}
```

Note that using an object form replaces the property-copying behaviour — wrap it in
`minecraft:copy_properties_provider` if you want properties carried over as well.

## Shared conversion options

These are available on chisel mappings and on block [`convertible`](./Blocks.md) declarations alike, and
map straight onto vanilla's block transformer:

| Field | Meaning |
| --- | --- |
| `particle` | `none`, `scrape`, `wax_on` or `wax_off`. |
| `disallowed_faces` | Faces the block may not be converted from, e.g. `["down"]`. |
| `loot` | Loot table dropped by the conversion. |
| `drop_strategy` | `clicked_face` or `from_middle` — where drops appear. |
| `update_from_neighbors` | Whether the new block re-evaluates its state from neighbours. |
| `transform_type` | `single_block` or `copper_chest`. |
| `consume_on_use` | Consume one item per conversion. |
| `item_damage_per_use` | Durability removed per conversion. |

```json
{
  "from": "minecraft:stone_bricks",
  "to": "minecraft:chiselled_stone_bricks",
  "particle": "scrape",
  "disallowed_faces": ["down"],
  "item_damage_per_use": 1,
  "loot": "examplepack:chiselling/stone_bricks"
}
```

Unknown values are skipped with a warning naming the field and the file, so a typo disables one option
rather than the whole mapping.

## See also

* [Blocks](./Blocks.md#convertible) — the block-side equivalent, declared on the block instead of the tool.
* [Ingredients](./Ingredient.md) — the simpler item test used there.

## Unsupported fields

`reversible`, `reversal_item` and `dropped_item` are parsed but ignored, and log a warning naming the
mapping:

* **`reversible` / `reversal_item`** — the component has no inverse. Declare the reverse as its own
  chisel mapping on the reversal item.
* **`dropped_item`** — the component expresses drops as a loot table, not a single item.
