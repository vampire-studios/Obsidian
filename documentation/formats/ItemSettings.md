# Item Settings

The physical properties of an item: how it stacks, how long it lasts, which creative tab it lives in.

This is the value of `information.item_properties` on an [item](./Items.md) or a
[block](./Blocks.md#information) — written inline, or shared as a named entry.

## Where it goes

Named entries live in `item/property`, with the file name as the id:

```
obsidian_addons/ExamplePack/content/examplepack/item/property/cheese_tool.json
```

```json
{
  "max_stack_size": 1,
  "max_uses": 250,
  "rarity": "uncommon",
  "is_enchantable": true,
  "enchantability": 14,
  "item_group": "examplepack:cheese"
}
```

Anything that takes settings then accepts either the id or the object itself:

```json
{ "information": { "item_properties": "examplepack:cheese_tool" } }
```

```json
{ "information": { "item_properties": { "max_stack_size": 16 } } }
```

## Fields

### Basics

| Field | Default | Meaning |
| --- | --- | --- |
| `parent` | — | Id of another settings entry to inherit unset values from. |
| `item_group` | `minecraft:building_blocks` | [Creative tab](./ItemGroups.md) the item appears in. |
| `max_stack_size` | `64` | Stack size, 1–99. |
| `max_uses` | `0` | Durability. `0` means no durability bar. Ignored when the item sets `"damageable": false`. |
| `rarity` | `common` | `common`, `uncommon`, `rare` or `epic`. Controls the name colour. |
| `fireproof` | `false` | Survives fire and lava as a dropped item. |
| `tooltip_style` | — | Sprite id in the `minecraft:tooltip_background` atlas, for a custom tooltip frame. |
| `use_cooldown` | — | Seconds the whole item type is on cooldown after being used. |
| `craft_remainder` | — | What is left in the grid after crafting with it, the way a bucket is left behind. |
| `repairs_with` | — | What repairs it in an anvil: an item id, or `#` and an item tag. |

### Burning

| Field | Default | Meaning |
| --- | --- | --- |
| `is_fuel` | `false` | Usable as furnace fuel. |
| `fuel_duration` | `0` | Burn time in ticks. One item smelts per 200. |
| `fuel` | — | `{ "duration": <ticks>, "return_item": <id> }` — fuel that leaves something behind, the way a lava bucket leaves a bucket. |

### Enchanting

| Field | Default | Meaning |
| --- | --- | --- |
| `is_enchantable` | `false` | Accepted by the enchanting table. |
| `enchantability` | `5` | Higher values give better enchantments per level. Gold is `25`, diamond `10`. |
| `has_enchantment_glint` | default | `true` forces the glint on, `false` forces it off, omitted follows the enchantments. |

### Wearing, placing, dyeing

| Field | Default | Meaning |
| --- | --- | --- |
| `wearable` | `false` | Equippable. |
| `wearable_slot` | — | Which slot it goes in. |
| `can_place_block` | `false` | Placing item, like a seed. |
| `placable_block` | — | The block it places. Required when `can_place_block` is set. |
| `dyeable` | `false` | Leather-style dyeing. |
| `default_color` | `10511680` | Undyed colour, as a packed integer. |

## Inheritance

`parent` chains a settings entry onto another:

```json
{
  "parent": "examplepack:cheese_tool",
  "max_uses": 500
}
```

Fields the child writes win; every field it leaves out keeps the parent's value, so `parent` is best
for a family of items that differ in one or two numbers. A parent may name a parent of its own, up to
a depth of 8 — which is also what stops settings that inherit from themselves.

## Older spellings

The names older packs used are still read — translated to the current ones before anything else
happens — and each one that turns up is named once in the log. A file that uses both spellings keeps
the current one.

| Older name                  | Read as                  |
|-----------------------------|--------------------------|
| `has_glint`, `glint`        | `has_enchantment_glint`  |
| `stack_size`, `maxStackSize`| `max_stack_size`         |
| `durability`, `max_damage`  | `max_uses`               |
| `creative_tab`, `group`     | `item_group`             |
| `enchantable`               | `is_enchantable`         |
| `fuel_time`, `burn_time`    | `fuel_duration`          |

## Settings vs components

Several of these overlap with vanilla data components — stack size, rarity, fireproofing, food. When
both are present the **component wins**, because components are applied last.

Components cover far more of the game than these settings do and are the vanilla-supported path, so for
a new pack the settings worth using are the ones with no component equivalent (`item_group`, `parent`,
the fuel and placement fields) and the components are worth using for the rest.

## See also

* [Items](./Items.md) — where settings are referenced from.
* [Block Settings](./BlockSettings.md) — the block-side equivalent.
