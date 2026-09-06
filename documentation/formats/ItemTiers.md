# Item Tiers

A tool material: how long tools made from it last, how fast they mine, how hard they hit, and what
repairs them. Wood, stone, iron and diamond are vanilla's.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/tier/cheese.json
```

The file name is the tier id. Registering a tier creates no items — tools and weapons in `item/tool` and
`item/weapon` reference it.

## Example

```json
{
  "durability": 250,
  "mining_speed": 6.0,
  "attack_damage": 2.0,
  "enchantability": 14,
  "repair_item": "examplepack:cheese_ingot",
  "repair_tag": "c:ingots/cheese",
  "incorrect_blocks_for_drops": "minecraft:incorrect_for_stone_tool"
}
```

## Fields

`repair_tag` is optional; without it, the tier uses an empty tag named `<tier>_repair_items`.

| Field | Meaning |
| --- | --- |
| `durability` | Uses before breaking, before Unbreaking. Iron is `250`, diamond `1561`. |
| `mining_speed` | Mining speed multiplier. Iron is `6.0`, diamond `8.0`. |
| `attack_damage` | Damage bonus added on top of the tool type's base. |
| `enchantability` | Higher values give better enchantments per level. Gold is `22`, diamond `10`. |
| `repair_item` | One concrete item that repairs tools and weapons made from this tier. |
| `repair_tag` | Item tag whose members repair tools and weapons made from this tier. |
| `incorrect_blocks_for_drops` | Block tag of what this tier is *too weak* to harvest — e.g. `minecraft:incorrect_for_stone_tool`. |

## repair_tag

The tag is applied through the tool material, which writes vanilla's `repairable` item component when
the tool or weapon properties are built.

```json
{ "repair_tag": "c:ingots/cheese" }
```

`repair_item` is applied directly to the item's `repairable` component and takes precedence when both
fields are present.

## incorrect_blocks_for_drops is inverted

It names what the tier is too weak for, not what it can mine. A tier that should behave like iron uses
`minecraft:incorrect_for_iron_tool`.

## See also

* [Items](./Items.md) — the item format itself.
* [Armor Materials](./ArmorMaterials.md) — the armor equivalent.
