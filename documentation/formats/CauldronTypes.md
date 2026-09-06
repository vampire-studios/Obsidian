# Cauldron Types

An item that changes a cauldron when used on it — the way a water bucket fills a cauldron. One file
says which item, what the cauldron becomes, and what it sounds like.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/cauldron_type/cheese.json
```

The file name is the id, unless the file sets `name` itself.

## Example

```json
{
  "item": "examplepack:cheese_bucket",
  "blockstate": {
    "block": "examplepack:cheese_cauldron",
    "properties": { "level": "3" }
  },
  "sound_event": "minecraft:item.bucket.empty"
}
```

Right-clicking a cauldron with a cheese bucket turns it into a cheese cauldron at level 3, plays the
sound, and leaves the player holding an empty bucket.

## Fields

| Field | Required | Meaning |
| --- | --- | --- |
| `name` | no | The registry id. Defaults to the file name. |
| `item` | yes | The item that triggers the interaction when used on a cauldron. |
| `blockstate` | yes | The block and state the cauldron becomes. |
| `sound_event` | no | The sound played on success. Defaults to `minecraft:item.bucket.empty`. |

### blockstate

| Field | Meaning |
| --- | --- |
| `block` | The block id the cauldron turns into. |
| `properties` | State values to set on it, as strings — `{ "level": "3" }`, not `{ "level": 3 }`. |

Properties are resolved once when the pack loads. A property the block does not have, or a value it does
not accept, is reported in the log and skipped; the rest of the state still applies.

## Which cauldrons it applies to

The interaction is registered against **every** cauldron state — empty, water, lava and powder snow — so
the item works whatever is already in the cauldron. There is currently no way to restrict it to one.

Because it is keyed by item, one item can only have one cauldron interaction; a second definition naming
the same item replaces the first, and vanilla items can be overridden this way.

## Emptying behaviour

The interaction reuses vanilla's bucket-emptying path, so it behaves like emptying a bucket: the held
stack is replaced with its remainder (a bucket becomes an empty bucket), the block state is set, and the
sound plays. Creative mode does not consume the item.

That also means it is aimed at bucket-shaped items. An item with no remainder still works — it simply is
not consumed.

## See also

* [Blocks](./Blocks.md) — defining the cauldron block itself.
* [Fluids](./Fluids.md) — the liquid the cauldron is meant to hold.
* [Events](../Events.md) — reacting to a click on a block more generally.
