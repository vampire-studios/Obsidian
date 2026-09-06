# Elytras

A glider worn in the chest slot, with its own wing texture.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/elytra/reinforced_elytra.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Reinforced Elytra",
    "item_properties": { "max_damage": 864 }
  },
  "rendering": {
    "equipment": {
      "elytra": { "texture": "examplepack:reinforced_elytra" }
    }
  }
}
```

## Fields

An elytra is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore` and `events`
all work as they do there. The only thing it adds is the wing texture.

| Field | Meaning |
| --- | --- |
| `rendering.equipment.elytra.texture` | The wing texture drawn on the wearer's back. Also accepted under `wings`. |

Leave it out and the elytra keeps vanilla's wings.

## Where the texture goes

The texture id resolves to the equipment texture path, not an item texture:

```
obsidian_addons/ExamplePack/assets/examplepack/textures/entity/equipment/wings/reinforced_elytra.png
```

The inventory sprite is separate, and comes from the item model like any other item's.

## The legacy `texture` field

Older packs put the wing texture at the top level:

```json
{ "texture": "examplepack:reinforced_elytra" }
```

This still works and needs no migration, but `rendering.equipment.elytra` is where it belongs now — it
keeps the wing texture with the item's other rendering fields. When both are present, the one under
`rendering` wins.

## Durability and flight

Flight behaviour is vanilla's: the elytra takes a point of damage per second of gliding and stops
working at one durability left. Durability itself comes from
`information.item_properties.max_damage`, and repair from the item's `repairable` component — see
[Item Settings](./ItemSettings.md).

## See also

* [Items](./Items.md) — everything an elytra inherits.
* [Armor](./Armor.md) — the other equipment items.
* [Cosmetics](./Cosmetics.md) — worn items with no stats.
