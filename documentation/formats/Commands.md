# Custom Commands

A command built out of vanilla commands. Each node in the tree can run a list of commands, with the
arguments the player typed substituted into them.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/command/home.json
```

The file name is the command name, unless the file sets `name` itself. Only the **path** of the id is
used, so `examplepack:home` registers `/home`.

## Example

```json
{
  "aliases": ["h"],
  "op_level": 0,
  "executes": ["say I have no home"],
  "arguments": {
    "target": {
      "type": "player",
      "executes": ["tp @s {target}"]
    }
  }
}
```

`/home` says the message; `/home Steve` teleports you to Steve.

## Node fields

Every node — the root, a literal, an argument — takes the same four fields:

| Field | Meaning |
| --- | --- |
| `executes` | Commands to run when the tree ends here. Run in order, as the player, with `{name}` substituted. |
| `arguments` | Child argument nodes, keyed by argument name. See [Arguments](#arguments). |
| `literals` | Child literal nodes, keyed by the literal word. |
| `op_level` | Permission level `0`–`4` required to reach this node. |

The root node takes two more:

| Field | Default | Meaning |
| --- | --- | --- |
| `name` | file name | The command's id. Only its path becomes the command word. |
| `aliases` | — | Extra words that redirect to this command. `/h` above. |
| `dedicatedOnly` | `false` | Registers the command only on a dedicated server. |

## Permission levels

| `op_level` | Who |
| --- | --- |
| `0` | Everyone. |
| `1` | Moderators. |
| `2` | Gamemasters — the level most vanilla cheats sit at. |
| `3` | Admins. |
| `4` | Owners. |

`op_level` gates *reaching* the node, and also raises the permission the `executes` commands run with —
an `op_level: 2` node can run `/tp` for a player who could not type it themselves. That is the point of
the format, and also the thing to be careful with: anything a node can execute, any player who can reach
that node can cause.

An `op_level` outside `0`–`4` throws while the command tree is built.

## Arguments

Each key under `arguments` is the argument's name; its `type` is a vanilla argument type id.

```json
{
  "arguments": {
    "user": {
      "type": "player",
      "executes": ["tp @s {user}"],
      "arguments": {
        "target": {
          "type": "player",
          "executes": ["tp {user} {target}"]
        }
      }
    }
  }
}
```

Nesting builds the tree: `/cmd <user>` runs the first `executes`, `/cmd <user> <target>` the second.
An argument name is in scope for its own node and every node below it.

Numeric types take bounds:

| Field | Meaning |
| --- | --- |
| `min`, `max` | Bounds for the `*_min` and `*_min_max` numeric types. |
| `center_integers` | For `vec2_centered_integers` and `vec3_centered_integers`. |

### Available types

Text and primitives: `brigadier:string`, `brigadier:word`, `brigadier:greedy`, `brigadier:bool`,
`brigadier:integer`, `brigadier:double`, `brigadier:float`, `brigadier:long`, each numeric one also as
`_min` and `_min_max`, plus `component`, `message`, `int_range`, `float_range`, `operation`.

Targets: `entity`, `entities`, `player`, `players`, `game_profile`, `uuid`, `team`, `entity_anchor`,
`game_mode`.

World: `block_pos`, `column_pos`, `vec2`, `vec3`, `vec2_centered_integers`, `vec3_centered_integers`,
`rotation`, `swizzle`, `heightmap`, `dimension`, `angel`, `time`.

Blocks and items: `block_state`, `block_predicate`, `item`, `item_predicate`, `item_slot`,
`nbt_compound_tag`, `nbt_tag`, `nbt_path`.

Registries: `resource_location`, `resource_*`, `resource_or_tag_*`, `resource_key_*` and
`resource_or_tag_key_*` for `block`, `item`, `entity`, `effect`, `enchantment`, `biome`, `feature` and
`structure`.

Other: `objective`, `objective_criteria`, `scoreboard_slot`, `score_holder`, `score_holders`,
`particle_effect`, `team_color`, `hex_color`, `function`, `template_mirror`, `template_rotation`,
`test_class`, `test_argument`, `mod_id`.

## What `{name}` substitutes

`{name}` is replaced by **the text the player typed** for that argument, exactly as written. Every
argument type works, including `block_pos`, `entity` and the numeric types:

```json
{
  "arguments": {
    "where": {
      "type": "block_pos",
      "executes": ["setblock {where} minecraft:stone"]
    }
  }
}
```

`/cmd ~ ~1 ~` runs `setblock ~ ~1 ~ minecraft:stone` — relative coordinates, selectors and quoted
strings all survive, because the substring is taken from the command line rather than from the parsed
value.

The argument still has to *parse*, so an invalid position is rejected before anything runs, with
vanilla's error and tab-completion intact.

Only arguments in scope are substituted — an argument's own node and everything below it. A `{name}`
naming an argument that is not in scope is left in the command as literal text.

## See also

* [Events](../Events.md) — running actions without a command.
* [Pack Structure](../PackStructure.md) — where command files live.
