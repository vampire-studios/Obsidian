# Ingredients

A test for "is this the right item". Used inside other files, never on its own — most visibly as the
`conversion_item` on a block [`convertible`](./Blocks.md#convertible).

## By item

```json
{ "item": "minecraft:clay" }
```

## By tag

```json
{ "tag": "c:string" }
```

Any item in the tag matches, which is usually what you want for a family of tools:

```json
{ "conversion_item": { "tag": "minecraft:pickaxes" } }
```

## Limits

Only `item` and `tag` are supported. Custom, mod-defined ingredient types cannot be used in content
packs — packs are read while the game is still starting up, before those types exist.

Give exactly one of the two. `item` is checked first, so an object with both silently ignores its `tag`,
and an object with neither is dropped entirely — as is an `item` naming something that does not exist.

## See also

* [Item Stacks](./ItemStack.md) — for *describing* an item rather than matching one.
* [Chisel Mappings](./ChiselMappings.md) — where a richer block-side predicate is available.
