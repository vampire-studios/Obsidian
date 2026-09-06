# Biome Modifications

Biome modifications add to, remove from, replace, or override parts of existing biomes. They can
target vanilla, modded, or data-pack biomes by id or biome tag.

They do **not** define a new biome or make one appear in a dimension. Define a biome with a normal
data-pack file under `data/<namespace>/worldgen/biome/`; biome placement is a separate concern.

## Where it goes

```text
obsidian_addons/ExamplePack/content/examplepack/world/biome_modification/lavender_forests.json
```

The file name becomes the modification id, so this example registers the ordered modification
`examplepack:lavender_forests`.

Biome modifications support the pack's declared `JSON`, `YAML`, `TOML`, `HJSON`, or `HOCON` format.
Legacy addon packs read this format as JSON.

> **Current loader limitation:** content definitions in `server_obsidian_addons` are not yet passed
> through the normal addon-module loop. Put biome modifications in a normally loaded addon pack until
> the server-addon path is unified with it.

## Complete example

```json
{
  "selector": {
    "biomes": [
      "#minecraft:is_forest",
      "minecraft:plains"
    ],
    "exclude": [
      "minecraft:dark_forest"
    ],
    "dimensions": [
      "minecraft:overworld"
    ]
  },

  "add": {
    "features": [
      {
        "step": "vegetal_decoration",
        "feature": "examplepack:patch_lavender"
      }
    ],
    "carvers": [
      "examplepack:crystal_caves"
    ],
    "spawns": [
      {
        "entity": "examplepack:firefly",
        "category": "ambient",
        "weight": 12,
        "min": 2,
        "max": 5
      }
    ]
  },

  "remove": {
    "features": [
      "minecraft:ore_dirt"
    ],
    "carvers": [],
    "spawns": [
      "minecraft:zombie"
    ],
    "spawn_categories": []
  },

  "replace": {
    "features": [
      {
        "step": "vegetal_decoration",
        "from": "minecraft:trees_plains",
        "to": "examplepack:trees_lavender_plains"
      }
    ]
  },

  "set": {
    "has_precipitation": true,
    "temperature": 0.7,
    "temperature_modifier": "none",
    "downfall": 0.8,
    "water_color": "#456e78",
    "grass_color": "#799c55",
    "foliage_color": "#628f45",
    "dry_foliage_color": "#93845b",
    "grass_color_modifier": "none"
  },

  "environment_attributes": {
    "minecraft:visual/fog_color": "#71808c",
    "minecraft:visual/cloud_height": 160.0,
    "minecraft:audio/music_volume": {
      "modifier": "multiply",
      "argument": 0.65
    },
    "minecraft:gameplay/monsters_burn": true,
    "minecraft:visual/ambient_particles": {
      "modifier": "append",
      "argument": [
        {
          "particle": {
            "type": "minecraft:ash"
          },
          "probability": 0.002
        }
      ]
    }
  }
}
```

Every section after `selector` is optional, but a file must make at least one change.

## Selector

```json
{
  "selector": {
    "biomes": ["minecraft:plains", "#minecraft:is_forest"],
    "exclude": ["minecraft:dark_forest"],
    "dimensions": ["minecraft:overworld"]
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `biomes` | none | Biome ids and `#`-prefixed biome tags. An entry matching is enough. |
| `exclude` | none | Biome ids and tags removed from the result after inclusion. |
| `dimensions` | any | Level-stem ids in which the biome must be able to generate. An entry matching is enough. |
| `all` | `false` | Explicitly select all biomes. `all_biomes` is also accepted. |

`biomes` and `all: true` are mutually exclusive. A selector with neither is rejected; selecting every
biome must always be deliberate. `all: true` may be combined with `exclude` and `dimensions`.

Dimension filtering uses the world's biome sources. It asks whether the biome can generate in that
level stem; it does not guess from the biome's namespace or temperature.

## Additions

Additions run in Fabric's `ADDITIONS` phase.

### Placed features

```json
{
  "add": {
    "features": [
      { "step": "underground_ores", "feature": "examplepack:ore_moonstone" },
      { "step": "vegetal_decoration", "feature": "examplepack:patch_lavender" }
    ]
  }
}
```

`feature` is a **placed feature**, normally defined under
`data/<namespace>/worldgen/placed_feature/`. `step` is required and accepts:

| Generation step | Typical contents |
| --- | --- |
| `raw_generation` | Earliest terrain-linked features. |
| `lakes` | Lakes. |
| `local_modifications` | Local terrain modifications. |
| `underground_structures` | Underground structure features. |
| `surface_structures` | Surface structure features. |
| `strongholds` | Strongholds. |
| `underground_ores` | Ores. |
| `underground_decoration` | Other underground decoration. |
| `fluid_springs` | Springs. |
| `vegetal_decoration` | Trees, plants and other vegetation. |
| `top_layer_modification` | Final surface-layer changes. |

### Carvers

```json
{ "add": { "carvers": ["examplepack:crystal_caves"] } }
```

Entries are ids in Minecraft's world-carver registry, normally backed by files under
`data/<namespace>/worldgen/carver/`.

### Spawns

```json
{
  "add": {
    "spawns": [
      {
        "entity": "examplepack:firefly",
        "category": "ambient",
        "weight": 12,
        "min": 2,
        "max": 5
      }
    ]
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `entity` | required | Registered entity-type id. |
| `category` | required | Mob category listed below. |
| `weight` | `10` | Relative selection weight. Must be greater than zero. |
| `min` | `1` | Minimum group size. Must be greater than zero. |
| `max` | `1` | Maximum group size. Must be at least `min`. |

Categories are `monster`, `creature`, `ambient`, `axolotls`, `underground_water_creature`,
`water_creature`, `water_ambient`, and `misc`. Obsidian rejects additions in `misc`, matching Fabric's
restriction for natural biome spawns.

## Removals

Removals run after additions, in Fabric's `REMOVALS` phase.

```json
{
  "remove": {
    "features": ["minecraft:ore_dirt"],
    "carvers": ["minecraft:cave"],
    "spawns": ["minecraft:zombie", "minecraft:skeleton"],
    "spawn_categories": ["monster"]
  }
}
```

| Field | Meaning |
| --- | --- |
| `features` | Placed-feature ids to remove from every generation step in each selected biome. |
| `carvers` | World-carver ids to remove. |
| `spawns` | Entity types whose natural spawns should be removed. |
| `spawn_categories` | Remove every spawn in each named mob category. |

Because removals run after additions, removing an entry that another modification added in the first
phase removes it too.

## Feature replacements

Replacements run in Fabric's `REPLACEMENTS` phase, after ordinary removals.

```json
{
  "replace": {
    "features": [
      {
        "step": "vegetal_decoration",
        "from": "minecraft:trees_plains",
        "to": "examplepack:trees_lavender_plains"
      }
    ]
  }
}
```

`from` is removed from every generation step. `to` is then added at the required `step`.

## Weather and biome effects

The `set` section runs in `POST_PROCESSING`, after additions, removals and replacements.

| Field | Values | Meaning |
| --- | --- | --- |
| `has_precipitation` | boolean | Whether the biome has precipitation. `precipitation` is also accepted. |
| `temperature` | number | Base temperature. |
| `temperature_modifier` | `none`, `frozen` | Position-dependent temperature adjustment. |
| `downfall` | number | Downfall value. |
| `water_color` | RGB | Water surface colour. |
| `grass_color` | RGB, `clear`, `default` | Grass-colour override, or remove the override. |
| `foliage_color` | RGB, `clear`, `default` | Foliage-colour override, or remove it. |
| `dry_foliage_color` | RGB, `clear`, `default` | Dry-foliage override, or remove it. |
| `grass_color_modifier` | `none`, `dark_forest`, `swamp` | Vanilla grass-colour calculation. |

An RGB value may be a `"#RRGGBB"` string or an integer from `0` through `16777215`. Fog, sky,
water-fog, lighting, particles and music belong in `environment_attributes`, not this section.

## Environment attributes

`environment_attributes` is decoded by Minecraft's native `EnvironmentAttributeMap.CODEC`. Obsidian
does not keep a separate name or type table, so this section supports both vanilla attributes and
attributes registered by other mods.

### Override a value

A direct value replaces the attribute for selected biomes:

```json
{
  "environment_attributes": {
    "minecraft:visual/fog_color": "#71808c",
    "minecraft:visual/cloud_height": 160.0,
    "minecraft:gameplay/water_evaporates": false
  }
}
```

The native codec controls each value's shape and range. RGB attributes use `"#RRGGBB"`; ARGB
attributes use `"#AARRGGBB"`.

### Modify the existing value

Use `modifier` and `argument` when the value should combine with what the biome already has:

```json
{
  "environment_attributes": {
    "minecraft:audio/music_volume": {
      "modifier": "multiply",
      "argument": 0.5
    },
    "minecraft:visual/ambient_particles": {
      "modifier": "append",
      "argument": [
        {
          "particle": { "type": "minecraft:ash" },
          "probability": 0.002
        }
      ]
    }
  }
}
```

Available operations depend on the attribute's registered type:

| Attribute type | Modifiers |
| --- | --- |
| Float | `alpha_blend`, `add`, `subtract`, `multiply`, `minimum`, `maximum` |
| Integer | `add`, `subtract`, `multiply`, `minimum`, `maximum` |
| RGB / ARGB colour | `alpha_blend`, `add`, `subtract`, `multiply`, `blend_to_gray` |
| Boolean | `and`, `nand`, `or`, `nor`, `xor`, `xnor` |
| List | `append` |
| Natural mob-spawn settings | `overlay` |

For float `alpha_blend`, `argument` may be a number or
`{ "value": <number>, "alpha": <0..1> }`. Colour `blend_to_gray` takes
`{ "brightness": <0..1>, "factor": <0..1> }`.

Do not put `minecraft:gameplay/natural_mob_spawns` in `environment_attributes` when the same file uses
`add.spawns`, `remove.spawns`, or `remove.spawn_categories`. Obsidian rejects that combination because
the two ways of modifying the same value would have unclear intent. Use the high-level spawn sections
for ordinary additions and removals; reserve the native `overlay` form for advanced cases.

### Built-in attribute groups in Minecraft 26.3

The registry is the authority, but the built-in ids are grouped as follows:

* `minecraft:visual/…` — fog start/end and colours, water fog, sky and cloud colours, cloud height,
  sun/moon/star angles, moon phase, star brightness, block/sky/ambient/night-vision light colours,
  default dripstone particle and ambient particles.
* `minecraft:audio/…` — background music, music volume, ambient sounds and firefly-bush sounds.
* `minecraft:gameplay/…` — sky-light level, raids, water evaporation, bed rules, respawn anchors,
  portal piglins, fast lava, fire burnout, eyeblossoms, turtle eggs, piglin zombification, snow golems,
  creakings, slime and cat-gift chances, bees, monster burning, patrols, natural mob spawns, creature
  worldgen spawn probability and villager activities.

Use fully qualified ids in pack files even though the native identifier codec defaults an omitted
namespace to `minecraft`.

## Ordering and compatibility

Each file becomes one ordered Fabric biome modification. Its operations run in this order:

1. `add`
2. `remove`
3. `replace`
4. `set` and `environment_attributes`

Within a phase, Fabric uses the modification id derived from the addon namespace and file name to keep
ordering deterministic. Prefer additions and native modifiers such as `multiply` or `append` when
possible; hard overrides in the final phase intentionally win over earlier values.

## Validation and troubleshooting

Obsidian parses the native attribute map and validates every static field before registering any
callbacks. A file that fails this stage logs `Failed to register biome modification <file>` and
installs none of its operations. Dynamic registry references, such as entity types, must exist when
Minecraft later applies the modification.

Common failures:

* The selector has neither `biomes` nor explicit `all: true`.
* `all: true` and `biomes` are both present.
* A feature addition or replacement has no valid `step`.
* A spawn has no entity/category, uses `misc`, or has invalid weight/group sizes.
* A colour, temperature modifier, grass-colour modifier, environment attribute, or native modifier
  does not match its codec.
* High-level spawn operations are mixed with the native `natural_mob_spawns` attribute.

Placed-feature, carver and biome ids are resource keys and may come from the same addon's `data`
directory. Entity types must be registered by the time biome modifications are applied.

## Related data-pack files

| Goal | Location |
| --- | --- |
| Define a new biome | `data/<namespace>/worldgen/biome/<name>.json` |
| Define a configured feature | `data/<namespace>/worldgen/configured_feature/<name>.json` |
| Define a placed feature used by `add`/`replace` | `data/<namespace>/worldgen/placed_feature/<name>.json` |
| Define a carver used by `add`/`remove` | `data/<namespace>/worldgen/carver/<name>.json` |
| Assign structure biomes | Use biome tags referenced by the structure definition. |

Defining a biome and modifying it are independent. A new biome can be selected here as soon as its
resource key exists, but a separate biome-source or placement mechanism must still make it generate.

## See also

* [Pack Structure](../PackStructure.md) — addon metadata, content folders and the bundled data pack.
* [Entities](./Entities.md) — defining an entity type used by a biome spawn addition.
