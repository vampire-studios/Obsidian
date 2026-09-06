# Projectiles

A projectile item: how it flies, and what it does when it lands.

Two shapes. A **thrown** projectile is used straight from the hand like a snowball — grenades, shurikens,
boomerangs, web shots. An **arrow** is ammunition, fired only by a bow or crossbow that names it, and it
sticks where it lands — arrows, and bolts for a crossbow.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/projectile/cheese_bomb.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Cheese Bomb",
    "item_properties": { "max_count": 16 }
  },
  "damage": 4.0,
  "cooldown": 20,
  "flight": {
    "speed": 1.5,
    "gravity": 0.05,
    "lifetime": 100
  },
  "trail": {
    "particle": "examplepack:cheese_crumb",
    "count": 2,
    "interval": 1
  },
  "events": {
    "projectile_hit_entity": [
      { "action": "apply_effect", "effect": "examplepack:cheesed", "duration": 200, "amplifier": 0 }
    ],
    "projectile_hit_block": [
      { "action": "play_sound_at", "sound": "minecraft:entity.generic.explode" },
      { "action": "spawn_particles_at", "particle": "examplepack:cheese_crumb", "count": 40 }
    ]
  }
}
```

## Fields

A projectile is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore` and the
usual item `events` all work as they do there. On top of that:

| Field | Default | Meaning |
| --- | --- | --- |
| `shape` | `thrown` | `thrown` or `arrow`. `bolt` is accepted as a synonym for `arrow`. See [Shapes](#shapes). |
| `damage` | `0.0` | Damage dealt to the entity struck. `0` throws something harmless, which is a normal thing to want. |
| `pierce` | `0` | How many entities it passes through before stopping. `0` stops at the first. |
| `bounce` | `false` | Whether it survives hitting a block, reflecting off the face instead of stopping. |
| `bounce_damping` | `0.5` | Fraction of speed kept through a bounce. Ignored unless `bounce` is set. |
| `recoverable` | `false` | Whether the item drops where the projectile came to rest, to be picked back up. |
| `consume_count` | `1` | How many the throw takes from the stack. `0` never consumes — a reusable wand. |
| `cooldown` | `0` | Ticks before the thrower can throw again. |
| `flight` | — | The flight path. See [flight](#flight). |
| `trail` | — | Particles left in flight. See [trail](#trail). |

### flight

| Field | Default | Meaning |
| --- | --- | --- |
| `speed` | `1.5` | Launch speed in blocks per tick. Vanilla's snowball is `1.5`. |
| `gravity` | `0.03` | Blocks per tick lost each tick. `0` flies perfectly flat. |
| `drag` | `0.99` | Fraction of speed kept each tick. `1.0` never slows down. |
| `inaccuracy` | `1.0` | How far the throw scatters from where the player aimed. `0` is exact. |
| `lifetime` | `200` | Ticks before an unspent projectile gives up and fires `projectile_expired`. |
| `stops_in_water` | `false` | Whether water stops it rather than letting it pass through. |

### trail

| Field | Default | Meaning |
| --- | --- | --- |
| `particle` | — | The particle left behind. Vanilla's, or one of the pack's own — see [Particles](./Particles.md). |
| `count` | `1` | Particles per emission. |
| `interval` | `2` | Ticks between emissions. `1` emits every tick. |
| `spread` | `0.0` | Random spread applied to each particle, in blocks. |

## Shapes

### thrown

Used from the hand: right-click throws one. Renders as the item's sprite, using vanilla's thrown-item
renderer. `pierce`, `bounce`, `bounce_damping`, `consume_count` and `cooldown` apply only to this shape.

### arrow

Ammunition. It cannot be used from the hand at all — a bow or crossbow has to name it in its `ammo`.
In exchange it gets everything vanilla arrows have: sticking into blocks, being picked back up, crit
damage on a full draw, Power, Punch, Flame and Infinity, and the crossbow's loaded-projectile component.

`recoverable` decides whether it can be picked up again; `damage` sets its base damage before the draw
multiplier and enchantments. `pierce` is not read for this shape — vanilla drives arrow piercing from
the Piercing enchantment.

A bolt is just an arrow named by a crossbow rather than a bow. There is no separate shape for it.

## Firing an arrow

A ranged weapon names what it will fire:

```json
{
  "weapon_type": "heavy_crossbow",
  "information": { "name": "Cheese Arbalest" },
  "ammo": ["examplepack:cheese_bolt"]
}
```

The gate is **exclusive and two-way**:

* A weapon that names `ammo` fires only what it names. Vanilla arrows will not load into it.
* A custom arrow is not in the `minecraft:arrows` tag, so vanilla bows will not fire it either.

So ammunition and weapon are designed as a pair, which is what makes a bolt distinct from an arrow
rather than just a reskin. A weapon that names no `ammo` behaves exactly as before — arrows for a bow,
arrows and fireworks for a crossbow.

## The 3D model

An arrow renders as **its own item model**, turned to face the way it is flying — the same look a trident
has, from a model the pack already ships:

```
obsidian_addons/ExamplePack/assets/examplepack/models/item/cheese_bolt.json
```

The model is drawn in the `GROUND` display context, so the `display.ground` block in the model file is
what positions and scales it in flight. A flat two-layer sprite works and looks like a vanilla arrow;
a real 3D model looks like a bolt.

This is why arrows do not need an entity model or a layer registration — the item model is the model.
Thrown projectiles render as a flat camera-facing sprite instead, which is vanilla's behaviour for
thrown items.

## Events

Impact is expressed with the ordinary [event format](../Events.md) — the same actions blocks and items
use, so there is nothing new to learn:

| Event | Fires when |
| --- | --- |
| `projectile_hit_entity` | The projectile strikes an entity. The entity struck is the action target. |
| `projectile_hit_block` | The projectile strikes a block. The position and face reach positional actions. |
| `projectile_expired` | `lifetime` ran out, or water stopped it, without it hitting anything. |
| `on_shoot` | The projectile is thrown or fired. Fires on the thrower, before it exists. |

The first two are the **same event names a ranged weapon fires**, so an action list written for a bow
works unchanged on a thrown item.

A projectile thrown by something other than a player — a dispenser, a mob — fires its events with no
player attached, and player-scoped actions are skipped with a warning. Positional actions still work.

## How it is built

There are two entity types in total — one for each shape — shared by every projectile a pack declares.
The definition is looked up from the item the projectile carries rather than copied onto the entity, so
packs never register an entity of their own, and a projectile already in flight picks up a reloaded
definition instead of a stale copy.

## Recipes

`pierce`, `bounce` and `recoverable` compose: a `bounce` projectile with `recoverable` and a long
`lifetime` is a boomerang; `pierce` with high `speed` and no `gravity` is a railgun bolt; `consume_count:
0` with a `cooldown` is a wand that never runs out.

## See also

* [Items](./Items.md) — everything a projectile inherits.
* [Events](../Events.md) — the action list format for impacts.
* [Particles](./Particles.md) — trail and impact particles.
* [Ranged Weapons](./RangedWeapons.md) — bows and crossbows, and the `ammo` field.
* [Weapons](./Weapons.md) — melee, including the throwable knife.
