# Food

Food comes in two files: a **food component** describing what eating does, and a **food item** that uses
one. They live in different directories and are loaded by different modules.

| Directory | What it defines |
| --- | --- |
| `item/food/food_component` | A named set of food properties. Registers no item. |
| `item/food` | An edible item that references a food component. |

## Food components

```
obsidian_addons/ExamplePack/content/examplepack/item/food/food_component/cheese.json
```

```json
{
  "hunger": 4,
  "saturation": 8.0,
  "can_always_eat": false,
  "snack": false
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `hunger` | `4` | Hunger points restored. Two per drumstick. |
| `saturation` | `8.0` | Saturation modifier — how long before hunger starts dropping again. |
| `can_always_eat` | `false` | Edible on a full hunger bar. For snacks, not meals. |
| `snack` | `false` | Eats in half the time of a meal, like dried kelp. |
| `effects` | none | List of effects; see [Effect Instances](./EffectInstances.md). |
| `id` | file name | Overrides the registered id. Normally leave this out. |

The id is the file name, so the example registers `examplepack:cheese`.

Each entry in `effects` carries its own `chance` between 0 and 1, defaulting to 1, so one food can apply
a certain effect and a rare one together:

```json
{
  "hunger": 6,
  "saturation": 7.2,
  "effects": [
    { "effect": "minecraft:regeneration", "duration": 5, "amplifier": 0 },
    { "effect": "minecraft:nausea", "duration": 8, "amplifier": 0, "chance": 0.25 }
  ]
}
```

`duration` is in seconds, not ticks.

## Food items

```
obsidian_addons/ExamplePack/content/examplepack/item/food/cheese_wheel.json
```

A food item is an ordinary [item definition](./Items.md) plus a `food_information` section:

```json
{
  "information": {
    "name": "Cheese Wheel",
    "item_properties": { "item_group": "examplepack:cheese" }
  },
  "food_information": {
    "food_component": "examplepack:cheese"
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `food_component` | — | Id of a food component. Required; a missing or unknown id fails registration. |
| `drinkable` | `false` | Drinking rather than eating animation. |
| `eat_sound`, `drink_sound` | vanilla | Sounds used while consuming. |
| `use_time` | — | Ticks the item takes to consume. |
| `return_item` | — | Item left behind, like a bowl or bottle. |
| `fullness` | `0` | Used by the AppleSkin/Meal API integration. |

Food components are shared, so several food items can point at the same one. The eating fields above
belong to the item rather than the component, so two foods can share one component and still be eaten
and drunk respectively.

## How this maps onto components

The game splits a food in two. Nutrition — `hunger`, `saturation`, `can_always_eat` — is the
`minecraft:food` component. Everything about the act of eating — the effects, how long it takes, the
animation and the sound — is `minecraft:consumable`. Obsidian writes both from the declarations above,
so there is nothing extra to set.

Writing `minecraft:consumable` yourself in the item's `components` overrides the generated one entirely,
including `effects` and `snack`. Use that when you want something the fields above cannot express;
otherwise leave it out.

## See also

* [Items](./Items.md) — the item format a food item extends.
* [Effect Instances](./EffectInstances.md) — the shape of `effects`.
