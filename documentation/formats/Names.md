# Names

The `name` field on items, blocks, fluids, entities and creative tabs.

## Just write a string

```json
{ "name": "Cheese Block" }
```

**This is almost always the whole answer.** It sets the display text *and* registers it as the `en_us`
translation, and colour and formatting go inside the string as [tags](#formatting-tags).

There is no `color` field, no `italic` field, and `"type": "literal"` is the default — so a name with one
language and no translation key has nothing to put in an object. Write the string.

```json
{ "name": "<gold>Cheese Block" }
```

## The object form

Reach for it in exactly two cases.

**Several languages:**

```json
{
  "name": {
    "translations": {
      "en_us": "Cheese Block",
      "de_de": "Käseblock"
    }
  }
}
```

**A translation key**, where the text lives in a language file rather than in the definition:

```json
{ "name": { "text": "block.examplepack.cheese_block", "type": "translatable" } }
```

A translatable name is **not** tag-parsed, so it cannot be coloured inline — the language file's value is
used as-is.

## Fields

| Field | Meaning |
| --- | --- |
| `text` | Literal display text, or a translation key when `type` is `translatable`. Set automatically by the string form. |
| `translations` | Either a string (treated as `en_us`) or a language-code map. |
| `type` | `literal` (the default), `translatable`, or `space`. |

`{ "translations": "Cheese Block" }` — a bare string rather than a map — is shorthand for
`{"en_us": "…"}`, but it is longer than writing the string and does the same thing minus setting `text`.

## Formatting tags

Literal text is parsed for inline tags, so colour and formatting need no component object and no extra
field:

```json
{ "name": "<gold>Cheese <bold>Block</bold></gold>" }
```

| Tag | Effect |
| --- | --- |
| `<red>`, `<gold>`, `<aqua>`, … | The sixteen vanilla colours. |
| `<color #FFD84D>` | Any hex colour. **Note the space** — `<#FFD84D>` is not the syntax. |
| `<bold>`, `<italic>`, `<underline>`, `<strikethrough>`, `<obfuscated>` | Styles. |

Tags close with `</tag>`, or run to the end of the string if left open — `"<gold>Cheese Block"` is
entirely gold and needs no closing tag.

### Gradients

```json
{ "name": "<gradient #ff0000 #ffd84d #00ff00>Cheese Block" }
```

| Tag | Short form | Effect |
| --- | --- | --- |
| `<gradient (type:[type]) [colour] [colour] …>` | `<gr …>` | Blends smoothly between any number of colours. |
| `<hard_gradient [colour] [colour] …>` | `<hgr …>` | Steps between the colours without mixing them. |

The optional `type:` on `gradient` chooses how the blend is computed — `type:oklab` (the default),
`type:hvs`, or `type:hard`:

```json
{ "name": "<gradient type:hvs #ff0000 #0000ff>Rift" }
```

### Rainbow

```json
{ "name": "<rainbow>Cheese Block" }
{ "name": "<rainbow 0.5 0.8 0.2>Cheese Block" }
{ "name": "<rainbow f:0.5 s:0.8 o:0.2>Cheese Block" }
```

| Tag | Short form | Arguments |
| --- | --- | --- |
| `<rainbow [frequency] [saturation] [offset]>` | `<rb …>` | All optional, each `0`–`1`. |

Arguments can be positional or named with `f:`, `s:` and `o:`. Bare `<rainbow>` is valid.

This is the parser's whole job, so **a `color` field does nothing**. Colour lives in the text.

The same parsing applies to [lore](./Items.md#lore) lines, which is why `"<gray>Very sharp!"` renders
grey, and to a [container's](./Blocks.md#containers) title and a [world event's](./WorldEvents.md) bar.

## Gotcha

`text` and `translations` are independent. The object form with only `text` sets no translation, so a
`lang` file is still needed for other languages. The string form fills both, which is the reason to
prefer it.

## See also

* [Items](./Items.md), [Blocks](./Blocks.md), [Creative Tabs](./ItemGroups.md) — the formats that take a name.
