# Particles

A particle type of the pack's own: its texture, tint, size and lifetime.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/particle/cheese_crumb.json
```

The file name is the particle id, unless the file sets `id` itself. Particles are registered on the
client only — a dedicated server ignores the directory.

## Example

```json
{
  "sheet_type": "TRANSLUCENT",
  "size": 0.6,
  "max_age": 40,
  "red_color": 1.0,
  "green_color": 0.85,
  "blue_color": 0.3,
  "collides_with_world": true,
  "always_spawn": false
}
```

## Fields

| Field | Default | Meaning |
| --- | --- | --- |
| `id` | file name | The particle type id. |
| `sheet_type` | `TRANSLUCENT` | Which layer to draw on. See [Layers](#layers). |
| `always_spawn` | `true` | Spawns regardless of the client's particle setting, and ignores the distance limiter. |
| `collides_with_world` | `false` | Whether the particle stops at blocks instead of passing through. |
| `red_color`, `green_color`, `blue_color` | `1.0` | Tint, each `0.0`–`1.0`. |
| `size` | `1.0` | Scale, multiplied into the particle's quad size. |
| `max_age` | `1` | Lifetime in ticks. Clamped to at least `1`. |

## The texture

The particle's texture comes from a vanilla particle definition in the pack's
[`assets` directory](../PackStructure.md#assets-and-data), the same as any other particle:

```
obsidian_addons/ExamplePack/assets/examplepack/particles/cheese_crumb.json
```

```json
{ "textures": ["examplepack:cheese_crumb"] }
```

```
obsidian_addons/ExamplePack/assets/examplepack/textures/particle/cheese_crumb.png
```

Listing several textures animates the particle across its lifetime, oldest sprite last — the definition
file needs nothing extra to opt into that.

## Layers

`sheet_type` picks the layer the particle draws on:

| Value | Use |
| --- | --- |
| `TRANSLUCENT` | The default. Soft-edged particles: smoke, sparkles, dust. |
| `OPAQUE` | Hard-edged particles with no partial transparency. |
| `TRANSLUCENT_TERRAIN`, `OPAQUE_TERRAIN` | Particles drawn from the block atlas. |
| `TRANSLUCENT_ITEMS`, `OPAQUE_ITEMS` | Particles drawn from the item atlas. |

Older packs wrote render-sheet names instead. Those still load, mapped to the closest layer:
`TERRAIN_SHEET` becomes `OPAQUE_TERRAIN`, `PARTICLE_SHEET_OPAQUE` becomes `OPAQUE_ITEMS`, and anything
unrecognised — including the old `NO_RENDER` default — becomes `TRANSLUCENT`, since an invisible particle
is never what the pack meant.

## Using it

The registered id works anywhere a particle is named:

* a block's `particle_type` field — see [Blocks](./Blocks.md)
* the `spawn_particle` [event action](../Events.md)
* vanilla's `/particle` command

## See also

* [Blocks](./Blocks.md) — the `particle_type` field.
* [Events](../Events.md) — spawning particles from an interaction.
* [Pack Structure](../PackStructure.md#assets-and-data) — where the texture lives.
