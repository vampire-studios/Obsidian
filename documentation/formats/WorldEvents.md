# World Events

A staged event with a boss bar: a fight, a siege, a countdown, a ritual that takes a while.

An event does not start on its own. Something fires the `start_event` action — a
[pattern](./Patterns.md) being activated, a block being clicked, an item being used — which is what
keeps the format from needing triggers of its own.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/world/event/cheese_siege.json
```

The file name is the event id.

## Example

```json
{
  "name": "The Cheese Siege",
  "radius": 64,
  "bar": { "color": "yellow", "overlay": "notched_10", "boss_music": true },
  "stages": [
    {
      "name": "First Wave",
      "wave": {
        "spawns": [{ "entity": "examplepack:cheese_zombie", "count": 5 }],
        "spread": 12,
        "per_player": 0.5
      },
      "advance_when": { "wave_cleared": true }
    },
    {
      "name": "Calm Before",
      "advance_when": { "duration": 200 }
    },
    {
      "name": "The Wyrm",
      "wave": {
        "spawns": [{ "entity": "examplepack:cheese_wyrm", "count": 1 }],
        "spread": 4
      },
      "advance_when": { "wave_cleared": true }
    }
  ],
  "events": {
    "on_complete": [
      { "action": "spawn_loot", "loot_table": "examplepack:siege_reward" }
    ]
  }
}
```

Starting it from a [pattern](./Patterns.md):

```json
{ "events": { "on_activate": [{ "action": "start_event", "event": "examplepack:cheese_siege" }] } }
```

## Fields

| Field | Default | Meaning |
| --- | --- | --- |
| `name` | file name | The bar's title. Stages may override it. See [Names](./Names.md). |
| `bar` | — | How the bar looks. See [The bar](#the-bar). |
| `radius` | `64` | How close a player must be to see the bar and count towards the event, in blocks. |
| `stages` | — | The stages, run in order. See [Stages](#stages). |
| `cancel_when_empty` | `true` | Whether the event ends when the last player leaves the radius. |
| `time_limit` | `0` | Ticks the whole event may run before it is lost. `0` lets it run forever. |
| `fail_when` | — | What else loses it. See [Failing](#failing). |
| `rewards` | — | What the participants get for finishing. See [Rewards](#rewards). |
| `unique` | `false` | Whether only one copy may run per dimension. |
| `cooldown` | `0` | Ticks after it ends before the same event may start again in that dimension. |
| `events` | — | `on_start`, `on_complete`, `on_cancel`, `on_fail`. See [Participants](#participants). |

### The bar

| Field | Default | Meaning |
| --- | --- | --- |
| `color` | `white` | `pink`, `blue`, `red`, `green`, `yellow`, `purple` or `white`. |
| `overlay` | `progress` | `progress`, `notched_6`, `notched_10`, `notched_12` or `notched_20`. |
| `darken_sky` | `false` | Dims the sky, as the Wither does. |
| `boss_music` | `false` | Plays the boss music. |
| `fog` | `false` | Adds the boss fog. |

An unrecognised colour or overlay falls back to the default rather than failing the event.

## Stages

Each stage may run actions as it begins and as it ends, and says what advances it:

| Field | Meaning |
| --- | --- |
| `name` | Overrides the bar's title while this stage runs. |
| `wave` | Mobs spawned as the stage begins. See [Waves](#waves). |
| `repeat` | How many times the stage runs before moving on. `0` or less runs it forever. |
| `bar` | Overrides the event's bar while this stage runs — same fields as [the bar](#the-bar). |
| `on_enter` | Actions run as the stage begins. |
| `on_exit` | Actions run as the stage ends, however it ended. |
| `advance_when` | What ends the stage. Also accepted as `until`. |

### advance_when

| Field | Meaning |
| --- | --- |
| `duration` | Ticks the stage lasts. |
| `wave_cleared` | `true` ends the stage once this stage's own wave is gone. |
| `kills` | `{ "entity": "<id>", "count": n }` — entities of that kind that must die inside the radius. |

Declaring **none of them** makes the stage manual: it runs until something fires `advance_event`. That is
how a stage waits on something the format cannot express — a lever pulled, a block placed, a
[pattern](./Patterns.md) completed.

`kills` only counts deaths inside the event's `radius`, so a player farming that mob elsewhere does not
advance the fight. For a wave the stage spawned itself, prefer `wave_cleared` — see below.

## Waves

A stage can spawn its own mobs, scattered around the origin:

```json
{
  "name": "First Wave",
  "wave": {
    "spawns": [
      { "entity": "examplepack:cheese_zombie", "count": 5 },
      { "entity": "examplepack:cheese_husk", "count": 2 }
    ],
    "spread": 12,
    "per_player": 0.5
  },
  "advance_when": { "wave_cleared": true }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `spawns` | — | What the wave is made of. See [Shaping the mobs](#shaping-the-mobs). |
| `spread` | `8` | How far from the origin mobs scatter, in blocks. `0` puts them all on it. |
| `per_player` | `0.0` | Extra mobs per player beyond the first, as a fraction. |
| `spawn_attempts` | `16` | Placements tried per mob before giving up on that one. |
| `interval` | `0` | Ticks between mobs arriving. `0` spawns the wave at once. |
| `track_health` | `false` | Whether the bar follows the wave's health instead of its headcount. |
| `despawn_on_end` | `true` | Whether mobs still alive when the event ends are removed with it. |

Each mob is placed at a random spot within `spread`, on the surface, needing two blocks of clear space.
A mob that finds nowhere in `spawn_attempts` tries is simply not spawned — a wave in a cramped place is
smaller, not stuck.

### Shaping the mobs

A spawn entry is more than an id and a count — armour, stats, effects and a name all belong to it, which
is what makes wave three different from wave one:

```json
{
  "spawns": [
    {
      "entity": "minecraft:zombie",
      "count": 6,
      "name": "Siege Ghoul",
      "name_visible": false,
      "equipment": {
        "head": "minecraft:iron_helmet",
        "mainhand": "examplepack:cheese_cleaver"
      },
      "attributes": {
        "minecraft:max_health": 40,
        "minecraft:movement_speed": 0.3
      },
      "effects": [
        { "effect": "minecraft:fire_resistance", "duration": 12000 }
      ]
    }
  ]
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `entity` | required | The entity to spawn. |
| `count` | `1` | How many, before `per_player` scaling. |
| `name` | — | A name shown above the mob. See [Names](./Names.md). |
| `name_visible` | `false` | Whether the name always shows rather than only when looked at. |
| `equipment` | — | Slot → item id. `head`, `chest`, `legs`, `feet`, `mainhand`, `offhand`, `body`. |
| `attributes` | — | Attribute id → base value. |
| `effects` | — | [Effects](./EffectInstances.md) applied on spawn. |
| `persistent` | `true` | Whether the mob refuses to despawn. |
| `finalize_spawn` | `true` | Whether vanilla gives it the usual random gear and variant for the difficulty. |
| `guard` | `false` | Whether the mob is being **protected** rather than fought. See [Guards](#guards). |

Slot names are forgiving — `helmet`, `chestplate`, `leggings`, `boots`, `main_hand` and `off_hand` all
land where you would expect.

Raising `minecraft:max_health` also heals the mob to it, so a 40-health zombie spawns at 40 rather than
looking half-dead. `finalize_spawn` runs **before** the rest, so anything declared here wins over
vanilla's randomisation; turn it off for a mob that should have exactly what you gave it and nothing
else.

`persistent` defaults on for a reason: a wave mob that wanders off and despawns would otherwise leave a
`wave_cleared` stage that can never finish.

The same block of fields works on the `spawn_entity` [action](../Events.md), so a mob described for a
wave can be summoned anywhere without being described twice.

### wave_cleared vs kills

These look similar and are not:

* `wave_cleared` tracks **the mobs this wave actually spawned**. It ends when they are gone — killed,
  despawned, or removed by a command.
* `kills` counts **deaths of a type** inside the radius, whoever spawned them.

For a wave the stage spawned, `wave_cleared` is almost always what you want. With `kills`, a player
killing naturally-spawned mobs of the same kind nearby advances your siege, which is rarely intended.
`kills` is for the other case: "kill 10 of something that was already there".

### Scaling with players

`per_player` multiplies the declared count by `1 + per_player × (players − 1)`, counting players in
range when the wave spawns. At `0.5`, three players get double the mobs. It never scales below the
declared count.

Players who join after the wave has spawned do not make it bigger — the count is fixed at spawn.

### Trickling them in

`interval` spaces the arrivals out instead of dropping the whole wave at once:

```json
{ "wave": { "spawns": [{ "entity": "minecraft:zombie", "count": 20 }], "interval": 10 } }
```

Twenty zombies, one every half second. The wave's size is fixed when the stage begins, so the bar
measures against the full twenty from the first tick, and the stage cannot finish early just because
the first few died before the rest arrived.

A mob that finds nowhere to stand is dropped from the wave's size too, so a wave that cannot fully
spawn still finishes.

### Repeating a stage

`repeat` runs the same stage again instead of moving on — three escalating waves need three stages, but
three *identical* waves need one:

```json
{ "wave": { "spawns": [{ "entity": "minecraft:zombie", "count": 4 }] },
  "advance_when": { "wave_cleared": true },
  "repeat": 3 }
```

`repeat: 0` (or any negative number) repeats **forever**, until something ends the event. That is the
piece that makes a rift possible — see [Rifts](#rifts).

Each repeat is a fresh run: `on_enter` fires again, the wave spawns again, and the bar resets.

## Rifts

A rift — a tear that opens somewhere, leaks mobs until it is closed, and can be stepped through — needs
no format of its own. It is a [portal](./Portals.md) block, a world event with an endless stage, and the
block's own `on_remove` event tying the two together.

**The rift block** is an ordinary [block](./Blocks.md) that a [portal](./Portals.md) names, so stepping
into it travels. Its `on_remove` closes the event when a player breaks it:

```json
{
  "information": { "name": "Rift", "block_properties": { "collidable": false, "luminance": 10 } },
  "events": {
    "on_remove": [{ "action": "stop_event", "event": "examplepack:rift_breach" }]
  }
}
```

**The event** places the block when it starts, leaks mobs forever, and clears the block when it ends:

```json
{
  "name": "Rift Breach",
  "radius": 32,
  "unique": true,
  "bar": { "color": "purple", "fog": true },
  "stages": [
    {
      "wave": {
        "spawns": [{ "entity": "examplepack:rift_spawn", "count": 3, "persistent": false }],
        "spread": 6,
        "interval": 60
      },
      "advance_when": { "wave_cleared": true },
      "repeat": 0
    }
  ],
  "events": {
    "on_start":  [{ "action": "set_block", "block": "examplepack:rift" }],
    "on_cancel": [{ "action": "set_block", "block": "minecraft:air" }],
    "on_complete": [{ "action": "set_block", "block": "minecraft:air" }]
  }
}
```

**Opening one** is anything that fires `start_event` — a [pattern](./Patterns.md) built by a player, an
item used, or a block's random tick with a `chance` condition for rifts that open on their own.

The pieces each do one thing: the portal handles travel, the event handles the fight, the pattern handles
the summoning, and `on_remove` closes the loop. Note `persistent: false` on the wave — rift spawn should
despawn naturally, unlike the wave mobs a cleared stage depends on.

## Participants

Anyone who has been inside the radius while the event ran is a **participant**, whether or not they are
still there — someone who fought the first wave and died is still owed the reward.

Action lists split by what the action acts on:

* Actions on the **world** — `summon_entity`, `set_block`, `spawn_loot`, `play_sound_at` — run **once**,
  at the event's origin.
* Actions on a **player** — `send_message`, `give_item`, `apply_effect`, `give_experience`,
  `give_experience_levels`, `open_gui` — run **once per participant**.

So the same mechanism gives you announcements and rewards:

```json
{
  "events": {
    "on_start":    [{ "action": "send_message", "message": "The siege begins!" }],
    "on_complete": [
      { "action": "send_message", "message": "The siege is broken." },
      { "action": "give_item", "item": "examplepack:siege_medal" },
      { "action": "spawn_loot", "loot_table": "examplepack:siege_hoard" }
    ]
  }
}
```

Everyone gets the message and the medal; the hoard drops once at the origin.

A participant who has logged out simply misses that action — nothing is held for them.

## Guards

A wave spawn marked `guard: true` is the thing being **protected**, not fought:

```json
{
  "wave": {
    "spawns": [
      { "entity": "minecraft:villager", "guard": true, "name": "Village Elder", "attributes": { "minecraft:max_health": 40 } },
      { "entity": "examplepack:raider", "count": 8 }
    ]
  },
  "advance_when": { "wave_cleared": true }
}
```

Guards differ from ordinary wave mobs in three ways:

* They are **not** part of `wave_cleared` — you do not kill the elder to finish the stage.
* They are **not** scaled by `per_player` — the villager you are protecting does not multiply.
* They are spawned **once for the whole event**, so a `repeat` stage does not stack up villagers.

On their own, guards are just mobs that come along. Pair them with `fail_when.guard_dies` to make
losing them lose the event.

## Failing

`time_limit` gives the event a deadline, and `fail_when` gives it ways to be lost outright:

```json
{
  "time_limit": 6000,
  "fail_when": { "guard_dies": true, "block_broken": true },
  "events": { "on_fail": [{ "action": "send_message", "message": "The elder is dead." }] }
}
```

| Field | Meaning |
| --- | --- |
| `guard_dies` | Every mob marked `guard` has died. An event that spawned none is never lost this way. |
| `block_broken` | The block the event started on has been broken or replaced. |

`block_broken` compares the **block**, not the exact state, so a door opening or a block being
waterlogged is not "broken" — only replacing it is. It pairs with `on_start` placing something: an
altar, a crystal, a beacon that has to survive.

Running out or losing a guard fires **`on_fail`**, which is deliberately not `on_cancel`: being beaten
is a different outcome from everyone walking away, and a pack usually wants to say so differently.

| Ending | Fires | Rewards? |
| --- | --- | --- |
| Last stage finished | `on_complete` | yes |
| `time_limit` ran out, or `fail_when` was met | `on_fail` | no |
| Last player left, or `stop_event` | `on_cancel` | no |

## Rewards

Everyone who was there gets something; the people who did the work get more:

```json
{
  "rewards": {
    "threshold": 3,
    "participation": [
      { "action": "give_item", "item": "minecraft:emerald", "count": 2 },
      { "action": "send_message", "message": "Thanks for standing with us." }
    ],
    "contribution": [{ "action": "give_item", "item": "examplepack:siege_medal" }],
    "top": [{ "action": "give_item", "item": "examplepack:hero_banner" }]
  }
}
```

| Field | Default | Meaning |
| --- | --- | --- |
| `participation` | — | Actions for **every** participant, however little they did. |
| `contribution` | — | Actions for participants who reached `threshold`. |
| `top` | — | Actions for the single highest contributor. Nothing if nobody contributed. |
| `threshold` | `1` | Kills of the event's own mobs needed to count as a contributor. |

Rewards are given on completion only — a lost event pays nothing beyond whatever `on_fail` chooses.

### What counts as contribution

**Kills of the event's own mobs, credited to whoever landed the killing blow.** That is all the death
hook can see.

It follows that someone healing, tanking, building barricades or reviving people contributes nothing
*measurable*, and that is exactly why `participation` exists and is listed first. A support player who
never lands a hit is still a participant and still gets paid; the kill count is a bonus on top, not the
whole reward. Weighting rewards purely by kills would quietly punish the people doing the least
countable work.

The tiers stack: the top contributor gets `participation`, `contribution` **and** `top`.

## Progress

The bar shows the **current stage's** progress, not the whole event's: ticks elapsed against `duration`,
mobs cleared against the wave's size, or kills against `count`. A manual stage sits at zero, since there
is nothing to measure.

`track_health` changes what a wave stage measures — the health left across the wave rather than how many
of it are left. That is what a bar for a single boss wants: a one-mob wave otherwise sits at zero until
the mob dies and then jumps to full.

```json
{
  "wave": { "spawns": [{ "entity": "examplepack:cheese_wyrm" }], "track_health": true },
  "advance_when": { "wave_cleared": true }
}
```

For a whole-event bar instead of a per-stage one, use one long stage and drive it with `advance_event`.

## Actions

Three [event actions](../Events.md) drive events, usable from anywhere that has a position:

| Action | Meaning |
| --- | --- |
| `start_event` | Starts the event at this position. |
| `advance_event` | Ends the current stage of the nearest running copy, beginning the next. |
| `stop_event` | Cancels the nearest running copy, running `on_cancel`. |

All three take an `event` id:

```json
{ "action": "start_event", "event": "examplepack:cheese_siege" }
```

`advance_event` and `stop_event` act on the **nearest** running copy in the same dimension, so two
sieges at two villages do not interfere.

## Several at once

The same event can run in several places at the same time — each `start_event` makes its own instance,
with its own stage, its own progress and its own bar. That is what you want for a siege that can happen
at two villages.

It is not what you want for a world boss. `"unique": true` allows only one copy per dimension: a second
`start_event` while one is running is ignored rather than stacking a second bar on the first.

Two instances started close together still overlap, `unique` or not, since it is a per-dimension rule
rather than a per-position one.

`cooldown` covers the other half of that problem — `unique` stops two running at once, but nothing stops
a player re-triggering the moment one ends. A cooldown in ticks, counted per dimension, makes the event
unavailable for a while after it finishes however it finished.

Both are forgotten when the server stops, along with the running events themselves.

## Events are not saved

Running events live only as long as the server does. A fight interrupted by a restart is simply over —
nothing is written to the save, and nothing resumes. That is nearly always what a pack wants rather than
a half-finished siege waking up in an empty world, but it does mean an event should not be the only way
to reach something important.

## See also

* [Patterns](./Patterns.md) — the natural way to start an event.
* [Portals](./Portals.md) — the travel half of a [rift](#rifts).
* [Events](../Events.md) — the action list format, and the three event actions.
* [Entities](./Entities.md) — what a fight is made of.
* [Names](./Names.md) — the bar's title.
