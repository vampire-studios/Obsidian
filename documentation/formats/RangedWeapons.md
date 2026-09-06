# Ranged Weapons

Bows, shortbows, crossbows, heavy crossbows and tridents.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/weapon/ranged/oak_shortbow.json
```

The file name is the item id. Everything an ordinary [item](./Items.md) accepts works here too —
`components`, `lore`, `rendering`, `events` — plus the fields below.

## Example

```json
{
  "weapon_type": "shortbow",
  "information": {
    "name": "Oak Shortbow",
    "item_properties": "examplepack:basic_item"
  },
  "rendering": {
    "item_model": "examplepack:item/oak_shortbow",
    "pulling_models": [
      "examplepack:item/oak_shortbow_pulling_0",
      "examplepack:item/oak_shortbow_pulling_1",
      "examplepack:item/oak_shortbow_pulling_2"
    ]
  }
}
```

## Fields

| Field | Meaning |
| --- | --- |
| `weapon_type` | Required: `bow`, `shortbow`, `crossbow`, `heavy_crossbow` or `trident`. |
| `draw_time` | Bows: ticks to a full-power shot. |
| `charge_time` | Crossbows: ticks to wind a bolt, before Quick Charge. |
| `projectile_velocity` | Multiplies the speed the projectile leaves at, and with it its damage. |
| `projectile_range` | How far the weapon can be aimed at a target, in blocks. |
| `ammo` | Ids of the [projectiles](./Projectiles.md) this weapon fires. See [Ammunition](#ammunition). |

Each type is the same weapon with different defaults:

| Type | Draw / charge | Velocity |
| --- | --- | --- |
| `bow` | 20 ticks | ×1.0 |
| `shortbow` | 10 ticks | ×1.0 |
| `crossbow` | 25 ticks | ×1.0 |
| `heavy_crossbow` | 40 ticks | ×1.25 |

So a shortbow is a bow that reaches full power in half the time, and a heavy crossbow is a crossbow
that takes longer to wind and hits harder for it. Override any of them per item — `"draw_time": 30`
makes a slow siege bow, `"charge_time": 15` a rapid-fire crossbow.

Power still scales with how long the weapon was actually held, so a half-drawn shortbow is exactly
as weak as a half-drawn bow. Only the time to reach full power changes.

## Ammunition

Without `ammo`, a weapon behaves like vanilla: a bow takes anything in the `minecraft:arrows` tag, a
crossbow takes arrows and fireworks.

Naming `ammo` makes the weapon **exclusive** — it fires only what it names, and vanilla arrows no longer
load into it:

```json
{
  "weapon_type": "heavy_crossbow",
  "ammo": ["examplepack:cheese_bolt"]
}
```

The entries are ids of arrow-shaped [projectiles](./Projectiles.md#arrow). The gate works both ways: a
custom arrow is not in the `minecraft:arrows` tag either, so a vanilla bow cannot fire it. Weapon and
ammunition are designed as a pair, which is what makes a crossbow's bolts distinct from a bow's arrows
rather than a reskin of them.

Several ids can be listed, and the weapon fires whichever the player is carrying.

## Models

Bows and shortbows use `item_model` plus `pulling_models`, and the draw animation is paced to the
weapon's own `draw_time` — a 10-tick shortbow runs through its pulling stages twice as fast as a
bow. Give as many pulling models as you like; the thresholds are spread across them.

Crossbows and heavy crossbows use `charged_model`, and optionally `arrow_model` and `firework_model`
when a loaded bolt and a loaded rocket should look different. Their three pulling stages are paced by
the game against `charge_time`, so nothing extra is needed there. See
[Items](./Items.md#models) for the full model field list.

## Colours

Ranged weapons take `rendering.channels` and `rendering.palette` like any other item, so one bow
model can be recoloured per palette — see [Palettes](./Palettes.md).

## See also

* [Items](./Items.md) — everything else in the file
* [Item Settings](./ItemSettings.md) — `item_properties`
