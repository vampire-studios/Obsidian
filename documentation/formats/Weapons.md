# Weapons

Melee weapons. The vanilla sword and mace, plus seven shapes Obsidian adds: spear, longsword, rapier,
dagger, knife, cleaver and scythe, each with its own combat behaviour.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/weapon/cheese_cleaver.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Cheese Cleaver",
    "item_properties": { "max_count": 1 }
  },
  "material": "examplepack:cheese",
  "weapon_type": "cleaver",
  "attack_damage": 5.0,
  "attack_speed": -3.0,
  "aoe_radius": 2.5,
  "aoe_damage_multiplier": 0.5
}
```

## Fields

A weapon is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore` and `events`
all work exactly as they do there. On top of that:

| Field | Default | Meaning |
| --- | --- | --- |
| `material` | required | The [tier](./ItemTiers.md) the weapon is made from. |
| `weapon_type` | `sword` | Which shape to build. See [Weapon types](#weapon-types). |
| `attack_damage` | `0.0` | Damage bonus on top of the material's. |
| `attack_speed` | `0.0` | Attack speed modifier. Vanilla swords use `-2.4`, axes `-3.0`. |

### material

Either a vanilla name or a pack's own [item tier](./ItemTiers.md):

```json
{ "material": "minecraft:diamond" }
{ "material": "examplepack:cheese" }
```

Vanilla names are matched case-insensitively against `wood`, `stone`, `copper`, `iron`, `gold`,
`diamond` and `netherite`. Anything in the `minecraft` namespace that is not one of those fails the
entry rather than falling back. Any other namespace is looked up in the tier registry, so the tier has
to be registered first.

## Weapon types

| Type | Behaviour | Fields it reads |
| --- | --- | --- |
| `sword` | Vanilla sword. | — |
| `mace` | Vanilla mace, with fall-damage smashing. | `mace` |
| `spear` | Charged lunge, with speed-dependent bonuses. | `spear` |
| `longsword` | Extra reach. | `reach_bonus` |
| `rapier` | Partly bypasses armour. | `armor_pierce` |
| `dagger` | Bonus damage from behind. | `backstab_multiplier` |
| `knife` | Backstab, and optionally throwable. | `backstab_multiplier`, `throwable` |
| `cleaver` | Hits nearby targets too. | `aoe_radius`, `aoe_damage_multiplier` |
| `scythe` | Hits nearby targets too. | `aoe_radius`, `aoe_damage_multiplier` |

### Shared behaviour fields

| Field | Default | Meaning |
| --- | --- | --- |
| `reach_bonus` | `0.0` | Longsword: extra entity interaction range, in blocks, on top of the default reach. |
| `armor_pierce` | `0.0` | Rapier: fraction (`0`–`1`) of the target's armour dealt as additional armour-bypassing damage. |
| `backstab_multiplier` | `1.0` | Dagger and knife: damage multiplier when striking from behind. |
| `throwable` | `false` | Knife: right-click throws it as a projectile, consuming one from the stack. |
| `aoe_radius` | `0.0` | Cleaver and scythe: radius in blocks for secondary hits. |
| `aoe_damage_multiplier` | `0.5` | Cleaver and scythe: fraction of the main hit applied to those secondary targets. |

An `aoe_radius` of `0.0` means no area hit at all, so a cleaver without one behaves like a slow sword.

## Spears

Spear values are nested under `spear` and all default to vanilla's iron spear. Times are in **seconds**,
not ticks.

```json
{
  "weapon_type": "spear",
  "spear": {
    "charge_time": 0.95,
    "damage_multiplier": 0.95,
    "delay": 0.6
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `charge_time` | `0.95` | Charge cycle length in seconds. Attack speed works out as `1 / charge_time - 4`. |
| `damage_multiplier` | `0.95` | Damage multiplier at full charge. |
| `delay` | `0.6` | Seconds before the lunge activates after release. |
| `dismount_time` | `2.5` | Window, in seconds, in which a dismount bonus can trigger. |
| `dismount_threshold` | `8.0` | Minimum speed needed to trigger the dismount bonus. |
| `knockback_time` | `6.75` | Window in which a knockback bonus can trigger. |
| `knockback_threshold` | `5.1` | Minimum speed for the knockback bonus. |
| `damage_time` | `11.25` | Window in which a damage bonus can trigger. |
| `damage_threshold` | `4.6` | Minimum speed for the damage bonus. |

## Maces

Mace values are nested under `mace`, and every one of them is an override — leave a field out and the
weapon uses the [tier](./ItemTiers.md)'s value.

```json
{
  "weapon_type": "mace",
  "mace": {
    "durability": 500,
    "enchantability": 15,
    "repairable": "c:ingots/cheese"
  }
}
```

| Field | Meaning |
| --- | --- |
| `durability` | Uses before breaking. Omit to use the material's. |
| `enchantability` | Enchantment quality. Omit to use the material's. |
| `repairable` | Item tag whose members repair it. Omit to use the material's repair items. |

## See also

* [Items](./Items.md) — everything a weapon inherits.
* [Item Tiers](./ItemTiers.md) — the `material` field.
* [Ranged Weapons](./RangedWeapons.md) — bows, crossbows and tridents.
* [Tools](./Tools.md) — pickaxes, axes and the rest.
