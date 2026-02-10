# Fluid Definitions

Fluids define custom liquids and their behavior.

Fluid definitions go in the `fluid` directory in the thing pack.

E.g.
```
/things/examplepack/fluid/liquid_cheese.json
```

## Basic structure of the JSON file

```json
{
  "parent": "WATER",
  "name": {
    "text": "Liquid Cheese"
  },
  "fluidColor": "16770519",
  "fluidFogColor": "16770519",
  "canBeInfinite": true,
  "flowSpeed": 4,
  "tickRate": 5
}
```

## "parent"

Controls which vanilla preset to start from before applying overrides.

Required. Values: `WATER`, `LAVA`, or `NONE`.

## "name"

A [NameInformation] object used for the registered fluid name.

Required.

## Color fields

* `fluidColor`: tint color for the fluid (stringified integer).
* `fluidFogColor`: fog color for the fluid (stringified integer).

## Flow and behavior fields

Fluids support a large number of tuning fields, including:

* `allowSprintSwimming`
* `canExtinguish`
* `canIgnite`
* `maxFluidLevel`
* `pushStrength`
* `fallDamageReduction`
* `horizontalViscosity`
* `verticalViscosity`
* `density`
* `temperature`
* `canBeInfinite`
* `flowSpeed`
* `levelDecreasePerBlock`
* `tickRate`
* `randomTicking`
* `blastResistance`
* `boatFloats`

These map directly to fields in `io.github.vampirestudios.obsidian.api.obsidian.fluid.Fluid`.

## Effects and sounds

Optional fields for particles and sounds:

* `splashSound`
* `highSpeedSplashSound`
* `particleType`
* `splashParticle`
* `bubbleParticle`
* `fishingLootTable`