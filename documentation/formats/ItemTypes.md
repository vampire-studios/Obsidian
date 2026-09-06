# Item Types

The `type` field of an [item definition](./Items.md) swaps in a different item implementation. It is
optional: an item without a `type` is an ordinary item, which is what most definitions want — durability,
stack size, food, tools and armor all come from settings, components and the dedicated directories
rather than from a type.

Values are the uppercase names below, and are matched exactly — a misspelled or lowercase value is
read as no type at all, so the item registers as an ordinary item with no error.

## `SHEARS`

Shear-style behaviour. Right-clicking an entity listed in the item's `drops` map spawns the mapped item
and damages the held stack by 1.

```json
{
  "type": "SHEARS",
  "drops": {
    "minecraft:sheep": "minecraft:white_wool"
  }
}
```

The drops map is required for the behaviour to do anything — a `SHEARS` item with no `drops` behaves
like a normal item.

## `BUNDLE`

A bundle-style container item.

## `CUSTOM_MENU`

An item that opens a chest-style menu when used. The menu comes from the item's `menu_config` field.

Give that field a `loot_pool` and the item becomes a crate: using it rolls a weighted pool once, consumes
the item, and either shows the rewards in a read-only menu or hands them straight over.

```json
{
  "type": "CUSTOM_MENU",
  "menu_config": {
    "title": "<gold>Treasure Crate",
    "rows": 1,
    "loot_pool": {
      "rolls": 3,
      "entries": [
        { "item": "minecraft:diamond", "weight": 1, "min_count": 1, "max_count": 3 },
        { "item": "minecraft:iron_ingot", "weight": 20, "min_count": 8, "max_count": 16 }
      ]
    }
  }
}
```

See [Menu Config](./CustomMenu.md) for the full format. An item of this type with no `menu_config`
behaves like an ordinary item.

## See also

* [Items](./Items.md) — the item format itself.
* [Menu Config](./CustomMenu.md) — menus and crates in full.
* [Events](../Events.md) — for behaviour that does not need a dedicated type, which is most of it.
