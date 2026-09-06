# Item Stacks

An item with a count and optional components. Used inside other files, never on its own.

## String form

A bare item id is a stack of one:

```json
"minecraft:stick"
```

## Object form

The object form is vanilla's `ItemStack` format — the same shape data packs and `/give` use:

```json
{
  "id": "minecraft:stick",
  "count": 16,
  "components": {
    "minecraft:custom_name": { "text": "Bundle of Sticks" }
  }
}
```

| Field | Meaning |
| --- | --- |
| `id` | Item id. |
| `count` | Stack size. Defaults to 1. |
| `components` | Data components, in the vanilla format for the version you are targeting. |

## Gotcha

A malformed object decodes to an **empty stack** rather than an error, so a typo in `id` shows up as
"nothing happened" with no log line. The string form has no such failure mode — prefer it whenever you
only need one item.

## See also

* [Ingredients](./Ingredient.md) — for *matching* an item rather than describing one.
