# Cosmetics

A worn item with no combat stats — a hat, a cape, a badge.

A cosmetic file registers a working item — wearable and dyeable like any other. Its `cosmetics` block,
which was meant to add visual layers, is not read by anything; see
[The `cosmetics` section](#the-cosmetics-section).

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/cosmetic/cheese_hat.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Cheese Hat",
    "item_properties": {
      "max_count": 1,
      "wearable": "head"
    }
  }
}
```

## What works

A cosmetic is an [item](./Items.md) first, and the item half is fully functional:

* **Wearable.** Declaring `wearable` in the [item settings](./ItemSettings.md) puts it in an equipment
  slot, with the worn texture coming from the equipment layer as it does for [armor](./Armor.md).
* **Dyeable.** A dyeable cosmetic gets vanilla's `dyed_color` component, seeded from `default_color` in
  its settings, or from the parent settings when it declares none.
* Everything else an item has — `components`, `rendering`, `lore`, `events`, creative tab placement.

For a hat, a cape or a trinket, that is the whole feature. Reach for `item/cosmetic` over plain `item`
when the thing is worn and does nothing else.

## The `cosmetics` section

`Cosmetic` also parses a `cosmetics` object — layers, transforms, particle trails, auras, glow and
animations. **Nothing reads it.** No renderer, no equipment layer, nothing: the object is parsed and
dropped. It is not documented here because writing one has no effect; treat `item/cosmetic` as an item
format until that changes.

## See also

* [Items](./Items.md) — everything a cosmetic inherits.
* [Item Settings](./ItemSettings.md) — `wearable` and `default_color`.
* [Armor](./Armor.md) — worn items that do have stats.
