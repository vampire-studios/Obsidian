# Fuel Sources

What a furnace burns, and what a brewing stand burns. One file makes an item — or every item in a tag —
usable as fuel, for a chosen length of time and at a chosen speed.

## Where they go

```
obsidian_addons/ExamplePack/content/examplepack/fuel_source/cooking/cheese_rind.json
obsidian_addons/ExamplePack/content/examplepack/fuel_source/brewing/cheese_powder.json
```

The file name is the entry id. It never appears in game — the fuel is attached to the item, not looked
up by name.

## Cooking fuel

```json
{
  "item": "examplepack:cheese_rind",
  "burn_time": 600,
  "speed_multiplier": 1.0
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `item` | one of | The item that becomes fuel. |
| `tag` | one of | An item tag whose every member becomes fuel. Used when `item` is absent. |
| `burn_time` | yes | Ticks of smelting one unit provides. Coal is `1600`, a plank `300`. |
| `speed_multiplier` | no | How fast this fuel smelts. Defaults to vanilla's cooking speed. |
| `operation` | no | `add` (default) or `remove`. See [Removing fuels](#removing-fuels). |

`burn_time` is in **ticks**, so 600 is thirty seconds — three items smelted at the usual ten seconds
each.

## Brewing fuel

```json
{
  "item": "examplepack:cheese_powder",
  "uses": 30,
  "speed_multiplier": 1.0
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `item` | one of | The item that becomes brewing fuel. |
| `tag` | one of | An item tag whose every member becomes brewing fuel. Used when `item` is absent. |
| `uses` | yes | How many brewing operations one item powers. Blaze powder is `20`. |
| `speed_multiplier` | no | How fast the stand brews on this fuel. Defaults to vanilla's brewing speed. |

Brewing fuels are counted in **uses**, not ticks — the one place the two formats differ in more than
their field names.

## item or tag

Give one or the other. `item` wins when both are present; the tag branch only runs when `item` is
absent, and a file with neither fails on the missing tag.

```json
{ "tag": "c:foods/cheese", "burn_time": 200 }
```

Tags come from the pack's [`data` directory](../PackStructure.md#assets-and-data), or from vanilla and
other mods.

## Removing fuels

An empty object clears the fuel component instead of setting it, which takes an item *out* of the fuel
list:

```json
{ "item": "minecraft:oak_planks" }
```

A file that names a target but gives no `burn_time` (or, for brewing, no `uses`) removes that item's
fuel component. This is how a pack stops planks burning, or takes blaze powder out of the brewing stand.

`operation: "remove"` is parsed and readable from Obsidian's registry, but the clearing behaviour above
is driven by the missing time field, not by `operation`.

## How it is applied

Fuels are written as vanilla item components — `minecraft:cooking_fuel` and `minecraft:brewing_fuel` —
onto every matching item at startup. That means they apply to vanilla and other mods' items just as
readily as to the pack's own, and that an item declaring the component itself is overwritten by a fuel
source naming it.

## See also

* [Items](./Items.md) — items to make burnable.
* [Item Settings](./ItemSettings.md) — the per-item settings block.
* [Potions](./Potions.md) — what a brewing stand is being fuelled for.
