# Item Tier Definitions

Item tiers define tool materials used for tools and weapons.

Tier definitions go in the `item/tier` directory in the thing pack.

E.g.
```
/things/examplepack/item/tier/cheese.json
```

## Basic structure of the JSON file

```json
{
  "durability": 250,
  "mining_speed": 6.0,
  "attackDamage": 2.0,
  "enchantability": 14,
  "repair_item": ["examplepack:cheese_ingot"],
  "incorrect_blocks_for_drops": "minecraft:needs_stone_tool"
}
```

## "durability"

Defines how many uses a tool made from this tier has.

Required.

## "mining_speed"

Defines the mining speed modifier for this tier.

Required.

## "attack_damage"

Defines the attack damage bonus for this tier.

Required.

## "enchantability"

Defines how enchantable the tool is.

Required.

## "repair_item"

A list of item identifiers used for repairing tools of this tier.

Required. At least one entry is required.

## "incorrect_blocks_for_drops"

A block tag identifier that defines which blocks do *not* drop items when mined with this tier.

Required.