# Tools

Pickaxes, shovels, hoes, axes, and the less ordinary ones.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/tool/cheese_pickaxe.json
```

The file name is the item id. Everything an ordinary [item](./Items.md) accepts works here too —
`components`, `lore`, `rendering`, `events` — plus the fields below.

## Example

```json
{
  "tool_type": "pickaxe",
  "material": "examplepack:cheese",
  "information": {
    "name": "Cheese Pickaxe",
    "item_properties": "examplepack:cheese_tool"
  }
}
```

## Fields

| Field | Meaning |
| --- | --- |
| `tool_type` | Required. See the table below. |
| `material` | An [item tier](./ItemTiers.md) id, or a vanilla one like `minecraft:iron`. Required by every type that mines. |
| `mining_radius` | Hammer, drill and excavator: `1` is 3×3, `2` is 5×5. |
| `chisel_mappings` | Chisel: the block conversions it performs — see [Chisel Mappings](./ChiselMappings.md). |

## Types

| Type | Behaviour |
| --- | --- |
| `pickaxe`, `shovel`, `hoe`, `axe` | The vanilla four. |
| `paxel` | Pickaxe, shovel and axe in one. |
| `mattock` | Hoe, shovel and axe in one. |
| `hammer`, `drill`, `excavator` | Area mining, sized by `mining_radius`. |
| `chisel` | Converts blocks in place, per `chisel_mappings`. |
| `brush` | Vanilla brushing — suspicious sand and gravel. |
| `fishing_rod` | Casting, reeling, and everything the bobber does. |

## Fishing rods

```json
{
  "tool_type": "fishing_rod",
  "information": {
    "name": "Cheese Rod",
    "item_properties": "examplepack:basic_item"
  },
  "rendering": {
    "item_model": "examplepack:item/cheese_rod",
    "cast_model": "examplepack:item/cheese_rod_cast"
  }
}
```

A rod takes no `material` — it mines nothing. Durability comes from the item settings, or 64 if they
set none. Lure and Luck of the Sea work as they do on a vanilla rod, since the bobber reads them off
the stack.

`cast_model` is what shows while the bobber is out; leaving it off means the rod looks the same cast
or not.

## See also

* [Items](./Items.md) — everything else in the file
* [Item Tiers](./ItemTiers.md) — what `material` points at
* [Ranged Weapons](./RangedWeapons.md) — bows, crossbows, tridents
