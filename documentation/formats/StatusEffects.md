# Status Effects

A potion effect: the icon in the inventory, the colour of the swirl, and the attribute changes it makes
while it is active. Speed, Strength and Poison are vanilla's.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/status_effect/cheesed.json
```

The file name is the effect id. Registering an effect creates no items — [potions](./Potions.md),
[food](./Food.md) and `apply_effect` [actions](./EffectInstances.md) reference it.

## Example

```json
{
  "name": "Cheesed",
  "status_effect_type": "beneficial",
  "color": "#FFD84D",
  "attributes": [
    {
      "attribute": "minecraft:movement_speed",
      "name": "examplepack:cheesed_speed",
      "amount": 0.1,
      "operation": "add_multiplied_total"
    }
  ]
}
```

## Fields

| Field | Required | Meaning |
| --- | --- | --- |
| `name` | yes | Display text. See [Names](./Names.md). |
| `status_effect_type` | yes | `beneficial`, `harmful` or `neutral`. |
| `color` | yes | Particle and inventory-bar colour. |
| `attributes` | no | Attribute modifiers applied for as long as the effect lasts. |

`status_effect_type` is matched in lowercase and has no fallback: any other value leaves the category
null, which fails the entry. `beneficial` shows a blue border in the inventory, `harmful` a red one.

## Colour

`color` is hex, with or without a leading `#` or `0x`:

```json
{ "color": "#FFD84D" }
{ "color": "0xFFD84D" }
{ "color": "FFD84D" }
```

An empty or blank string falls back to white. Anything that is not valid hex fails the entry — there is
no partial parse.

## Attributes

Each entry is an [attribute modifier](./AttributeModifiers.md) applied when the effect is applied and
removed when it ends.

| Field | Meaning |
| --- | --- |
| `attribute` | The attribute to change, e.g. `minecraft:movement_speed`. |
| `name` | Id for the modifier itself. Give each one a unique id within the pack. |
| `amount` | How much to change it by. Interpreted according to `operation`. |
| `operation` | `add_value`, `add_multiplied_base` or `add_multiplied_total`. |

The effect's amplifier does not scale these automatically — a modifier declared here is applied at its
declared `amount` regardless of the level the effect was applied at.

## See also

* [Potions](./Potions.md) — bottling an effect.
* [Effect Instances](./EffectInstances.md) — applying one, with a duration and amplifier.
* [Attribute Modifiers](./AttributeModifiers.md) — the modifier format in full.
* [Food](./Food.md) — effects applied by eating.
