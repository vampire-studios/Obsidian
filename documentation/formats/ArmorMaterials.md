# Armor Materials

An armor material is the shared set of stats behind a set of armor pieces: protection, durability,
enchantability, and what repairs it.

Materials go in `item/armor/material`, and the file name is the material's id:

```
obsidian_addons/ExamplePack/content/examplepack/item/armor/material/rubber.json
```

Registering a material creates no items. Armor pieces in `item/armor` reference it.

## Basic structure of the JSON file

```json
{
  "durability": 15,
  "enchantability": 10,
  "toughness": 2.0,
  "knockback_resistance": 0.1,
  "equip_sound": "minecraft:item.armor.equip_chain",
  "repair_item": "examplepack:rubber",
  "defense": {
    "BOOTS": 2,
    "LEGGINGS": 5,
    "CHESTPLATE": 6,
    "HELMET": 2,
    "BODY": 5
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `durability` | `0` | Durability multiplier, applied per slot the vanilla way — iron is `15`, diamond `33`. Not a raw hit count. |
| `defense` | 1/2/3/1/3 | Armor points per slot, keyed by `BOOTS`, `LEGGINGS`, `CHESTPLATE`, `HELMET` and `BODY` (`BODY` is for wolf and horse armor). Omitted slots keep the default shown. |
| `toughness` | `0` | Armor toughness — reduces how much high-damage hits cut through armor. Diamond is `2`. |
| `knockback_resistance` | `0` | Fraction of knockback ignored, `0` to `1`. Netherite is `0.1`. |
| `enchantability` | `0` | Higher values give better enchantments per level. Gold is `25`, diamond `10`. |
| `equip_sound` | leather | Sound played on equip. Resource location; falls back to the leather sound if unknown. |
| `repair_item` | — | One concrete item that repairs this armor in an anvil. Written directly to the item's `repairable` component. |
| `repair_tag` | empty material-local tag | Item tag whose members repair this armor in an anvil. |

`name` is set from the file name; a `name` in the file is overwritten.

If both repair fields are present, `repair_item` takes precedence.

The slot keys in `defense` are the enum constants, so they are uppercase — `"BOOTS"`, not `"feet"`.

## Textures

Armor textures are not declared on the material. Each custom armor item declares its worn layers under
`rendering.equipment`, separately from its inventory model:

```json
{
  "slot": "helmet",
  "material_id": "examplepack:rubber",
  "rendering": {
    "equipment": {
      "humanoid": "examplepack:rubber_helmet"
    }
  }
}
```

The texture above goes at
`assets/examplepack/textures/entity/equipment/humanoid/rubber_helmet.png`. Leggings use the
`humanoid_leggings` layer and directory. Obsidian generates the equipment-model JSON that connects
the item to those textures.

## See also

* [Items](./Items.md) — armor pieces are items.
* [Item Tiers](./ItemTiers.md) — the tool equivalent.
