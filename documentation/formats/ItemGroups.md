# Creative Tabs

The categories down the side of the creative inventory.

Obsidian reads two formats from two directories; both register a tab named after the file.

| Directory | Format |
| --- | --- |
| `creative_tab` | [Current format](#current-format) — items listed as a holder set, so tags work. |
| `item_group` | [Legacy format](#legacy-format) — separate item/block lists with op-only and feature-flag variants. |

```
obsidian_addons/ExamplePack/content/examplepack/creative_tab/cheese.json
```

That file registers `examplepack:cheese`. Its title comes from the translation key
`itemGroup.examplepack.cheese`, which your pack's language file must provide.

## Current format

```json
{
  "name": "Cheese",
  "icon": "examplepack:cheese_block",
  "items": [
    "examplepack:cheese_block",
    "examplepack:cheese_stick"
  ],
  "no_scroll_bar": false
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `name` | yes | Display text — see [Names](./Names.md). Required by the parser, but the tab's visible title comes from the translation key above. |
| `icon` | yes | Item shown on the tab. Resource location; `minecraft` is implied when the namespace is omitted. |
| `items` | no | The tab's contents, as a vanilla holder set: a list of item ids, or `"#namespace:tag"` for a tag. Defaults to empty. |
| `texture` | no | Background texture. |
| `no_scroll_bar` | no | Hides the scroll bar. Default `false`. |

A file that fails to decode logs the reason and registers nothing, so an unknown item id in `items`
costs you the whole tab.

## Legacy format

Files in `item_group` use separate lists, which is what makes op-only and feature-gated entries
possible:

```json
{
  "name": "Cheese",
  "icon": "examplepack:cheese_block",
  "items": ["examplepack:cheese_stick"],
  "blocks": ["examplepack:cheese_block"],
  "opItems": ["examplepack:debug_wand"]
}
```

| Field | Meaning |
| --- | --- |
| `name`, `icon` | As above. |
| `items`, `blocks` | Ids added to the tab. |
| `opItems`, `opBlocks` | Added only for players with operator permissions. |
| `tags` | `{"item": "<tag id>"}` and/or `{"block": "<tag id>"}`. |
| `featureSetItems`, `featureSetBlocks` | Map of feature flag to id; only `vanilla` is recognised. |

## Putting an item in a tab


Items and blocks choose their tab themselves, through `item_group` in their item settings:

```json
{
  "information": {
    "item_properties": { "item_group": "examplepack:cheese" }
  }
}
```

Listing the item on the tab and setting `item_group` on the item are two separate additions, so doing
both can show the item twice. Pick one — `item_group` on the item scales better, since adding an item
does not mean editing the tab as well.

## See also

* [Item Settings](./ItemSettings.md) — `item_group`, the other half of this.
* [Names](./Names.md) — the `name` field.
