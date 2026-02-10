# Item Definitions

Items define things that can be in your inventory, and in your hands.

Item definitions go in the `item` directory in the thing pack.

E.g.
```
/things/examplepack/item/cheese_stick.json
```

## Obsidian format (current)

Modern Obsidian item definitions use the `information`, `components`, and `type` fields shown below.

```json
{
  "type": "SHEARS",
  "information": {
    "name": {
        "translations": {
            "en_us": "Cheese Shears"
        }
    },
    "item_properties": "examplepack:cheese_item"
  },
  "components": {
    "minecraft:max_stack_size": 1,
    "minecraft:rarity": "rare"
  },
  "lore": [
    "Sharp!",
    "<gray>Very sharp!"
  ],
  "events": {
    "on_use": [
      { "command": "say hi" }
    ]
  }
}
```

### "type"

Defines the extra behavior for the item (bundle, custom menu, custom shears, etc.).

See the available item types in the [Item Types](./ItemTypes.md) page.

### "information"

Defines naming and item settings.

* `name`: a [NameInformation] object for display text and translations.
* `item_properties`: either a settings object or a reference to a registered item settings entry.
* `item_type`: optional string used for item categorization (used by render logic).

### "components"

Data component patch to apply to the item. This uses the vanilla data component format.

### "lore"

Defines lore entries displayed in tooltips. Each entry can be a string or a text component object.

### "drops"

For `SHEARS` items, maps entity identifiers to dropped item identifiers when used.

### "events"

Defines event action lists keyed by event name.

## Legacy format (1.19)

The legacy format is still documented below for older packs or legacy loaders.

## Basic structure of the JSON file

```json
{
  "parent": "minecraft:string",
  "type": "plain",
  "max_stack_size": 64,
  "max_damage": 50,
  "food": {
    "saturation": 5
  },
  "group": "decorations",
  "creative_menu_stacks": [
    {
      "nbt": {}
    }
  ],
  "attribute_modifiers": [
    {
      "nbt": {}
    }
  ],
  "color_handler": "foliage",
  "lore": [
    "Hello",
    {"text": "Hi", "italic": true, "color": "gray" }
  ]
}
```

## "parent"

Defines another item to copy properties from.

Optional. Default: no parent.

Must be a resource location string like `"string"`, or `"minecraft:stick"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "type"

Defines the type of item to construct. Each type has additional properties.

Optional. Default: plain type without additional properties.

Must be a resource location string like `"block"`, or `"minecraft:sword"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

See the available item types in the [Item Types](./ItemTypes.md) page.

## "max_stack_size"

Defines how much the item stacks.

Optional. Default: 0.

The default means no amplification, and will do the standard effect.

Must be a positive integer between 1 and 127. Values above 64 are not well-defined and may fail.

## "max_damage"

Defines how many uses (without Unbreaking) the item has before breaking.

Optional. Default: 0 (no durability bar), except for item types like tools and armor.

Must be a positive integer or zero. Zero means the item will not be damageable.


## "group"

Defines which creative tab to have the item in.

Cannot be used at the same time as `"creative_menu_stacks"`.

Optional. Default: not shown in the creative menu.

Must be a string with the name of a creative menu tab.

## "creative_menu_stacks"

Defines which stacks of this item will be added to the creative menu. Each stack can be added to one or more tabs.

Cannot be used at the same time as `"group"`.

Optional. Default: not shown in the creative menu.

Must be a json array (`[]`) containing json objects (`{}`).

Each json object corresponds to the combination of a tab list and information about the stack:

```json
    {
      "tabs": [ "tools" ],
      "nbt": { "tmp": 0 }
    }
```

The `"tabs"` key is required and must be a json array (`[]`) containing strings.

The rest of the object are values defining the item stack.

For details on the definition of item stacks in json, see the [ItemStack Definitions](./ItemStack.md) page.

## "food"

Defines the item to be edible.

Optional. Default: not food.

For details on the values used to define food, see the [Food Definitions](./Food.md) page.

## "attribute_modifiers"

Defines the attribute changes that are applied when this item is equipped. E.g.: +20% speed

Optional. Default: no attribute modifiers.

Must be a json array (`[]`) containing json objects (`{}`). Each object must describe one attribute modifier.

The syntax for attribute modifiers is described in the [Attribute Modifiers](./AttributeModifiers.md) page.

## "color_handler"

Defines a color handler for the stack. Color handler provides tint values based on context.

Optional. Default: no tinting.

Must be a resource location string like `"foliage"`, or `"minecraft:tall_grass"`. Like on recipe jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

By default, only 2 color handlers are defined: `"foliage"` and `"tall_grass"`. More can be defined by mod code, and in the future it will be possible to define them via scripting.

## "lore"

Defines a list of lines of lore text to show in the tooltip box.

Optional. Default: no lore.

Must be a json array (`[]`) containing strings and json-formatted text components, as they would be used in commands such as `/tellraw`.