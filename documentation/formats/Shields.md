# Shields

A blocking item: how long it stays disabled after an axe hit, what repairs it, what it sounds like, and
whether banners can be applied to it.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/shield/cheese_shield.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Cheese Shield",
    "item_properties": { "max_damage": 336 }
  },
  "can_have_banner": true,
  "cooldown_ticks": 100,
  "repair_item": "examplepack:cheese_ingot",
  "block_sound": "minecraft:item.shield.block",
  "break_sound": "minecraft:item.shield.break"
}
```

## Fields

A shield is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore` and `events`
all work as they do there. On top of that:

| Field | Default | Meaning |
| --- | --- | --- |
| `can_have_banner` | `true` | Whether a banner can be applied to the shield in a crafting grid. |
| `cooldown_ticks` | `0` | How long the shield is disabled after an axe hit. Vanilla's is `100`. |
| `repair_item` | `minecraft:air` | The item that repairs it in an anvil. |
| `block_sound` | `minecraft:item.shield.block` | Played when a hit is blocked. |
| `break_sound` | `minecraft:item.shield.break` | Played when the shield breaks. |

`cooldown_ticks` defaults to `0`, not to vanilla's hundred — a shield that does not set it is never
disabled by an axe. Write `100` for vanilla behaviour.

`repair_item` defaults to `minecraft:air`, which means "nothing repairs it". Durability comes from
`information.item_properties.max_damage`, as it does for any other item.

Sounds are ordinary sound event ids, resolved from the pack's
[`assets` directory](../PackStructure.md#assets-and-data) or from vanilla.

## Banner patterns

`can_have_banner` only controls whether the shield accepts a banner. Rendering the pattern on a custom
shield model is up to the pack's model and texture; Obsidian does not generate the pattern layers.

## See also

* [Items](./Items.md) — everything a shield inherits.
* [Item Settings](./ItemSettings.md) — durability and stack size.
* [Weapons](./Weapons.md) — the other half of a loadout.
