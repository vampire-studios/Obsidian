# Potions

A brewable potion: an entry in vanilla's potion registry carrying the effects it applies, each with
its own duration and strength.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/potion/cheesed.json
```

The file name is the potion id, unless the file sets `name` itself.

## Example

```json
{
  "effects": [
    {
      "effect": "examplepack:cheesed",
      "duration": 900,
      "amplifier": 0
    },
    {
      "effect": "minecraft:speed",
      "duration": 600,
      "amplifier": 1
    }
  ]
}
```

## Fields

| Field | Required | Meaning |
| --- | --- | --- |
| `name` | no | The registry id. Defaults to the file name. |
| `effects` | yes | Every effect the potion applies. |

### Effect entries

| Field | Default | Meaning |
| --- | --- | --- |
| `effect` | — | The status effect to apply — vanilla's, or one of the pack's own. Also accepted as `name`. |
| `duration` | `0` | Length in **ticks**: 20 per second, so `900` is 45 seconds. |
| `amplifier` | `0` | Strength, zero-based. `0` is level I, `1` is level II. |
| `ambient` | `false` | Beacon-style presentation: different colour, no countdown. |
| `show_particles` | `true` | Whether the effect emits its particles. |
| `show_icon` | `true` | Whether the effect shows its inventory icon. |

This is the same shape [food](./Food.md) and the `apply_effect` action use — see
[Effect Instances](./EffectInstances.md).

## Effects must exist first

`effect` is resolved against the effect registry while the potion is registered, so a
[status effect](./StatusEffects.md) the pack declares has to be loaded first. Obsidian loads
`status_effect` before `item/potion`, which covers entries in the same pack; across packs, the pack
declaring the effect has to load first.

An entry naming an effect that does not exist is **skipped with a warning** and the rest of the potion
still registers. A potion whose every effect is skipped registers as a plain bottle that does nothing,
and says so in the log.

## Brewing recipes

Obsidian does not read brewing recipes. Registering a potion makes it exist; getting to it in a brewing
stand needs a vanilla recipe from the pack's [`data` directory](../PackStructure.md#assets-and-data), or
a [fuel source](./FuelSources.md) if the goal was only to extend what the stand burns.

## See also

* [Status Effects](./StatusEffects.md) — declaring an effect of your own.
* [Effect Instances](./EffectInstances.md) — the same shape, used by food and events.
* [Food](./Food.md) — the other route to a consumable that applies effects.
