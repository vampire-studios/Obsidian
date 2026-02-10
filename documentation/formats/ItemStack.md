# Item Stack Definitions

Item stacks are used throughout Obsidian for defining items with counts and components.

Obsidian accepts item stacks as either a string or an object. The object form follows vanilla's `ItemStack.CODEC` format.

## String form

A plain item identifier creates a stack of count 1:

```json
"minecraft:stick"
```

## Object form

The object form accepts the standard vanilla fields (typically `id`, `count`, and optional `components`):

```json
{
  "id": "minecraft:stick",
  "count": 16,
  "components": {
    "minecraft:custom_name": {"text": "Bundle of Sticks"}
  }
}
```

Exact component keys and values follow the vanilla component format used by the game version you are targeting.