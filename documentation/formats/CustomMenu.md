# Menu Config

A chest-style menu, and optionally a weighted loot pool rolled when it opens — which is how you build
crates and lootboxes.

The same format is read from three places:

* An [item](./Items.md) with `"type": "CUSTOM_MENU"`, as its `menu_config`. Opens when the item is used.
* A [block](./Blocks.md), as its `menu_config`. Opens when an [`open_crate`](../Events.md#open_crate)
  action fires on it.
* An `open_crate` action, as an inline `menu` object.

## Minimal form

An empty menu, nine slots wide and three rows tall:

```json
{
  "type": "CUSTOM_MENU",
  "menu_config": {
    "title": "Storage",
    "rows": 3
  }
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `title` | no | Menu title. Parsed with the usual text tags, so `<gold>` and friends work. Defaults to `Custom Menu`. |
| `rows` | no | 1–9. Defaults to 3. Rows 7–9 use menu types this mod registers; everything below is vanilla. |
| `loot_pool` | no | Turns the menu into a crate. See below. |

A value outside 1–9 is **clamped rather than rejected**, so a typo opens a smaller menu instead of
throwing while the player is holding the item.

Without a `loot_pool` the menu is an ordinary, fully interactive chest menu that is **not saved** — it is
backed by a fresh container each time, so anything left in it when the screen closes is gone.

## Crates

Add a `loot_pool` and the item becomes a crate: using it rolls the pool once, consumes the item, and
either shows the results or hands them straight over.

```json
{
  "type": "CUSTOM_MENU",
  "menu_config": {
    "title": "<gold>Treasure Crate",
    "rows": 1,
    "loot_pool": {
      "mode": "PREVIEW",
      "rolls": 3,
      "unique": true,
      "consume": true,
      "entries": [
        { "item": "minecraft:diamond", "weight": 1, "min_count": 1, "max_count": 3 },
        { "item": "minecraft:gold_ingot", "weight": 5, "min_count": 4, "max_count": 12 },
        { "item": "minecraft:iron_ingot", "weight": 20, "min_count": 8, "max_count": 16 },
        {
          "item": { "id": "minecraft:netherite_sword", "count": 1 },
          "weight": 1,
          "chance": 0.25
        }
      ]
    }
  }
}
```

| Field | Required | Meaning |
| --- | --- | --- |
| `mode` | no | `PREVIEW` (default) or `INSTANT`. Uppercase, matched exactly — an unrecognised value falls back to `PREVIEW`. |
| `rolls` | no | How many times to draw from the pool. Defaults to 1. |
| `unique` | no | Default `true`. A roll cannot pick an entry an earlier roll already picked. |
| `consume` | no | Default `true`. Shrinks the used stack by one, unless the player is in creative. |
| `entries` | yes | The pool. An empty or missing list leaves the menu as a plain chest menu. |

### Modes

* **`PREVIEW`** — opens a read-only menu holding the rolled rewards, and gives them to the player when
  the menu closes. **Every click is rejected**, including clicks in the player's own inventory, because
  a shift-click or drag could otherwise push items into the reward slots. Rewards that do not fit in the
  inventory are dropped at the player's feet. If the player disconnects with the menu open the rewards
  are still granted.
* **`INSTANT`** — no menu at all. The rewards go straight to the inventory, and overflow is dropped.

In `PREVIEW` mode the menu needs enough slots to show the rewards: more rolls than `rows * 9` logs a
warning and the extras are discarded.

### Entries

| Field | Required | Meaning |
| --- | --- | --- |
| `item` | yes | An [item stack](./ItemStack.md) — a bare id string, or the object form with `count` and `components`. |
| `weight` | no | Relative chance of being drawn. Defaults to 1. Entries with a weight of 0 or less are ignored. |
| `chance` | no | 0.0–1.0, applied *after* the entry is drawn. Defaults to 1.0. |
| `min_count` / `max_count` | no | Random stack size. Without either one, the stack's own `count` is used. Clamped to the item's max stack size. |

Weights are relative to the other entries in the pool, not out of 100 — in the example above iron is
drawn 20 times as often as diamond.

`chance` and `weight` do different jobs and stack: `weight` decides *which* entry a roll draws, `chance`
decides whether that draw actually produces anything. A drawn entry that fails its `chance` roll yields
nothing for that roll — it does not re-draw. That is the way to make a rare entry that sometimes leaves
a slot empty, rather than one that simply comes up less often.

## Crate blocks and keys

Put the pool on the **block**, not on the key. One key then opens many crates, and re-tuning a crate's
rewards is one file rather than every key that can open it.

The block carries the crate; the key carries only the permission to open it:

```json
// block/vault.json
{
  "menu_config": {
    "title": "Vault",
    "rows": 1,
    "loot_pool": { "rolls": 2, "consume": false, "entries": [ ... ] }
  }
}
```

```json
// item/keycard.json
{
  "events": {
    "use_on": [
      {
        "action": "open_crate",
        "conditions": [
          { "condition": "block_is", "block": "examplepack:vault" },
          { "condition": "has_item", "item": "examplepack:keycard", "consume": true,
            "message": "Access denied" }
        ]
      }
    ]
  }
}
```

Note `"consume": false` on the block's pool. **A crate read off a block never touches what the player is
holding** — consuming the key is the `has_item` condition's job, which is why it can also say why it
failed. `consume` on a pool only means anything for a crate item eating itself, or an inline `menu` on
the action.

Reading the crate from the block also means the key needs no knowledge of the rewards, so the same
keycard works on a wooden crate and a netherite one.

## Gotchas

* An entry whose `item` fails to parse decodes to an **empty stack** and is silently skipped, per the
  usual [item stack](./ItemStack.md) behaviour.
* `unique: true` with more `rolls` than entries stops early — a roll cannot draw from an empty pool.
* If the item also sets `use_actions.right_click_actions` to `open_gui` or `open_url`, that screen wins
  and `menu_config` is skipped; the two do not both open.
* A crate opened from a block ignores its pool's `consume`. Only an item crate, or an inline `menu` on an
  `open_crate` action, can consume the held stack.

## See also

* [Items](./Items.md) — the item format itself.
* [Item Types](./ItemTypes.md) — where `CUSTOM_MENU` is listed.
* [Item Stacks](./ItemStack.md) — the `item` field of an entry.
