# Items

Everything that can sit in an inventory or be held in a hand.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/cheese_stick.json
```

**The file name is the id** — that file registers `examplepack:cheese_stick`. Nothing inside the file
sets it. Subdirectories of `item/` are not scanned.

## Example

```json
{
  "type": "SHEARS",
  "information": {
    "name": "Cheese Shears",
    "item_properties": "examplepack:cheese_tool"
  },
  "components": {
    "minecraft:max_stack_size": 1,
    "minecraft:rarity": "rare"
  },
  "lore": ["Sharp!", "<gray>Very sharp!"],
  "drops": { "minecraft:sheep": "examplepack:cheese_wool" },
  "events": {
    "on_use": [{ "action": "execute_command", "command": "say hi" }]
  }
}
```

## Fields

| Field | Required | Meaning |
| --- | --- | --- |
| `information` | yes | Name and settings. See [below](#information). |
| `type` | no | A special item behaviour — see [Item Types](./ItemTypes.md). |
| `template` | no | Inherit from an entry in `item/template`. See [Templates](#templates). |
| `components` | no | Vanilla data components. |
| `damageable` | no | Default `true`. `false` ignores `max_uses` from the settings. |
| `rendering` | no | Per-state models. See [Models](#models). |
| `lore` | no | Tooltip lines. |
| `drops` | no | Entity id → item id, for `SHEARS`. |
| `events` | no | Behaviour. See [Events](../Events.md). |
| `menu_config` | no | Menu layout and crate loot pool, for `CUSTOM_MENU`. See [Menu Config](./CustomMenu.md). |
| `use_actions` | no | Use animation and timing, plus the `right_click_actions` shorthand. See [below](#use_actions). |

## "information"

```json
{
  "information": {
    "name": "Cheese Shears",
    "item_properties": "examplepack:cheese_tool"
  }
}
```

| Field | Meaning |
| --- | --- |
| `name` | Display text. A plain string is usually all you need — see [Names](./Names.md). |
| `item_properties` | The item's [settings](./ItemSettings.md) — an inline object, or the id of an entry in `item/property`. |
| `item_type` | Optional categorisation string, used by render logic. |

## "components"

A vanilla data component patch — the same keys `/give` accepts:

```json
{
  "components": {
    "minecraft:max_stack_size": 1,
    "minecraft:food": { "nutrition": 4, "saturation": 2.5 },
    "minecraft:attribute_modifiers": [
      { "type": "minecraft:attack_damage", "id": "examplepack:cheese_damage",
        "amount": 3, "operation": "add_value", "slot": "mainhand" }
    ]
  }
}
```

Components are applied **after** the item settings, so a component wins over the equivalent settings
field. Where both exist — stack size, rarity, food — the component is the more capable of the two and
the better choice for new packs.

## "lore"

A list of tooltip lines. Each entry is a [name](./Names.md) — a plain string, or an object with `text`:

```json
{
  "lore": [
    "Sharp!",
    { "text": "<gray><italic>Very sharp!" },
    { "text": "item.examplepack.cheese_sword.lore", "type": "translatable" }
  ]
}
```

Colour and styling are [inline tags](./Names.md#formatting-tags) in the text, not separate fields —
there is no `color`, `italic` or `formatting` key. A `"type": "translatable"` line takes its text from a
language file and is **not** tag-parsed, so it cannot be coloured this way.

## "use_actions"

Use animation and timing, plus `right_click_actions` — a one-action shorthand from before the
[events](../Events.md) system existed.

| Field | Meaning |
| --- | --- |
| `use_animation` | `none`, `eat`, `drink`, `block`, `bow`, `spear`, `crossbow`, `spyglass`, `toot_horn`, `brush`. |
| `use_duration` | Ticks the use takes. |
| `right_click_actions` | `open_gui`, `run_command` or `open_url`. |
| `gui_type`, `gui_size`, `gui_title` | For `open_gui`. |
| `command` | For `run_command`. |
| `url` | For `open_url`. |

The shorthand runs as an ordinary event action — `open_gui` *is* the [`open_gui`](../Events.md#open_gui)
action, `run_command` is `execute_command` — so both paths behave identically. `open_url` is the
exception: opening a link happens on the client, so it has no event action and stays here.

New packs should prefer `events.on_use`, which takes a list rather than a single action and supports
[conditions](../Events.md#conditions). The shorthand is kept for existing packs and is not deprecated.

## Templates

`item/template` holds entries with the same shape as an item. Referencing one fills in what the item
leaves out:

```json
{
  "template": "examplepack:magic_tool",
  "information": { "name": "Cheese Wand" }
}
```

How each field merges:

| Field | Merge |
| --- | --- |
| `type`, `use_actions`, `components`, `menu_config`, `lore` | Taken from the template only if the item omits them **entirely**. |
| `drops`, `events` | Merged per key; the item's entry wins. |
| `information`, `rendering` | Merged field by field. |
| `name` | **Never inherited.** Every item keeps its own identity. |

Because `components` is all-or-nothing, an item that sets one component does not inherit the template's
others.

## Models

Omit `rendering` entirely and the item uses the model at `item/<its id>`, which is what most packs want.
Use it when a state needs its own model:

```json
{
  "rendering": {
    "item_model": { "textures": { "layer0": "examplepack:item/cheese_stick" } },
    "broken_model": "examplepack:item/cheese_stick_broken"
  }
}
```

Every field takes either a model id, or an inline object with `model`/`parent` and `textures` that
Obsidian generates a model from. An inline object with only `textures` gets
`minecraft:item/generated` as its parent.

| Field | Applies to |
| --- | --- |
| `item_model` | The item normally. Defaults to `item/<id>`. |
| `blocking_model` | Blocking with a shield. |
| `pulling_models`, `charged_model`, `arrow_model`, `firework_model` | Bows and crossbows. |
| `charging_models`, `cooldown_model` | Charging and cooldown states. |
| `use_models`, `throwing_model`, `cast_model` | While being used, thrown or cast. |
| `damaged_models`, `broken_model` | Damage thresholds and the broken state. |
| `binary_selects` | Custom two-state model switches. |
| `equipment` | What the item looks like *worn* rather than held — see [Equipment layers](#equipment-layers). |
| `channels` | The model's colour channels, in tint-layer order — see [Colours](#colours) below. |
| `palette` | Which palette the item starts out painted with. |

Fields for a state the item never enters are simply unused.

## Equipment layers

The model above is the item in a hand or an inventory slot. What the game draws on the *wearer* is a
separate texture, listed under `rendering.equipment`:

```json
{
  "rendering": {
    "equipment": {
      "elytra": "examplepack:cheese_wings"
    }
  }
}
```

Obsidian writes the equipment file the client reads from this — no `equipment/` json of your own.

| Layer | Drawn on |
| --- | --- |
| `elytra` | The wearer's back. An alias for `wings`, which also works. |
| `humanoid`, `humanoid_leggings` | Armor on a player or mob. |
| `wolf_body`, `horse_body`, `llama_body`, `happy_ghast_body` | Animal armor. |
| `*_saddle` | Saddles — `pig_saddle`, `camel_saddle`, `horse_saddle`, and the rest. |

A layer is either the texture on its own, as above, or an object when it needs more:

```json
{
  "rendering": {
    "equipment": {
      "humanoid": { "texture": "examplepack:cheese_mail", "dyeable_color": 16770519 }
    }
  }
}
```

| Field | Meaning |
| --- | --- |
| `texture` | The texture id. |
| `dyeable_color` | Colour the layer takes while undyed. Leave it out and the layer is not dyeable. |
| `use_player_texture` | Draw the wearer's own skin on this layer instead of the texture. |

**Where the png goes.** The game looks for a layer's texture in that layer's own directory:

```
assets/examplepack/textures/entity/equipment/wings/cheese_wings.png
```

So `"elytra": "examplepack:cheese_wings"` resolves to the path above. Writing the whole path instead
of the bare name works — the known prefixes are trimmed back off — but a file that isn't in the layer's
directory can't be found, and Obsidian says so in the log.

## Colours

An item can be recoloured per region at runtime instead of shipping a texture per colour. One
registered `examplepack:longsword` covers every colour scheme — no `red_longsword`, no
`blue_longsword` — because the colours live in a component on the stack, not in the item's identity.

Two pieces:

**1. Name the item's tint layers**, in order, under `rendering.channels`:

```json
{
  "rendering": {
    "item_model": {
      "parent": "minecraft:item/handheld",
      "textures": {
        "layer0": "examplepack:item/longsword_blade",
        "layer1": "examplepack:item/longsword_grip",
        "layer2": "examplepack:item/longsword_gem"
      }
    },
    "channels": ["primary", "secondary", "accent"]
  }
}
```

Layered models give each layer a tint index automatically — `layer0` is tint 0, up to `layer4` — so
those three names land on those three layers in order. Draw the layers as greyscale masks: the tint
multiplies, so a white region takes the colour exactly while shading in the texture survives.

**2. Write [palettes](./Palettes.md) using those same names**, one file each in `palettes/`:

```json
{
  "name": { "text": "Royal" },
  "colors": {
    "primary": "#2B3A8F",
    "secondary": "#5A3A22",
    "accent": "#D8B24A"
  }
}
```

Every palette declaring those names can now paint the sword, and the same palettes paint anything
else that uses them. Matching is by name — that's the whole mechanism.

### When you want more than that

`channels` also accepts a palette id, so several items can share one channel list — a palette's write
order is its layer order:

```json
{ "rendering": { "channels": "examplepack:basic" } }
```

…or an inline palette object, if this one item needs keys the array form cannot carry: a
`default_palette`, a whitelist, or the long-form `channels` list that lets a region's name differ
from the colour it reads (`{ "name": "blade", "source": "metal" }`). See
[Palettes](./Palettes.md) for those.

`rendering.palette` sets what the item starts out painted with, overriding any `default_palette`.

A model written by hand with `elements` works too — put the `tintindex` on the faces yourself, and
the channel at that position drives it. That is also how you go past five colours: layered icons stop
at `layer4`, but a hand-written model can carry as many tint layers as you draw.

### What the item gets

Registration adds two components automatically:

| Component | Holds |
| --- | --- |
| `obsidian:channels` | The channel palette's id, so anything holding the stack knows what it supports. |
| `obsidian:palette` | The current palette plus any per-channel overrides. |

Set `obsidian:palette` yourself to start an item somewhere other than the default, or to pin
individual channels:

```json
{
  "components": {
    "obsidian:palette": {
      "palette": "examplepack:cursed",
      "overrides": { "gem": "#6EE7A8" }
    }
  }
}
```

Overrides take the same colour forms palettes do — `"#RRGGBB"`, `"#AARRGGBB"`, `"0x…"`, an int, or
an `[r, g, b, a]` array — and win over the palette. A channel marked `locked` ignores them.

### Repainting in-game

Any item becomes a painting tool with an `obsidian:palette_applicator` component:

```json
{
  "components": {
    "obsidian:palette_applicator": {
      "paint": { "palette": "examplepack:royal" },
      "consumes": false
    }
  }
}
```

Right-click it onto the item to paint in your inventory, the way items go into a bundle — or hold one
in each hand and use. Add `"channels": ["gem"]` to make a tool that only touches one region, or
`"uses": 8` to make it wear out. The full field list is in
[Palettes](./Palettes.md#applicator-items--chroma-sets).

For testing without crafting anything, `/obsidian palette set examplepack:royal` repaints whatever is
in your main hand.

### Versus `dyeable`

The `dyeable` item setting is the vanilla single-colour path: one `minecraft:dyed_color` value, one
tint layer, dyes in a crafting grid. Channels are the multi-region path and do not need the item to be
dyeable. Use one or the other; an item that sets both gets the dye tint first and the channel tints
after it, which shifts every channel's tint index by one.

## See also

* [Item Settings](./ItemSettings.md) — everything `item_properties` accepts.
* [Palettes](./Palettes.md) — the colour system in full.
* [Item Types](./ItemTypes.md) — `SHEARS`, `BUNDLE`, `CUSTOM_MENU`.
* [Menu Config](./CustomMenu.md) — menus and crate loot pools.
* [Events](../Events.md) — the full event and action reference.
* [Creative Tabs](./ItemGroups.md) — where the item shows up.

## Fields removed since the 1.19 format

Older packs used a flat file with `parent`, `max_stack_size`, `food` and friends at the top level. Those
keys are no longer read, and a file still using them registers an item with **default settings rather
than failing** — which makes the breakage quiet. Their replacements:

| Old field | Now |
| --- | --- |
| `max_stack_size`, `max_damage` | `information.item_properties` → `max_stack_size`, `max_uses`. |
| `parent` | `item_properties` → `parent`, or `template` for whole-item inheritance. |
| `group` | `item_properties` → `item_group`. |
| `creative_menu_stacks` | Declare the entries on the tab — see [Creative Tabs](./ItemGroups.md). |
| `food` | The `minecraft:food` component, or a definition in `item/food` — see [Food](./Food.md). |
| `attribute_modifiers` | The `minecraft:attribute_modifiers` component. |
| `type: "block"`, `"sword"`, … | An [item type](./ItemTypes.md), or a dedicated directory such as `item/tool`. |
| `color_handler` | No replacement yet. |
