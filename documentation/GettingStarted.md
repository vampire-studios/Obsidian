# Getting Started

Build a pack that adds a block and an item, from an empty folder. Everything here is JSON; nothing is
compiled.

You need the Obsidian mod and Fabric API installed, and a text editor.

## 1. Make the pack

In your game directory, create `obsidian_addons/ExamplePack/`, and inside it a file called
`addon.info.json`:

```json
{
  "version": 5,
  "format": "JSON",
  "addon": {
    "id": "examplepack",
    "name": "Example Pack",
    "folder_name": "ExamplePack",
    "version": "1.0.0",
    "description": "My first Obsidian pack.",
    "authors": ["Me"],
    "has_assets": true,
    "format": "obsidian"
  }
}
```

Three of these have to agree with the world outside the file:

* `folder_name` must match the folder you just created.
* `id` is your namespace, and also the folder name under `content/`.
* `version` is the **schema** version and must be `5`. Any other value and the pack is skipped entirely.

Launch the game once. The log should contain `Registering obsidian addon: Example Pack`. If it does not,
fix that before writing any content — nothing else will load either.

## 2. Add a block

Create `content/examplepack/block/cheese_block.json`:

```json
{
  "block_type": "block",
  "information": {
    "name": "Cheese Block",
    "block_properties": {
      "hardness": 0.6,
      "resistance": 0.6,
      "sound_group": "minecraft:wool",
      "map_color": "COLOR_YELLOW"
    },
    "item_properties": {
      "item_group": "minecraft:building_blocks"
    }
  }
}
```

**The file name is the id.** This registers `examplepack:cheese_block` — there is no id field in the
file, and renaming the file renames the block.

Relaunch and run `/give @s examplepack:cheese_block`. It exists, but it is untextured.

## 3. Give it a texture

Assets work exactly like a resource pack, under `assets/<your id>/`:

```
ExamplePack/
├── addon.info.json
├── content/examplepack/block/cheese_block.json
└── assets/examplepack/
    ├── blockstates/cheese_block.json
    ├── models/block/cheese_block.json
    ├── models/item/cheese_block.json
    ├── textures/block/cheese_block.png
    └── lang/en_us.json
```

`blockstates/cheese_block.json`:

```json
{ "variants": { "": { "model": "examplepack:block/cheese_block" } } }
```

`models/block/cheese_block.json`:

```json
{ "parent": "minecraft:block/cube_all", "textures": { "all": "examplepack:block/cheese_block" } }
```

`models/item/cheese_block.json`:

```json
{ "parent": "examplepack:block/cheese_block" }
```

## 4. Add an item

Create `content/examplepack/item/cheese_stick.json`:

```json
{
  "information": {
    "name": "Cheese Stick",
    "item_properties": {
      "max_stack_size": 16,
      "rarity": "uncommon",
      "item_group": "minecraft:food_and_drinks"
    }
  },
  "lore": ["Suspiciously soft."]
}
```

With `assets/examplepack/models/item/cheese_stick.json` alongside it:

```json
{ "parent": "minecraft:item/generated", "textures": { "layer0": "examplepack:item/cheese_stick" } }
```

An item whose model sits at `item/<its id>` needs no `rendering` section — that is the default path.

## 5. Make it do something

Items and blocks both take an `events` map. Add this to the item:

```json
{
  "events": {
    "on_use": [
      { "action": "play_sound", "sound": "minecraft:entity.player.burp" },
      { "action": "apply_effect", "effect": "minecraft:speed", "duration": 200, "amplifier": 0 },
      { "action": "send_message", "message": "Cheesy." }
    ]
  }
}
```

Right-clicking now burps, hastens and reports. The full list of events and actions is in
[Events](./Events.md).

## 6. Share the settings

Both files above spelled their settings out inline. Once a second block wants the same ones, move them
into `content/examplepack/block/property/soft.json`:

```json
{
  "hardness": 0.6,
  "resistance": 0.6,
  "sound_group": "minecraft:wool",
  "map_color": "COLOR_YELLOW"
}
```

and reference it by id:

```json
{
  "information": {
    "name": "Cheese Block",
    "block_properties": "examplepack:soft"
  }
}
```

Item settings work the same way, from `item/property`.

## When something does not appear

* **Nothing at all loads.** `version` is not `5`, or `folder_name` does not match the folder.
* **One file does not load.** Search the log for `Failed to register` — each file is handled
  independently, so a broken one does not take the others with it.
* **The item exists but is invisible or purple.** That is assets, not Obsidian: check the model path
  matches the id and that `has_assets` is `true`.
* **A file in a subfolder is ignored.** Directories are not scanned recursively; only files sitting
  directly in `item/` and `block/` are read.

## Next

* [Pack Structure](./PackStructure.md) — every directory Obsidian reads.
* [Blocks](./formats/Blocks.md) — variants, shapes, block states, conversions.
* [Items](./formats/Items.md) — components, templates, per-state models.
