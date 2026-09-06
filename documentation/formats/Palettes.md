# Palettes

A named, ordered set of colours.

```json
{
  "colors": {
    "primary": "#A1744B",
    "secondary": "#4A4F67",
    "accent": "#EBE9DB",
    "detail": "#AD6AB7"
  }
}
```

That is the whole format. Everything else on this page is optional.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/palettes/royal.json
```

The file name is the palette id — `examplepack:royal`.

## Two roles

A palette is used in one of two ways, and the file looks the same either way:

**As an asset's channels.** The write order is tint-layer order, so `primary` colours the model's
`tintindex` 0, `secondary` colours 1, and so on. The colours are the fallback for when nothing has
repainted the asset. An item points at one with `rendering.channels` — see
[Items](./Items.md#colours).

**As paint.** A scheme applied to an asset, matched to its channels by name. `primary` in the
palette colours the channel named `primary`, whatever asset it belongs to. That is what lets one
`examplepack:royal` paint a sword, a chestplate and a chair without any of them knowing about each
other — and what lets a single registered `examplepack:longsword` exist in every colour scheme
instead of registering `red_longsword`, `blue_longsword` and so on.

So the mechanism in one line: **name your model's tint layers, then write palettes using those
names.**

## Colours

| Form | Example |
| --- | --- |
| Hex RGB | `"#2B3A8F"` |
| Hex ARGB | `"#802B3A8F"` |
| `0x` prefixed | `"0xFF2B3A8F"` |
| Component array | `[43, 58, 143]`, or `[43, 58, 143, 128]` with alpha |
| Channel reference | `"$primary"` — the same colour as `primary` |
| Derived | `{ "from": "primary", "lighten": 0.35 }` |

Derived colours let a palette define two or three real colours and pull the rest from them:

| Modifier | Effect |
| --- | --- |
| `from` | Channel to read. Required on a derived colour. |
| `mix` + `mix_amount` | Blends towards another channel; `mix_amount` `0`–`1`, default `0.5`. |
| `saturate` | Positive pushes away from grey, negative towards it. |
| `lighten` | Blends towards white, `0`–`1`. |
| `darken` | Blends towards black, `0`–`1`. |
| `alpha` | Multiplies the existing alpha, `0`–`1`. |

Modifiers apply in that order. A reference cycle (`a` derives from `b`, `b` from `a`) resolves to
white rather than hanging.

## Optional fields

| Field | Meaning |
| --- | --- |
| `colors` | Channel name → colour, in tint-layer order. The only field most palettes need. |
| `name` | Display name, for tooltips and pickers. Same shape as everywhere else in Obsidian. |
| `parent` | Another palette to inherit from. This palette's own entries win. |
| `default_palette` | When used as an asset's channels: what the asset starts out painted with. |
| `tags` | Free-form labels, so a whitelist can name a group rather than every palette. |
| `palettes`, `palette_tags` | When used as channels: which palettes may paint the asset. Empty allows any. |
| `hidden` | Keeps it out of pickers. It still resolves when referenced explicitly. |
| `channels` | The long form — see below. |

There is no limit on how many channels a palette declares, and a palette may declare more than any
one asset uses; unused channels are ignored rather than being an error.

### The long form

Use `channels` instead of `colors` when a region's name differs from the colour it reads, or when it
needs an explicit layer:

```json
{
  "channels": [
    { "name": "blade", "source": "metal", "default": "#B0C4DE" },
    { "name": "grip", "source": "secondary", "default": "#5A3A22", "tint_index": 1 }
  ]
}
```

| Field | Meaning |
| --- | --- |
| `name` | Channel name — what the asset calls the region. |
| `source` | Palette channel to read. Defaults to `name`. |
| `default` | Fallback colour. Any of the colour forms above. |
| `tint_index` | Model tint layer this drives. Defaults to the channel's position. |
| `locked` | Players cannot recolour this channel directly; it only moves when the palette changes. |

`colors` and `channels` can be combined — `colors` entries come first, and a `channels` entry with
the same name replaces it.

## Resolution order

For any channel, the colour is the first of:

1. a per-channel override stored on the asset (`obsidian:palette` → `overrides`)
2. the palette it is painted with, following its `parent` chain
3. the fallback on its own channel
4. white

## Applying palettes in-game

```
/obsidian palette set examplepack:royal
/obsidian palette channel blade "#8B0000"
/obsidian palette info
/obsidian palette clear
```

These act on the stack in your main hand and need permission level 2.

### Applicator items — "chroma sets"

Any item can paint other items: give it an `obsidian:palette_applicator` component. There is no
special item type, so an applicator can be an ordinary item, a tool, a food, whatever fits.

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

Two ways to apply one:

- **In the inventory** — pick the applicator up and right-click it onto the item to paint, the way
  items go into a bundle. It works in either direction, so carrying the item and right-clicking the
  applicator in its slot does the same thing.
- **In hand** — hold the applicator in one hand and the item to paint in the other, then use.

| Field | Meaning |
| --- | --- |
| `paint` | What it applies: `{ "palette": "…" }`, `{ "overrides": { "detail": "#FF0000" } }`, or both. |
| `channels` | Restricts it to named channels. Empty (default) repaints everything. |
| `uses` | How many applications before it breaks. Gives the item durability; unset means unlimited. |
| `damage` | Durability spent per application. Defaults to 1 when `uses` is set. |
| `consumes` | Spends one *item* per application instead of durability. |

An applicator is reusable and indestructible unless it says otherwise. `"uses": 8` is the short way
to get a wearing-out one — no `durability` in the item settings needed.

A full repaint hands over the palette and clears leftover overrides. A `channels`-restricted
applicator writes only those channels as overrides, leaving the target's palette alone — that is how
a "detail brush" recolours the trim of an item without repainting the rest of it. Channels marked
`locked` are never written, and an applicator is refused if the target's channels do not `accept` its
palette.

An applicator needs no `channels` of its own; giving it some just means its own icon displays the
colours it carries.

## Palette references elsewhere

Anything in Obsidian that takes a colour string also accepts a palette reference, so particles, GUIs
and generated assets can pull from the same palettes as items:

```
palette:examplepack:royal/accent
```

## See also

* [Items](./Items.md#colours) — wiring channels to an item's model

Palettes colour items only. Blocks have their own single-colour path — see `dyable` in
[Blocks](./Blocks.md).
