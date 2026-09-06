# Sound Playing Items

Music discs and goat horns — items whose whole purpose is to play a sound defined in a data pack.

## Where it goes

```
obsidian_addons/ExamplePack/content/examplepack/item/sound_playing_item/cheese_disc.json
```

The file name is the item id.

## Example

```json
{
  "information": {
    "name": "Music Disc",
    "item_properties": { "max_count": 1, "rarity": "rare" }
  },
  "sound_type": "music_disc",
  "sound": "examplepack:cheese_theme"
}
```

## Fields

A sound playing item is an [item](./Items.md) first — `information`, `components`, `rendering`, `lore`
and `events` all work as they do there. On top of that:

| Field | Required | Meaning |
| --- | --- | --- |
| `sound_type` | yes | `music_disc` or `goat_horn`. |
| `sound` | yes | The data-driven entry to play. What it points at depends on `sound_type`. |

## Music discs

`sound` is a jukebox song key, and the song is defined in the pack's
[`data` directory](../PackStructure.md#assets-and-data):

```
obsidian_addons/ExamplePack/data/examplepack/jukebox_song/cheese_theme.json
```

```json
{
  "comparator_output": 12,
  "description": { "translate": "jukebox_song.examplepack.cheese_theme" },
  "length_in_seconds": 174,
  "sound_event": "examplepack:music_disc.cheese_theme"
}
```

The `sound_event` inside it resolves from `assets/<namespace>/sounds.json`, the same as any other sound.

## Goat horns

`sound` is an instrument key, defined the same way:

```
obsidian_addons/ExamplePack/data/examplepack/instrument/cheese_horn.json
```

```json
{
  "sound_event": "examplepack:instrument.cheese_horn",
  "use_duration": 7,
  "range": 256,
  "description": { "translate": "instrument.examplepack.cheese_horn" }
}
```

A vanilla instrument works just as well:

```json
{ "sound_type": "goat_horn", "sound": "minecraft:ponder_goat_horn" }
```

## Why the sound lives in `data`

Jukebox songs and instruments are vanilla registry entries loaded from a data pack, not Obsidian
formats. The item file names one; the pack's `data` directory defines it. A `sound` pointing at an entry
that does not exist registers the item and fails at use time, not at load.

## See also

* [Items](./Items.md) — everything a sound playing item inherits.
* [Pack Structure](../PackStructure.md#assets-and-data) — where the audio itself is declared.
* [Pack Structure](../PackStructure.md#assets-and-data) — the `data` and `assets` directories.
