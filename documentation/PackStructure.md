# Pack Structure

Everything Obsidian loads comes from an *addon pack*: a folder (or zip) with an info file at its root
and a `content` directory holding the definition files.

## Where packs live

| Directory | Loaded |
| --- | --- |
| `<game dir>/obsidian_addons/` | On both client and server. |
| `<game dir>/server_obsidian_addons/` | Server-side only. |

A pack can be a plain folder or a `.zip`. The folder name must match the `folder_name` declared in the
info file.

## Layout

```
obsidian_addons/
└── ExamplePack/                     <- folder_name
    ├── addon.info.json              <- pack metadata (required)
    ├── fabric.mod.json              <- optional; used for mod metadata if present
    ├── content/
    │   └── examplepack/             <- addon id; becomes the namespace of everything inside
    │       ├── item/
    │       │   └── cheese_stick.json
    │       ├── block/
    │       │   └── cheese_block.json
    │       ├── item/tier/
    │       └── …                    <- one directory per format, see the table below
    ├── assets/                      <- normal resource pack assets
    ├── data/                        <- normal data pack files
    └── scripts/                     <- optional .obs scripts
```

Two rules follow from how the loader walks this tree, and both trip people up:

* **The file name is the registry name.** `content/examplepack/item/cheese_stick.json` registers
  `examplepack:cheese_stick`, and so does `cheese_stick.yml` — the format extension is stripped off.
  Nothing inside the file sets the id.
* **Directories are not scanned recursively.** Only files sitting directly in `item/` are read; a
  `item/tools/axe.json` is silently ignored. (Nested *format* directories like `item/tier/` are separate
  formats with their own loader, not subfolders of `item`.)

## `addon.info.json`

```json
{
  "version": 5,
  "format": "JSON",
  "addon": {
    "id": "examplepack",
    "name": "Example Pack",
    "folder_name": "ExamplePack",
    "version": "1.0.0",
    "description": "An example Obsidian pack.",
    "authors": ["You"],
    "license": "MIT",
    "has_assets": true,
    "format": "obsidian"
  }
}
```

| Field | Meaning |
| --- | --- |
| `version` | **Schema version. Must be `5`.** A pack with any other value is skipped with a message in the log and nothing else — no error, no partial load. |
| `format` | Fallback file format for definition files that have no recognised extension: `JSON`, `TOML`, `YAML`, `HJSON` or `HOCON`. Default `JSON`. A file's own extension wins over this. |
| `addon.id` | Namespace for everything the pack registers, and the directory name under `content/`. |
| `addon.folder_name` | The pack's own folder name under `obsidian_addons/`. |
| `addon.version`, `addon.name`, `addon.description`, `addon.authors`, `addon.license` | Metadata; surfaced through Fabric's mod list. |
| `addon.has_assets` | Whether the pack ships an `assets` directory. |
| `addon.format` | Which loader reads the content: `obsidian` (default), `crucible_like`, or `nexo_like`. Modules check this, so an `obsidian` pack's `item/` files are ignored by the Nexo and Crucible modules and vice versa. |
| `requires`, `breaks`, `optional` | Dependency maps, each entry `{ "<id>": { "version": "…", "type": "MOD" \| "ADDON" } }`. |

Every module reads every format, and the format is decided per file by its extension: `.json`, `.yml`,
`.yaml`, `.toml`, `.hjson`, `.conf` and `.hocon`. One pack can mix them freely — an
`item/cheese_stick.yml` next to an `item/bread.json` — and files with any other extension are skipped.
The pack-wide `format` field only decides how to read a file whose extension is not one of those.

The info file follows the same rule: `addon.info.yml`, `addon.info.toml` and so on are loaded just like
`addon.info.json`.

`addon.info.pack` is the older info file, using flat `displayName` / `namespace` / `folderName` /
`addonVersion` fields instead of the nested `addon` object. It is still loaded, and packs using it are
treated as `obsidian` format.

## `assets` and `data`

Alongside `content`, a pack may carry the two directories any resource or data pack has, in exactly the
vanilla layout:

| Directory | Holds |
| --- | --- |
| `assets/<namespace>/` | Models, blockstates, textures, sounds, language files. |
| `data/<namespace>/` | Tags, recipes, loot tables, advancements, enchantments, worldgen. |

Both are served automatically — `assets` to the client, `data` as a data pack sitting between mods and
the user, so a pack can override what vanilla and mods provide while the player's own data packs still
win.

This is the route for anything the content formats do not cover. A block that should be climbable, for
instance, is a `climbable` block type *plus* an entry in the vanilla tag:

```
ExamplePack/data/minecraft/tags/block/climbable.json
```

```json
{ "replace": false, "values": ["examplepack:rope"] }
```

Content files are read at startup, long before tags and recipes load, which is why these live here
rather than under `content`.

## Content directories

Each format reads one directory under `content/<addon id>/`. Linked entries have a page; the rest are
loaded but not yet documented.

### Items

| Directory                                                       | Contents                                                                                  |
|-----------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| `item`                                                          | [Items](./formats/Items.md)                                                               |
| `item/template`                                                 | Reusable item templates (referenced by an item's `template` field)                        |
| `item/property`                                                 | Named [item settings](./formats/ItemSettings.md), referenced by `item_properties`         |
| `item/tier`                                                     | [Item tiers](./formats/ItemTiers.md)                                                      |
| `item/food`                                                     | [Food](./formats/Food.md)                                                                 |
| `item/food/food_component`                                      | Named [food components](./formats/Food.md#food-components)                                |
| `item/armor`, `item/armor/material`, `item/armor/model`         | Armor and [armor materials](./formats/ArmorMaterials.md)                                  |
| `item/tool`, `item/weapon`, `item/weapon/ranged`, `item/shield` | Tools and weapons — `item/tool` also holds [chisel mappings](./formats/ChiselMappings.md) |
| `item/projectile`                                               | [Projectiles](./formats/Projectiles.md)                                                   |
| `item/elytra`, `item/cosmetic`, `item/sound_playing_item`       | Specialised item types                                                                    |

### Blocks

| Directory                                 | Contents                                                                                                             |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `block`                                   | [Blocks](./formats/Blocks.md)                                                                                        |
| `block/template`                          | Reusable block templates (referenced by a block's `template` field) — see [Templates](./formats/Blocks.md#templates) |
| `block/property`                          | Named [block settings](./formats/BlockSettings.md), referenced by `block_properties`                                 |
| `block/sound_group`                       | [Custom sound groups](./formats/BlockSettings.md#custom-sound-groups)                                                |
| `block/block_set_type`, `block/wood_type` | [Block set and wood types](./formats/WoodTypes.md) for doors, signs, etc.                                            |
| `cauldron_type`                           | [Cauldron types](./formats/CauldronTypes.md)                                                               |

### Creative menu

| Directory                                               | Contents                                                          |
|---------------------------------------------------------|-------------------------------------------------------------------|
| `creative_tab`                                          | Creative tabs — see [Item Groups](./formats/ItemGroups.md)        |
| `item_group`                                            | Legacy creative tabs — see [Item Groups](./formats/ItemGroups.md) |
| `creative_tab/sub_group`, `creative_tab/condensed_item` | Sub-groups and condensed entries                                  |

### World and entities

| Directory                                    | Contents                                                                                                               |
|----------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `entity`                                     | [Entities](./formats/Entities.md)                                                                                      |
| `fluid`                                      | [Fluids](./formats/Fluids.md)                                                                                          |
| `world/biome_modification`                   | [Biome modifications](./formats/BiomeModifications.md) — features, carvers, spawns, weather and environment attributes |
| `world/event`                                | [World events](./formats/WorldEvents.md)                                                                               |
| `world/pattern`                              | [Patterns](./formats/Patterns.md)                                                                                      |
| `world/portal`                               | [Portals](./formats/Portals.md)                                                                                        |
| `villager/profession`, `villager/biome_type` | [Villagers](./formats/Villagers.md) — professions and biome types                                                      |

### Misc

| Directory                                    | Contents                                     |
|----------------------------------------------|----------------------------------------------|
| `palettes`                                   | [Palettes](./formats/Palettes.md)            |
| `status_effect`                              | [Status effects](./formats/StatusEffects.md) |
| `item/potion`                                | [Potions](./formats/Potions.md)              |
| `command`                                    | [Custom commands](./formats/Commands.md)     |
| `gui`                                        | GUI/screen definitions                       |
| `fuel_source/cooking`, `fuel_source/brewing` | [Fuel sources](./formats/FuelSources.md)     |
| `particle`                                   | [Particles](./formats/Particles.md)          |

## See also

* [Getting Started](./GettingStarted.md) — build one of these from scratch.
* [Documentation index](./README.md) — every format page.

## Troubleshooting

* **Nothing from the pack loads.** Check `version` is `5` and that `folder_name` matches the folder on
  disk. Both failures log a line at startup and load nothing else.
* **One file does not load.** Look for `Failed to register <type> <file>` in the log — modules catch per
  file, so one bad definition does not stop the others.
* **A file registered under the wrong name.** The registry name comes from the file name, not from the
  file's contents.
