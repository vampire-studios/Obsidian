# Block Definitions

Blocks define placed world blocks and their behaviors.

Block definitions go in the `block` directory in the thing pack.

E.g.
```
/things/examplepack/block/cheese_block.json
```

## Basic structure of the JSON file

```json
{
  "description": {
    "identifier": "examplepack:cheese_block",
    "register_to_creative_menu": true
  },
  "block_type": "block",
  "information": {
    "name": {
      "text": "Cheese Block"
    },
    "block_set_type": "minecraft:oak",
    "wood_type": "minecraft:oak",
    "block_properties": "examplepack:soft_block",
    "item_properties": "examplepack:soft_item"
  },
  "drop_information": {
    "experience": 0
  },
  "additional_information": {
    "stairs": true,
    "slab": true
  }
}
```

## "description"

A Bedrock-style description block used by Obsidian for metadata.

Required.

* `identifier`: the full resource location of the block.
* `register_to_creative_menu`: whether the block should appear in the creative menu.

## "block_type"

Defines which block implementation to use.

Optional. Default: `"block"`.

See the available block types in the [Block Types](./BlockTypes.md) page.

## "information"

The main information section for the block. This includes naming and settings references.

Required.

Common fields:

* `name`: a [NameInformation] object (same structure as other text components).
* `block_set_type`: resource location used for doors/trapdoors (see `BlockSetType`).
* `wood_type`: resource location used for wood-related blocks.
* `parent_block`: optional block to copy base block settings from.
* `block_properties`: either a block settings object or a reference to a registered block settings entry.
* `item_properties`: either an item settings object or a reference to a registered item settings entry.

## "block_properties"

Block settings such as hardness, resistance, and sound. These can be defined inline or referenced from registries.

The entry is resolved by the `BlockSettings` registry in code.

## "item_properties"

Item settings for the block item (stack size, durability, rarity, etc.). This also supports inline definitions or references.

The entry is resolved by the `ItemSettings` registry in code.

## "drop_information"

Optional drop information for blocks, such as experience (the full drop structure is handled by Obsidian internals).

## "additional_information"

Flags to generate extra block variants (stairs, slabs, fences, etc.) and additional behaviors.

See `AdditionalBlockInformation` in the code for the full list of fields.

## Other optional sections

Blocks support several specialized sections used by block types and systems:

* `behaviour`: redstone and container behavior.
* `ore_information`: ore-generation metadata.
* `food_information`: edible block settings.
* `growable`: growable plant information.
* `painting_table_information`: painting table UI configuration.
* `can_plant_on`: list of block identifiers that support the block.
* `particle_type`: particle identifier for block interactions.

Refer to the source classes in `io.github.vampirestudios.obsidian.api.obsidian.block` for full details.