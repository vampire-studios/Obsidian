# Feature List

Where each part of Obsidian stands. Documented features link to their page.

## Working

* [Blocks](./formats/Blocks.md) — types, variants, [shapes](./formats/VoxelShapes.md),
  [settings](./formats/BlockSettings.md), block state, conversions,
  [templates](./formats/Blocks.md#templates)
* [Items](./formats/Items.md) — [settings](./formats/ItemSettings.md), components, templates,
  per-state models
* [Item tiers](./formats/ItemTiers.md) and [armor materials](./formats/ArmorMaterials.md)
* [Ranged weapons](./formats/RangedWeapons.md) — bows, shortbows, crossbows, heavy crossbows, tridents
* [Projectiles](./formats/Projectiles.md) — thrown items and custom arrows, with their own flight,
  trails, impact events and 3D models; bows and crossbows can name their own ammunition
* [Portals](./formats/Portals.md) — frames linking two dimensions, with the far side built on arrival
* [Patterns](./formats/Patterns.md) — block shapes that summon, transform or trigger when built and activated
* [World events](./formats/WorldEvents.md) — staged fights and sieges with a boss bar, mob waves with
  equipment and stats, guards to protect, tiered rewards, failure conditions, and
  [rifts](./formats/WorldEvents.md#rifts) built from an event, a portal and a block
* [Tools](./formats/Tools.md) — the vanilla four plus paxels, hammers, chisels, brushes, fishing rods
* [Palettes](./formats/Palettes.md) — one item, many colour schemes, without registering a variant
  per colour
* [Food](./formats/Food.md) components and food items, including effects, eating speed and sounds
* [Creative tabs](./formats/ItemGroups.md), in both the current and legacy formats
* [Events](./Events.md) on items and blocks
* [Status effects](./formats/StatusEffects.md) and [potions](./formats/Potions.md)
* [Particles](./formats/Particles.md) — custom particle types, drawn from the pack's own textures
* [Chisel mappings](./formats/ChiselMappings.md) and block [convertibles](./formats/Blocks.md#convertible)
* JSON entity models and animations
* Pack [`assets` and `data`](./PackStructure.md#assets-and-data) directories, so a pack can ship tags,
  recipes, loot tables and advancements alongside its content
* Content pack syncing from server to client

## Working, with gaps

* [Entities](./formats/Entities.md) — components, states, timers, interactions, goal and brain AI
* Ores, worldgen additions, villager professions
* [Cosmetics](./formats/Cosmetics.md) — the item works; the `cosmetics` layer block is read by nothing
* GUIs
* [Cauldron types](./formats/CauldronTypes.md) — apply to every cauldron state; no way to restrict one

## Half-built

* Scripting (`.obs` scripts and the script manager)
* Block `behaviour` — `seat`, `placement`, `power_source`, `repeater`,
  [`container`](./formats/Blocks.md#containers) and [`lock`](./formats/Blocks.md#locks) are implemented;
  `showcase`, `glowing` and `rotate` are specified in the API and read by nothing

## Ideas

* Structures and jigsaw pieces
* Worldgen modification through biome loading
* More station and redstone block types — `lectern`, `grindstone`, `composter`, `jukebox`,
  `cartography_table`, `fletching_table`, `noteblock`, `piston`, `rails`
