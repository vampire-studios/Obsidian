# Portals

A portal linking two dimensions: the frame that holds it, the item that lights it, and where it goes.

Obsidian does **not** create the dimension. That is a vanilla worldgen file in the pack's
[`data` directory](../PackStructure.md#assets-and-data). What a portal definition adds is the runtime
half — recognising a frame, lighting it, carrying entities through, and building the portal on the far
side so they can get back.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/world/portal/cheese.json
```

The file name is the portal id. It registers no block of its own — the block that fills the frame is one
the pack declares in [`block/`](./Blocks.md) and names here.

## Example

```json
{
  "block": "examplepack:cheese_portal_block",
  "frame": "examplepack:cheese_block",
  "ignition": "minecraft:flint_and_steel",
  "dimension": "examplepack:cheese_dimension",
  "return_dimension": "minecraft:overworld",
  "scale": 1.0,
  "delay": 80
}
```

Build a frame out of cheese blocks, light it with flint and steel, stand in it for four seconds, and
arrive in the cheese dimension.

## Fields

| Field | Default | Meaning |
| --- | --- | --- |
| `block` | required | The block that fills the lit frame. Also accepted as `portal_block`. See [The portal block](#the-portal-block). |
| `frame` | required | The block the frame is built from. |
| `ignition` | — | The item that lights the frame. Without one, nothing can light it. |
| `dimension` | required | The dimension the portal leads to. |
| `return_dimension` | `minecraft:overworld` | Where a portal in `dimension` leads back to. |
| `scale` | `1.0` | Coordinate scaling on the way out, inverted coming back. The Nether's is `0.125`. |
| `delay` | `80` | Ticks an entity must stand in the portal before it travels. |
| `confusion` | `true` | Whether standing in it applies vanilla's nausea-and-wobble. |
| `sound` | — | Played when a frame is lit. |
| `min_width`, `max_width` | `2`, `21` | Allowed width of the opening. |
| `min_height`, `max_height` | `3`, `21` | Allowed height of the opening. |

## The portal block

The block that fills a lit frame is an ordinary [block](./Blocks.md) the pack declares:

```json
{
  "information": {
    "name": "Cheese Portal",
    "block_properties": {
      "collidable": false,
      "luminance": 11,
      "hardness": -1.0,
      "resistance": 3600000.0
    }
  },
  "rendering": { "model": "examplepack:block/cheese_portal" }
}
```

Everything about how it looks and behaves is declared there, not in the portal file — model, texture,
light, sounds, collision, particles. The portal definition only says what it *does*.

Give it the `horizontal_axis` vanilla property if the model should turn with the frame:

```json
{ "information": { "vanilla_properties": ["horizontal_axis"] } }
```

Obsidian sets `axis` when it fills a frame if the block has that property, and leaves it alone if it
does not — a symmetrical model looks the same either way and needs no property.

Make it unbreakable and non-collidable, as vanilla's portals are, or players will walk into a wall and
be able to mine the portal out.

## The frame

The frame works like a nether portal's: a rectangular hole walled in on both sides, along the top and
along the bottom, standing on either horizontal axis. The corners are not checked, so a frame can be
built with or without them.

Lighting is a right-click with `ignition` on a **frame block**; the search starts from the face that was
clicked. Air and fire both count as an empty opening, so flint and steel works even though it drops fire
on the way in.

Both axes are tried, X first. A frame that would be valid on both is registered on X.

## Travel

Standing in the portal for `delay` ticks moves the entity. Which way depends on where it already is: a
portal standing in `dimension` leads back to `return_dimension`, and one anywhere else leads out to
`dimension`. The same definition, and the same block, serve both directions.

The destination is the entity's position with X and Z multiplied by `scale` going out, and divided by it
coming back. Y is kept. Because the mapping is deterministic, going back through the far portal lands
near where you left.

### The far side

On arrival, Obsidian looks for a portal block of the same kind within 16 blocks vertically of the mapped
position. If it finds none, it **builds one**: a minimum-size frame out of `frame`, standing on the
surface at that column, filled with portal blocks.

That is deliberately simpler than vanilla's nether linking, which searches a wide area and can drop you
somewhere surprising. The trade-off is that two portals built close together in one dimension can map to
the same spot in the other and end up sharing a far-side portal.

## Dimension files

`dimension` names a dimension the pack ships as ordinary worldgen data:

```
obsidian_addons/ExamplePack/data/examplepack/dimension/cheese_dimension.json
```

```json
{
  "type": "examplepack:cheese_dimension_type",
  "generator": {
    "type": "minecraft:noise",
    "settings": "minecraft:overworld",
    "biome_source": {
      "type": "minecraft:fixed",
      "biome": "minecraft:plains"
    }
  }
}
```

with the dimension type beside it in `data/examplepack/dimension_type/`. Those are vanilla formats and
vanilla's rules apply — in particular, **a dimension has to exist when the world is created**; adding one
to an existing world does not generate it.

A portal naming a dimension that does not exist logs a warning and refuses to travel; the block still
stands.

## See also

* [Blocks](./Blocks.md) — the portal block and the frame block.
* [World Events](./WorldEvents.md#rifts) — combining a portal with an event to make a rift.
* [Pack Structure](../PackStructure.md#assets-and-data) — where the dimension files go.
* [Biome Modifications](./BiomeModifications.md) — changing what generates in a dimension.
