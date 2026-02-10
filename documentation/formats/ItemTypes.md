# Item Types

Item types control extra behaviors for Obsidian item definitions.

Item types are defined by the `type` field in an item definition.

## Available types

### `SHEARS`

Adds custom shear drop behavior. When `drops` are supplied, the item will drop items from entities when used like shears.

### `BUNDLE`

Creates a bundle-style item with bundle storage behavior.

### `CUSTOM_MENU`

Creates an item that opens a custom menu. Configure behavior with the `menu_config` field in the item definition.