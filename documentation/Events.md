# Events and Actions

Items and blocks both take an `events` map: an event name, and a list of actions that run in order when
it fires.

```json
{
  "events": {
    "on_use": [
      { "action": "play_sound", "sound": "minecraft:block.note_block.bell" },
      { "action": "apply_effect", "effect": "minecraft:speed", "duration": 200, "amplifier": 0 }
    ]
  }
}
```

Every action object needs an `action` key. One without it is skipped with a warning naming the object,
and the rest of the list still runs. Same for an unknown action name — one bad action does not stop the
others.

Events run **server-side only**.

## What an action needs

An action is available on an event when the event can supply what the action needs — not based on
whether an item or a block declared it. There are three groups:

| Group | Needs | Available on |
| --- | --- | --- |
| [Player actions](#player-actions) | a player | every item and equipment event, and block or projectile events with a player |
| [Positional actions](#positional-actions) | a block position | every block event, plus `use_on`, `mine_block` and `projectile_hit_block` |
| [`convert_item`](#convert_item) | both, plus the hand | `use_on` |

An action used on an event that cannot supply what it needs is skipped with a warning naming both, and
the rest of the list still runs.

## What each event knows

Actions and conditions read from a shared context that the trigger fills in. What is present decides
what works:

| The context has | Filled in by | Used by |
| --- | --- | --- |
| the player | every item event; block events with a player | player actions, `has_item`, `health`, … |
| a block position | every block event, `use_on`, `mine_block`, `projectile_hit_block` | positional actions, `block_is`, `block_property`, … |
| the clicked face | `on_interact`, `use_on`, `on_place`, `projectile_hit_block` | rotation of positional offsets |
| the exact hit point | `on_interact`, `use_on`, `projectile_hit_block` | particle and entity placement |
| the hand | `use_on` | `convert_item` |
| a target entity | `hurt_enemy`, `interact_entity`, `projectile_hit_entity` | `ignite`, `damage`, `entity_is`, `entity_health` |
| an equipment slot | the [equipment events](#equipment-events) | `damage_item`, `consume_item`, `set_cooldown`, `item_damage`, … |

The world conditions — `dimension`, `biome`, `time_of_day`, `day_count`, `moon_phase`, `weather`,
`light_level`, `height`, `can_see_sky` — fall back to the player's position when the event has no block,
so they work everywhere.

The [event catalogues](#item-events) below list what each trigger supplies.

## `chance`

Any action takes an optional `chance` between 0 and 1, and runs that fraction of the time. An action
without one always runs.

```json
{ "action": "give_item", "item": "minecraft:diamond", "chance": 0.1 }
```

## `conditions`

Any action takes an optional `conditions` list. Every condition has to pass for the action to run, and
an action without the key always runs. They are grouped below by what they read from the
[context](#what-each-event-knows).

```json
{
  "events": {
    "use_on": [
      {
        "action": "open_gui",
        "gui_type": "chest",
        "conditions": [
          { "condition": "block_is", "block": "examplepack:vault_door" },
          { "condition": "has_item", "item": "examplepack:keycard", "consume": true,
            "message": "Access denied" }
        ]
      }
    ]
  }
}
```

**Conditions double as requirements.** A condition with `"consume": true` takes what it matched — that is
how a key, keycard or ticket is expressed. Consumption happens only after *every* condition has passed,
so a failing later condition can never eat the key. Creative players are never charged.

Order matters for the message, not the outcome: the first failing condition is the one whose `message`
the player sees.

### Items

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `has_item` | `item` or `tag`, `count`, `where`, `consume` | The player has the item. `count` defaults to `1`; `tag` wins over `item` when both are given. |
| `has_component` | `component` | The held stack carries a data component. Presence only — values are not compared. |
| `item_damage` | `min`, `max`, `equals` | Durability used on the held stack. `0` is undamaged. |
| `durability_percent` | `min`, `max`, `equals` | Durability **left** on the held stack, 0–100. Unbreakable items read as `100`. |
| `item_count` | `min`, `max`, `equals` | Size of the held stack. |
| `enchantment` | `enchantment`, `where`, `min`/`max`/`equals` | The enchantment is present. The range applies to its level; omit it to match any level. |

`where` selects which stacks to look at, and applies to `has_item`, `enchantment`, and the `consume`
that goes with them:

| `where` | Stacks |
| --- | --- |
| `hand` | The held item. The default. |
| `inventory` | Everything the player is carrying. |
| `armor` | The four worn pieces. |
| `mainhand`, `offhand`, `head`, `chest`, `legs`, `feet` | One equipment slot. |

### The player

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `player_state` | `state` | One of `sneaking`, `sprinting`, `swimming`, `underwater`, `in_water`, `in_lava`, `on_ground`, `flying`, `may_fly`, `burning`, `creative`, `spectator`, `alive`, `sleeping`, `using_item`. |
| `is_sneaking` | — | Shorthand for `player_state` with `sneaking`. |
| `health` | `min`, `max`, `equals` | Half-hearts, so `20` is full. |
| `food_level` | `min`, `max`, `equals` | 0–20. |
| `experience_level` | `min`, `max`, `equals` | |
| `has_effect` | `effect`, `min_amplifier` | Amplifier is 0-based, so `min_amplifier: 1` means Strength II or better. |
| `gamemode` | `value` or `values` | `survival`, `creative`, `adventure`, `spectator`. |
| `riding` | `entity` or `entities` | The player is riding something. Naming nothing matches any vehicle — including `obsidian:seat`, so this is how a block tells whether someone is sitting on it. |

### The block

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `block_is` | `block` or `blocks` | The block at the event's position. |
| `block_has_tag` | `tag` or `tags` | |
| `block_property` | `property`, `value` | One state property, by the name the property itself uses — `"property": "open", "value": "true"`. |
| `block_at_offset` | `x`, `y`, `z`, plus `block`/`blocks` or `tag`/`tags` | A neighbouring block. Offsets are **not** rotated, unlike the positional actions — this asks about the world, not about a layout. |
| `standing_on` | `block`/`blocks` or `tag`/`tags` | The block under the player's feet. `block_at_offset` cannot reach it: its offsets start from the event's block, not from the player. |

### The world

Each of these falls back to the player's position when the event has no block.

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `dimension` | `dimension` or `dimensions` | |
| `biome` | `biome`/`biomes`, or `tag`/`tags` | |
| `time_of_day` | `value`, or `min`/`max` | A named span or brightness, or a range in ticks within the 24000-tick day. |
| `day_count` | `min`, `max`, `equals` | Days since the world was made — the number F3 shows. For gating progression. |
| `moon_phase` | `value`, or `min`/`max` | `full`, `waning_gibbous`, `last_quarter`, `waning_crescent`, `new`, `waxing_crescent`, `first_quarter`, `waxing_gibbous`, or the numbers 0–7 in that order. |
| `weather` | `value`, `local` | `clear`, `raining` or `thundering`. With `"local": true` it asks whether the weather reaches this spot, so a desert or anything with a roof never counts as raining. |
| `light_level` | `min`, `max`, `equals`, `type` | `type` is `any` (default, the brighter of the two — what mob spawning uses), `block` for torches and lava, or `sky` for daylight reaching the spot. |
| `height` | `min`, `max`, `equals` | Y level. |
| `can_see_sky` | — | Nothing solid overhead. |

`time_of_day` takes two kinds of `value`. Four of them are spans on the clock, each starting at one of
vanilla's time markers and running to the next, so they never overlap and always cover the whole day:

| `value` | Ticks |
| --- | --- |
| `day` | 1000–5999 |
| `noon` | 6000–12999 |
| `night` | 13000–17999 |
| `midnight` | 18000–999, wrapping |

The other two, `bright_outside` and `dark_outside`, ask the dimension whether it is light out instead of
comparing ticks — so they still mean something where the sky does not work like the overworld's. Reach
for those when what you actually care about is daylight rather than the hour.

### The target entity

Only `hurt_enemy` and `interact_entity` supply one; everywhere else these fail.

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `entity_is` | `entity`/`entities`, or `tag`/`tags` | |
| `entity_health` | `min`, `max`, `equals` | |
| `entity_is_baby` | — | |

### Logic

| `condition` | Keys | Meaning |
| --- | --- | --- |
| `all_of` | `conditions` | Every nested condition passes. Same as listing them, but nestable. |
| `any_of` | `conditions` | At least one passes. |
| `none_of` | `conditions` | None passes. |
| `chance` | `value` | 0–1. |

`any_of` takes the consumables of **only the branch that passed**, which is what makes "either card opens
this" work without charging the player twice:

```json
{
  "condition": "any_of",
  "conditions": [
    { "condition": "has_item", "item": "examplepack:gold_card", "consume": true },
    { "condition": "has_item", "item": "examplepack:silver_card", "consume": true }
  ]
}
```

`none_of` never consumes anything, since by definition nothing matched.

### Shared keys

| Key | Meaning |
| --- | --- |
| `invert` | Flips the result. `block_is` + `invert` means "any block but this one". |
| `message` | Shown to the player when this condition is the one that failed. Plain text, like `send_message`. Action bar by default; `"overlay": false` sends it to chat. |
| `consume` | Only meaningful on `has_item`. Ignored, with a warning, when combined with `invert` — an inverted check passes because the item was *absent*, so there is nothing to take. |

Conditions taking `min`/`max` also accept `equals` for one exact value, and treat a missing `min` or
`max` as an open end.

A condition that needs something the event cannot supply — `block_is` on `on_use`, `entity_is` on
anything but `hurt_enemy` and `interact_entity` — fails, and the action is skipped.

### Unknown conditions fail closed

An unknown or malformed **condition** fails the whole list and the action does not run. This is the
opposite of an unknown **action**, which is skipped while the rest of the list carries on. The reasoning
is that a typo in a lock must not open the door: `"condition": "has_itemm"` denies access rather than
granting it to everyone. Check the log if an action mysteriously stops firing.

## Item events

| Event | Fires when | Target |
| --- | --- | --- |
| `on_use` | The item is right-clicked. | — |
| `use_on` | The item is right-clicked on a block. | — |
| `interact_entity` | The item is right-clicked on a living entity. | the entity |
| `hurt_enemy` | The item damages an entity. | the entity |
| `mine_block` | A block is mined with the item. Supports [filters](#mine_block-filters). | — |
| `use_tick` | Every tick while the item is being used. | — |
| `finish_using` | Use completes, full duration elapsed. | — |
| `stop_using` | Use is released early. | — |
| `inventory_tick` | Every tick the item sits in an inventory. | — |
| `craft` | The item is crafted. | — |
| `on_pickup` | A dropped stack is picked up. | — |
| `destroyed` | A dropped stack burns or is blown up. Only if a player dropped it. | — |

"Target" matters for the few actions that act on something other than the player — `ignite` and
`extinguish` hit the target where there is one, and the player otherwise.

`use_on` and `mine_block` know *where* they happened, so they can run the
[positional actions](#positional-actions) as well as the player ones. `use_on` is also the only event
that runs [`convert_item`](#convert_item).

## Projectile events

For [ranged weapons](./formats/RangedWeapons.md) — a bow, a crossbow, or an item with a `shooter`
component.

| Event | Fires when | Has a player | Target |
| --- | --- | --- | --- |
| `on_shoot` | The weapon launches a projectile. | yes | — |
| `projectile_hit_entity` | A projectile it launched strikes an entity. | only if a player fired it | the entity |
| `projectile_hit_block` | A projectile it launched strikes a block. | only if a player fired it | — |

`on_shoot` fires **once per projectile**, so a multishot crossbow fires it three times, not once.

The hit events belong to the weapon, not the arrow — Obsidian tags each projectile with the item that
launched it, and the tag is what carries the events across. Because a skeleton can fire an Obsidian bow,
the hit events may have no player behind them; the [positional actions](#positional-actions) still run,
and the player ones are skipped with a warning as usual.

`projectile_hit_block` knows where it landed and which face it struck, so it can place, break or convert
blocks there. `projectile_hit_entity` supplies the entity as the target, so `ignite` and `damage` apply to
what was hit rather than to the shooter:

```json
{
  "events": {
    "projectile_hit_entity": [
      { "action": "ignite", "duration_in_seconds": 4 }
    ],
    "projectile_hit_block": [
      { "action": "play_sound", "sound": "minecraft:block.fire.extinguish" }
    ]
  }
}
```

A projectile still in flight when the world is unloaded comes back untagged and fires no hit events.

## Equipment events

These fire for whatever is worn in the four armor slots and the off hand. They work on any item that can
end up in one — an [armor](./formats/ArmorMaterials.md) piece, an elytra, a shield, or a plain item with
an equippable component.

| Event | Fires when |
| --- | --- |
| `on_equip` | The item appears in an equipment slot. |
| `on_unequip` | The item leaves an equipment slot. |
| `equipment_tick` | Every tick the item stays in one. |

The stack these events are about is the **worn** one, not whatever the player is holding. `damage_item`
wears down the helmet, `item_damage` and `durability_percent` read the helmet, and a break plays in the
helmet's slot.

Obsidian watches the slots rather than the ways an item gets into them, so all of them fire the same:
dragging in the inventory screen, right-clicking, a dispenser, `/item`, or picking gear back up after
dying. That also means gear a player logs in already wearing counts as newly equipped, which makes
`on_equip` a dependable place to set up state that `on_unequip` takes back down:

```json
{
  "events": {
    "on_equip": [
      { "action": "modify_attribute", "id": "example:diver_helmet", "attribute": "minecraft:oxygen_bonus", "amount": 2, "operation": "add_value" }
    ],
    "equipment_tick": [
      { "action": "apply_effect", "effect": "minecraft:water_breathing", "duration": 40, "amplifier": 0, "conditions": { "in_water": true } }
    ]
  }
}
```

Held items are deliberately not watched — scrolling the hotbar is not equipping, and an item in hand
already has `inventory_tick` and the use events.

On `on_unequip` the stack has already left the slot, so the context points at whatever now occupies it,
usually nothing. Undo state there; do not expect to read the item that left.

## Block events

| Event | Fires when | Has a player |
| --- | --- | --- |
| `on_interact` | The block is right-clicked with an empty hand. | yes |
| `on_attack` | The block is left-clicked. | yes |
| `on_step_on` | An entity steps on the block. | only if the entity is a player |
| `on_entity_inside` | An entity is inside the block's shape. | only if the entity is a player |
| `on_place` | The block is placed. | no |
| `on_remove` | The block is removed. | no |
| `on_random_tick` | The block gets a random tick. Needs `randomTicks` in its [settings](./formats/BlockSettings.md). | no |

Most actions act on a player. On the events with no player, those are skipped with a warning in the log
— only the [positional actions](#positional-actions) do anything there.

### Which blocks fire them

Every plain block does, along with the horizontal-facing types, `crop` and `bush`. The more specialised
types — `sapling`, `leaves`, `plant`, the double plants, `bed`, `campfire` and the working stations —
are registered as their own vanilla-derived blocks and do **not** fire block events yet; declaring
`events` on one of those is currently silent. `crop` and `bush` fire `on_interact`, `on_place`,
`on_random_tick` and, for a bush, `on_entity_inside`.

`on_interact` fires when the click is not already handled by the held item — always with an empty hand,
and with a held item only when that item's own use passes the interaction on. A "this item opens this
block" behaviour therefore belongs on the item's `use_on`, which always fires and knows both sides.

## Player actions

Available on every item and equipment event, and on block events that have a player.

"The event's stack" is the worn item on an [equipment event](#equipment-events), the stack in the hand
that triggered it on `use_on`, and the main hand everywhere else.

| `action` | Keys | Notes |
| --- | --- | --- |
| `send_message` | `message`, `overlay` | `overlay` defaults to `true` (action bar). `false` sends it to chat. |
| `apply_effect` | `effect`, `duration`, `amplifier` | `duration` in ticks. All three are required. |
| `remove_effect` | `effect` | |
| `clear_effects` | — | Removes every effect. |
| `give_experience` | `amount` | Points. Default `0`. |
| `give_experience_levels` | `amount` | Levels. Default `0`. |
| `give_item` | `item`, `count` | `count` defaults to `1`. Drops at the player's feet if the inventory is full. |
| `consume_item` | `count` | Shrinks the event's stack. Default `1`. |
| `damage_item` | `amount` | Damages the event's stack. Default `1`. No effect on items without durability. |
| `set_cooldown` | `seconds` | Use cooldown on the event's stack. |
| `heal` | `amount` | Half-hearts — `2` is one heart. |
| `feed` | `food`, `saturation` | |
| `extinguish` | — | The target if there is one, otherwise the player. |
| `play_sound` | `sound`, `volume`, `pitch` | Defaults `0.5` and `1.0`. Plays at the player. |
| `spawn_particles` | `particle`, `count`, `speed`, `offsetX`, `offsetY`, `offsetZ` | |
| `modify_attribute` | `id`, `attribute`, `amount`, `operation` | See [Attribute Modifiers](./formats/AttributeModifiers.md). |
| `execute_command` | `command` | Runs at the player's own permission level. |
| `teleport` | `x`, `y`, `z` | |
| `ignite` | `duration_in_seconds` | The target on `hurt_enemy` and `interact_entity`, the player elsewhere. Default `1`. |
| `damage` | `amount` | Half-hearts. Same subject rule as `ignite` — the target if there is one. |
| `open_gui` | `gui_type`, `gui_size`, `title` | See [below](#open_gui). |
| `open_crate` | `menu` | Opens a crate menu. See [below](#open_crate). |

## Positional actions

These act on a block position, so they work on **every** block event — including `on_random_tick`, which
has no player at all — on the two item events that have a position, `use_on` and `mine_block`, and on
`projectile_hit_block`. On an item event they act on the block that was clicked or mined, and on a
projectile event the block it struck.

On an event with no position at all, such as `on_use`, they are skipped with a warning in the log.

| `action` | Keys | Notes |
| --- | --- | --- |
| `set_block` | `block` | Replaces the block that fired the event. |
| `set_block_at_pos` | `block`, `x`, `y`, `z`, `replace` | Offsets relative to the block, each defaulting to `0`. |
| `fill_blocks` | `block`, `from`, `to`, `replace` | Fills a box of offsets. Both `from` and `to` are required, as `[x, y, z]`. |
| `set_block_property` | `property`, `value` | Changes one state property, leaving the rest alone. |
| `spawn_loot` | — | Drops the block's loot table without removing the block. |
| `play_sound_at` | `sound`, `volume`, `pitch` | Plays at the block rather than at the player. |
| `spawn_entity` | `entity`, `count`, `x`, `y`, `z`, plus [mob settings](./formats/WorldEvents.md#shaping-the-mobs) | Spawns at the block, offset by `x`/`y`/`z`. Random facing. Also takes `name`, `equipment`, `attributes`, `effects`, `persistent` and `finalize_spawn`. |
| `drop_item` | `item`, `count`, `x`, `y`, `z` | Drops in the world — the counterpart to `give_item`, which needs a player. |
| `spawn_particles_at` | `particle`, `count`, `spread`, `speed`, `x`, `y`, `z` | At the block rather than the player. `spread` defaults to `0.5`. |
| `strike_lightning` | `visual_only`, `x`, `y`, `z` | `"visual_only": true` skips the fire and damage. |

Two things make these usable for props and multi-block structures:

* **Offsets rotate with the block.** A layout authored for a north-facing block follows it around, the
  same way [shapes](./formats/VoxelShapes.md) do.
* **`replace` restricts what may be overwritten.** `"replace": "minecraft:air"` turns a fill into a
  placement that refuses to eat the player's build.

`fill_blocks` never overwrites the block that fired the event, and refuses boxes over **512 blocks**,
logging a warning instead.

```json
{
  "events": {
    "on_place": [
      { "action": "fill_blocks", "block": "examplepack:invisible_collider",
        "from": [0, 0, 1], "to": [0, 2, 2], "replace": "minecraft:air" }
    ],
    "on_remove": [
      { "action": "fill_blocks", "block": "minecraft:air",
        "from": [0, 0, 1], "to": [0, 2, 2], "replace": "examplepack:invisible_collider" }
    ]
  }
}
```

## `open_gui`

`gui_type` accepts `chest`, `dispenser`, `anvil`, `crafting`, `smithing`, `cartography`, `stonecutter`,
`merchant`, `smoker`, `blast_furnace`, `furnace`, `brewing_stand`, `hopper`, `shulker_box`, `crafter`,
`beacon`, `enchantment`, `grindstone` and `loom`.

`gui_size` defaults to `3` and only applies to `chest`.

Menus that normally need a block in the world — `enchantment`, `grindstone`, `loom`, `anvil`, `crafting`,
`smithing`, `cartography`, `stonecutter` — open at the player's position instead. `enchantment` therefore
counts the bookshelves around the *player*, not around a table.

## `open_crate`

Rolls a loot pool and opens the resulting menu. The config is a [menu config](./formats/CustomMenu.md),
taken from one of two places:

* **From the block**, when the action has no `menu` key. Reads the `menu_config` of the block at the
  event's position, so the crate's rewards live on the crate.
* **Inline**, from a `menu` object on the action itself.

```json
{ "action": "open_crate" }
```

```json
{
  "action": "open_crate",
  "menu": { "title": "Prize", "rows": 1, "loot_pool": { "entries": [ { "item": "minecraft:emerald" } ] } }
}
```

An inline `menu` whose pool sets `"consume": true` shrinks the held stack — that is a crate item eating
itself. A crate read **from a block never touches the held stack**, so take the key with a `has_item`
condition carrying `consume` instead.

Needs a player, and needs a block position unless the `menu` is inline.

## `convert_item`

Only runs on `use_on`, and only on items.

| Key | Meaning |
| --- | --- |
| `converted_item` | What the held item turns into. Required. |
| `block` | Only convert when clicking this block. Omitted means any block. |
| `amount_required` | Minimum held count. |
| `cooldown` | Cooldown between conversions. |

For turning *blocks* into other blocks, prefer [Chisel Mappings](./formats/ChiselMappings.md) or a block
[`convertible`](./formats/Blocks.md#convertible) — both compile to vanilla's block transformer component
and are handled by the game rather than at runtime.

## `mine_block` filters

By default a `mine_block` action runs for every block mined. Add any of these keys to the action object
and it only runs when one of them matches:

| Key | Matches |
| --- | --- |
| `blocks` | A list of block ids. |
| `tags` | A list of block tags. |
| `position` | An exact `[x, y, z]`. |
| `min_position` + `max_position` | A box, both required. |

The filters are OR-ed: an action with both `blocks` and `tags` runs when *either* matches.

## See also

* [Items](./formats/Items.md) — where item events live.
* [Blocks](./formats/Blocks.md) — where block events live.
* [Entities](./formats/Entities.md) — entities have their own, separate event system.
