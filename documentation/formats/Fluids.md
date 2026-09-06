# Fluids

Custom liquids: how they look, how they flow, and what happens to things standing in them.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/fluid/liquid_cheese.json
```

The file name is the fluid id.

## Example

```json
{
  "parent": "WATER",
  "name": "Liquid Cheese",
  "fluidColor": "16770519",
  "fluidFogColor": "16770519",
  "canBeInfinite": true,
  "flowSpeed": 4,
  "tickRate": 5,
  "horizontalViscosity": 0.8
}
```

Field names here are camelCase — this format is read field-for-field, without the snake_case renaming
used by items and blocks.

## Choosing a base

`parent` is required and picks the preset the rest of the file adjusts.

| Value | Starts from |
| --- | --- |
| `WATER` | Water: swimmable, infinite-capable, extinguishes fire. |
| `LAVA` | Lava: slow, damaging, emits light. |
| `NONE` | No preset — you set everything. |

`name` is required too. Everything else has a default.

## Appearance

| Field | Meaning |
| --- | --- |
| `fluidColor` | Tint, as a stringified integer — `"16770519"`, not `16770519`. |
| `fluidFogColor` | Fog colour underwater, same format. |
| `particleType`, `splashParticle`, `bubbleParticle` | Particle ids. |
| `splashSound`, `highSpeedSplashSound` | Sound ids for entering the fluid. |

## Flow

| Field | Meaning |
| --- | --- |
| `flowSpeed` | How fast it spreads. |
| `tickRate` | Ticks between flow updates. Lower is faster. |
| `levelDecreasePerBlock` | How much a level drops per block travelled. Water is `1`, lava `2`. |
| `maxFluidLevel` | Depth of a source block. |
| `canBeInfinite` | Whether two sources make a third. |
| `randomTicking` | Whether the fluid receives random ticks. |

Each of these has an `…Ultrawarm` counterpart (`flowSpeedUltrawarm`, `tickRateUltrawarm`,
`levelDecreasePerBlockUltrawarm`) used in the Nether, gated by a matching
`…ChangesWhenWarm` boolean. Set both or the Nether values are ignored.

## Physics and effects

| Field | Meaning |
| --- | --- |
| `horizontalViscosity`, `verticalViscosity` | Drag on entities moving through it. |
| `density`, `temperature` | Used by buoyancy and warmth checks. |
| `pushStrength` | How hard the current pushes. `pushStrengthUltrawarm` and `pushStrengthChangesWhenWarm` apply in the Nether. |
| `allowSprintSwimming` | Whether players can swim-sprint. |
| `fallDamageReduction`, `fallDamageReductionType` | How much of a fall the fluid absorbs. |
| `canExtinguish`, `canIgnite` | Whether it puts entities out, or sets them alight. |
| `boatFloats`, `fishingBobberFloats`, `canFish` | Boats, bobbers and fishing. |
| `fishingLootTable` | What fishing in it yields. |
| `blastResistance` | Resistance to explosions. |

## Fluid types

There is no separate fluid-type registry: `parent` is the whole mechanism. Anything a preset does not
give you is set field by field here.

## See also

* [Blocks](./Blocks.md) — for the block side of a fluid's world presence.
