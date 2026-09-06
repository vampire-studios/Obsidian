# Obsidian Documentation

Obsidian turns data files into game content. Write JSON, drop it in a folder, and the mod registers
items, blocks, fluids and entities from it — no Java, no build step, no restart of your workflow beyond
relaunching the game.

```
content/examplepack/item/cheese_stick.json   ->   examplepack:cheese_stick
```

## Start here

| | |
| --- | --- |
| **[Getting Started](./GettingStarted.md)** | Build a working pack from an empty folder, in about ten minutes. |
| **[Pack Structure](./PackStructure.md)** | Where files go, what `addon.info.json` means, and the directory each format is read from. |
| **[Events](./Events.md)** | Making items and blocks *do* things — the shared event and action reference. |

## Formats

### Items

| Page | What it defines |
| --- | --- |
| [Items](./formats/Items.md) | The item itself: name, settings, components, models, events. |
| [Item Settings](./formats/ItemSettings.md) | Stack size, durability, rarity, fuel, creative tab. Inline or shared. |
| [Item Types](./formats/ItemTypes.md) | The handful of special item behaviours (`SHEARS`, `BUNDLE`, `CUSTOM_MENU`). |
| [Item Tiers](./formats/ItemTiers.md) | Tool materials — durability, mining speed, attack damage. |
| [Weapons](./formats/Weapons.md) | Swords, maces, spears, and Obsidian's own melee shapes. |
| [Shields](./formats/Shields.md) | Blocking items — cooldown, repair, sounds, banners. |
| [Projectiles](./formats/Projectiles.md) | Thrown items and custom arrows — flight, trails, impact events. |
| [Armor](./formats/Armor.md) | Helmets, chestplates, leggings, boots and animal barding. |
| [Armor Materials](./formats/ArmorMaterials.md) | Armor stats shared by a set of pieces. |
| [Elytras](./formats/Elytras.md) | Gliders, and their wing texture. |
| [Cosmetics](./formats/Cosmetics.md) | Worn items with no stats. |
| [Sound Playing Items](./formats/SoundPlayingItems.md) | Music discs and goat horns. |
| [Food](./formats/Food.md) | Food components and edible items. |
| [Creative Tabs](./formats/ItemGroups.md) | Tabs in the creative inventory. |

### Blocks

| Page | What it defines |
| --- | --- |
| [Blocks](./formats/Blocks.md) | The block itself, its variants, and its events. |
| [Block Types](./formats/BlockTypes.md) | Which vanilla-shaped block to build — stairs, slab, door, plant, … |
| [Block Settings](./formats/BlockSettings.md) | Hardness, sounds, light, piston behaviour. Inline or shared. |
| [Block Set and Wood Types](./formats/WoodTypes.md) | Door, button and sign behaviour and sounds. |
| [Voxel Shapes](./formats/VoxelShapes.md) | Collision box, selection outline, and face culling. |
| [Chisel Mappings](./formats/ChiselMappings.md) | Tools that convert one block into another. |

### World

| Page | What it defines |
| --- | --- |
| [Biome Modifications](./formats/BiomeModifications.md) | Add, remove, replace, or override biome features, spawns, weather and environment attributes. |
| [Entities](./formats/Entities.md) | Mobs, as components, states and events. |
| [Villagers](./formats/Villagers.md) | Villager professions, their job sites and trades, and biome types. |
| [Fluids](./formats/Fluids.md) | Custom liquids. |
| [Portals](./formats/Portals.md) | Frames that link two dimensions. |
| [Patterns](./formats/Patterns.md) | Block shapes that do something once built and activated. |
| [World Events](./formats/WorldEvents.md) | Staged events with a boss bar. |
| [Enchantments](./formats/Enchantments.md) | *Not currently loaded — use a vanilla data pack.* |

### Gameplay

| Page | What it defines |
| --- | --- |
| [Status Effects](./formats/StatusEffects.md) | Potion effects and the attributes they change. |
| [Potions](./formats/Potions.md) | Brewable potions. |
| [Fuel Sources](./formats/FuelSources.md) | What a furnace and a brewing stand burn. |
| [Custom Commands](./formats/Commands.md) | Commands built out of vanilla commands. |
| [Particles](./formats/Particles.md) | Custom particle types. |
| [Cauldron Types](./formats/CauldronTypes.md) | Items that change a cauldron when used on it. |

### Shared building blocks

Small formats that appear **inside** other files rather than in files of their own.

| Page | Used by |
| --- | --- |
| [Names](./formats/Names.md) | Every `name` field. |
| [Item Stacks](./formats/ItemStack.md) | Anywhere an item with a count is needed. |
| [Ingredients](./formats/Ingredient.md) | Conversion items, and anything matching an item. |
| [Effect Instances](./formats/EffectInstances.md) | Food effects, `apply_effect` actions. |
| [Attribute Modifiers](./formats/AttributeModifiers.md) | Item components, `modify_attribute` actions. |

## Also worth knowing

* [Feature list](./Features.md) — what works, what is half-built, what is planned.
* Obsidian loads around thirty formats. The ones above have pages; the rest are listed with their
  directories in [Pack Structure](./PackStructure.md#content-directories).

This branch targets **Minecraft 26.3**. Other Minecraft versions live on other branches.
