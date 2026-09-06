# Obsidian

A Fabric library mod that adds a Bedrock-like addon system to Java Edition: items, blocks, fluids,
entities and more, defined in data files instead of code.

This branch targets **Minecraft 26.3**.

## Documentation

* [Documentation index](documentation/README.md) — every format, grouped
* [Getting Started](documentation/GettingStarted.md) — a working pack from an empty folder
* [Pack Structure](documentation/PackStructure.md) — pack layout, `addon.info.json`, and the directory
  each format is read from
* [Events](documentation/Events.md) — making items and blocks do things
* [Feature list](documentation/Features.md) — what works, what is half-built, what is planned

## Building

```bash
./gradlew build
```

The built jar lands in `build/libs`.

## Links

* [Homepage](https://vampirestudios.netlify.app/pages/obsidian.html)
* [Issues](https://github.com/vampire-studios/Obsidian/issues)

## License

See [LICENSE](LICENSE) — BSD 3-Clause. (`fabric.mod.json` currently declares MIT; one of the two needs
correcting.)
