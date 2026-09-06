# Villagers

Two formats: **professions**, the job a villager can take, and **biome types**, the look a villager is
born with.

## Professions

### Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/villager/profession/tinker.json
```

The file name is the profession id — the file above registers `examplepack:tinker`.

### Example

```json
{
  "name": "Tinker",
  "poi": {
    "id": "examplepack:tinkering_bench",
    "blocks": ["examplepack:tinkering_bench"],
    "ticket_count": 1,
    "search_distance": 1
  },
  "work_sound": "minecraft:entity.villager.work_toolsmith",
  "harvestable_items": ["minecraft:iron_ingot"],
  "secondary_poi": ["minecraft:anvil"],
  "trades": {
    "1": "examplepack:tinker_level_1",
    "2": "examplepack:tinker_level_2"
  }
}
```

### Fields

| Field | Meaning |
| --- | --- |
| `name` | Display name. A [name](./Names.md) object or a plain string. Left out, the profession uses the translation key `entity.<namespace>.villager.<profession>`, so a pack can ship the name as a translation instead. |
| `poi` | The job site. Required unless it names one that already exists — see below. |
| `poi.id` | Id of the point of interest type. Defaults to the profession's own id. |
| `poi.blocks` | Blocks that are the job site. **Every** state of each block is claimed, so the block works from any facing. |
| `poi.ticket_count` | How many villagers can claim one job site block. Default `1`, which is what vanilla job sites use. |
| `poi.search_distance` | How far the point of interest counts as reached. Default `1`. |
| `work_sound` | Sound played while working. Optional. |
| `harvestable_items` | Items the villager picks up and shares with other villagers, the way a farmer shares wheat. Optional. |
| `secondary_poi` | Blocks the villager works with besides its job site, the way a farmer works farmland. Optional. |
| `trades` | Trade sets by villager level, `1` through `5`. Optional — see below. |

### The job site

A profession is only reachable through its job site block: an unemployed villager walks to a claimable
job site, and the profession whose job site it is becomes theirs. Obsidian registers the point of
interest from `poi` and adds it to `minecraft:acquirable_job_site` for you, so placing the block next to
an unemployed villager is all it takes.

Two ways to fill in `poi`:

* **Give it `blocks`.** A new point of interest type is registered under `poi.id`.
* **Give it only an `id` that already exists**, such as `minecraft:farmer` or a point of interest
  another pack registered. Nothing new is registered and the profession shares that job site.

A block can belong to only one point of interest type, and a job site can belong to only one
profession — if two professions share a job site, the first one registered claims every villager that
takes it.

### Trades

`trades` maps a villager level to a `trade_set` the pack ships as data:

```
obsidian_addons/ExamplePack/data/examplepack/trade_set/tinker_level_1.json
```

Trade sets and the trades inside them are vanilla data pack formats, not Obsidian formats. A profession
with no `trades` registers fine and simply has nothing to offer at any level.

### Textures

The client looks for the profession's overlay at

```
assets/examplepack/textures/entity/villager/profession/tinker.png
```

in the pack's own namespace. Without it the villager renders with the missing texture.

## Biome types

### Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/villager/biome_type/volcanic.json
```

The file name is the villager type id.

### Example

```json
{
  "name": "Volcanic",
  "biomes": [
    "minecraft:basalt_deltas",
    "examplepack:ashfields"
  ]
}
```

| Field | Meaning |
| --- | --- |
| `name` | Display name. Unused by the game today — villager types are identified by their texture. |
| `biomes` | Biomes whose villagers are born this type. Listing a biome vanilla already assigns takes it over. |

Villagers pick their type from the biome they spawn in, so the type applies to villagers generated
after the pack is installed, not to ones already in the world.

### Textures

Each type needs its two body textures in the pack's namespace:

```
assets/examplepack/textures/entity/villager/type/volcanic.png
assets/examplepack/textures/entity/villager/baby/volcanic.png
```

## See also

* [Pack Structure](../PackStructure.md) — where these directories live.
* [Names](./Names.md) — the `name` field.
