# Creative Mode Tab Definition

Creative Mode tab definitions allow adding new tabs to the creative mode menu.

See ["group"](./Items.md#group) and ["creative_menu_stacks"](./Items.md#creative_menu_stacks) in the Item definitions for an example of where this is used.

Named item groups go in the `item_groups` directory in the content pack part of the addon.

E.g.
```
content/examplepack/item_groups/bedrock_blocks.json
```

## Basic structure of the JSON file

```json
{
  "icon": "minecraft:bedrock",
}
```

## "icon"

Defines the item to be used as the icon for the tab.

Required.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.