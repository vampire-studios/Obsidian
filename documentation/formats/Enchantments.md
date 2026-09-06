# Enchantments

> **Obsidian does not load enchantments.** No module reads an enchantment directory on this branch, so
> files in the format below do nothing at all.
>
> **Use a vanilla data pack instead.** Enchantments have been fully data-driven since 1.21, under
> `data/<namespace>/enchantment/`, and the vanilla format is more capable than the one Obsidian had.

The format is kept here as a record of what older versions accepted, and as the design if the module
returns.

## The old format

Files went in `enchantments`, with the file name as the id.

```json
{
  "rarity": "rare",
  "type": "weapon",
  "min_level": 1,
  "max_level": 3,
  "base_cost": 5,
  "blacklisted_enchantments": ["minecraft:smite", "minecraft:bane_of_arthropods"]
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `rarity` | `common` | `common`, `uncommon`, `rare`, `very_rare`. |
| `type` | `breakable` | What it can be applied to — `armor`, `armor_head`, `weapon`, `digger`, `bow`, `trident`, `crossbow`, `wearable`, `fishing_rod`, `vanishable`, and the per-slot armor variants. |
| `min_level` | `1` | Lowest level offered. |
| `max_level` | `1` | Highest level offered. Must be ≥ `min_level`. |
| `base_cost` | `1` | Enchanting cost. Higher makes it harder to get alongside others. |
| `blacklisted_enchantments` | none | Enchantments this one cannot coexist with. |

## The vanilla equivalent

A data pack enchantment lives in the pack's data directory rather than its content directory:

```
ExamplePack/data/examplepack/enchantment/sharpness_plus.json
```

Its fields — `description`, `supported_items`, `weight`, `max_level`, `min_cost`, `max_cost`,
`effects` — are documented in the [Minecraft Wiki](https://minecraft.wiki/w/Enchantment_definition).
`exclusive_set` replaces `blacklisted_enchantments`, and effects can do considerably more than the old
format allowed.

## See also

* [Feature list](../Features.md) — what else is not loaded.
