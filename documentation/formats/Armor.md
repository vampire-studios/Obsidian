# Armor

Wearable armor: helmets, chestplates, leggings and boots for players, and barding for wolves, horses,
llamas and happy ghasts.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/armor/cheese_helmet.json
```

The file name is the item id. The stats live in a separate [armor material](./ArmorMaterials.md), so a
four-piece set is four files pointing at one material.

## Example

```json
{
  "information": {
    "name": "Cheese Helmet",
    "item_properties": { "max_count": 1 }
  },
  "material_id": "examplepack:cheese",
  "slot": "helmet"
}
```

## Fields

An armor piece is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore` and
`events` all work as they do there. On top of that:

| Field | Default | Meaning |
| --- | --- | --- |
| `material_id` | — | The [armor material](./ArmorMaterials.md) supplying protection, durability and repair. Also accepted as `armor_material`. |
| `armor_template` | `LEATHER` | Vanilla material to copy when no `material_id` is given. See [Templates](#templates). |
| `slot` | required | Which piece this is. Also accepted as `armor_slot`. |
| `armor_type` | `HUMANOID` | Who wears it. See [Armor types](#armor-types). |

## slot

`slot` is required for humanoid armor and matched case-insensitively. Both the current names and the
older equipment-slot names are accepted:

| Write | Or the older | Piece |
| --- | --- | --- |
| `helmet` | `head` | Helmet |
| `chestplate` | `chest` | Chestplate |
| `leggings` | `legs` | Leggings |
| `boots` | `feet` | Boots |

`body` is also valid, and is the slot used by animal barding.

A humanoid armor piece with no `slot` fails with *"Humanoid armor is missing its slot"* — there is no
default.

## Templates

Without a `material_id`, `armor_template` picks a vanilla material to copy wholesale:

```json
{ "armor_template": "DIAMOND", "slot": "chestplate" }
```

`LEATHER`, `COPPER`, `CHAINMAIL`, `IRON`, `GOLD`, `DIAMOND`, `TURTLE_SCUTE`, `NETHERITE`,
`ARMADILLO_SCUTE` and `CUSTOM`.

Naming a `material_id` is enough to select a custom material on its own — `CUSTOM` exists for packs
written before that was true and does not need to be repeated. `CUSTOM` with no `material_id` falls back
to leather's stats.

## Armor types

| Value | Worn by |
| --- | --- |
| `HUMANOID` | Players and mobs with the four armor slots. The default. |
| `WOLF` | Wolf armor. |
| `HORSE` | Horse barding. |
| `LLAMA` | Llama carpet. |
| `NAUTILUS` | Happy ghast harness. |

Non-humanoid types occupy the body slot, so they need no `slot` of their own.

## Textures

The worn texture is the [armor material](./ArmorMaterials.md)'s, not the item's — the item's `rendering`
covers the inventory model only. See [Armor Materials](./ArmorMaterials.md#textures) for the layer
texture paths.

## See also

* [Armor Materials](./ArmorMaterials.md) — protection, durability, repair and worn textures.
* [Items](./Items.md) — everything an armor piece inherits.
* [Item Settings](./ItemSettings.md) — stack size and rarity.
