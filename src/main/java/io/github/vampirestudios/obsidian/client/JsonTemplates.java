package io.github.vampirestudios.obsidian.client;

public class JsonTemplates {

    public static final String STAIRS_BLOCKSTATE = """
            {
                "variants": {
                    "facing=east,half=bottom,shape=straight":  { "model": "%MOD_ID%:block/%BLOCK_ID%" },
                    "facing=west,half=bottom,shape=straight":  { "model": "%MOD_ID%:block/%BLOCK_ID%", "y": 180, "uvlock": true },
                    "facing=south,half=bottom,shape=straight": { "model": "%MOD_ID%:block/%BLOCK_ID%", "y": 90, "uvlock": true },
                    "facing=north,half=bottom,shape=straight": { "model": "%MOD_ID%:block/%BLOCK_ID%", "y": 270, "uvlock": true },
                    "facing=east,half=bottom,shape=outer_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer" },
                    "facing=west,half=bottom,shape=outer_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 180, "uvlock": true },
                    "facing=south,half=bottom,shape=outer_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 90, "uvlock": true },
                    "facing=north,half=bottom,shape=outer_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 270, "uvlock": true },
                    "facing=east,half=bottom,shape=outer_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 270, "uvlock": true },
                    "facing=west,half=bottom,shape=outer_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 90, "uvlock": true },
                    "facing=south,half=bottom,shape=outer_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer" },
                    "facing=north,half=bottom,shape=outer_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "y": 180, "uvlock": true },
                    "facing=east,half=bottom,shape=inner_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner" },
                    "facing=west,half=bottom,shape=inner_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 180, "uvlock": true },
                    "facing=south,half=bottom,shape=inner_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 90, "uvlock": true },
                    "facing=north,half=bottom,shape=inner_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 270, "uvlock": true },
                    "facing=east,half=bottom,shape=inner_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 270, "uvlock": true },
                    "facing=west,half=bottom,shape=inner_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 90, "uvlock": true },
                    "facing=south,half=bottom,shape=inner_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner" },
                    "facing=north,half=bottom,shape=inner_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "y": 180, "uvlock": true },
                    "facing=east,half=top,shape=straight":  { "model": "%MOD_ID%:block/%BLOCK_ID%", "x": 180, "uvlock": true },
                    "facing=west,half=top,shape=straight":  { "model": "%MOD_ID%:block/%BLOCK_ID%", "x": 180, "y": 180, "uvlock": true },
                    "facing=south,half=top,shape=straight": { "model": "%MOD_ID%:block/%BLOCK_ID%", "x": 180, "y": 90, "uvlock": true },
                    "facing=north,half=top,shape=straight": { "model": "%MOD_ID%:block/%BLOCK_ID%", "x": 180, "y": 270, "uvlock": true },
                    "facing=east,half=top,shape=outer_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 90, "uvlock": true },
                    "facing=west,half=top,shape=outer_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 270, "uvlock": true },
                    "facing=south,half=top,shape=outer_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 180, "uvlock": true },
                    "facing=north,half=top,shape=outer_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "uvlock": true },
                    "facing=east,half=top,shape=outer_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "uvlock": true },
                    "facing=west,half=top,shape=outer_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 180, "uvlock": true },
                    "facing=south,half=top,shape=outer_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 90, "uvlock": true },
                    "facing=north,half=top,shape=outer_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_outer", "x": 180, "y": 270, "uvlock": true },
                    "facing=east,half=top,shape=inner_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 90, "uvlock": true },
                    "facing=west,half=top,shape=inner_right":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 270, "uvlock": true },
                    "facing=south,half=top,shape=inner_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 180, "uvlock": true },
                    "facing=north,half=top,shape=inner_right": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "uvlock": true },
                    "facing=east,half=top,shape=inner_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "uvlock": true },
                    "facing=west,half=top,shape=inner_left":  { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 180, "uvlock": true },
                    "facing=south,half=top,shape=inner_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 90, "uvlock": true },
                    "facing=north,half=top,shape=inner_left": { "model": "%MOD_ID%:block/%BLOCK_ID%_inner", "x": 180, "y": 270, "uvlock": true }
                }
            }
            """;

    public static final String WALL_BLOCKSTATE = """
            {
               "multipart": [
                 {
                   "when": { "up": "true" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_post" }
                 },
                 {
                   "when": { "north": "low" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side", "uvlock": true }
                 },
                 {
                   "when": { "east": "low" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side", "y": 90, "uvlock": true }
                 },
                 {
                   "when": { "south": "low" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side", "y": 180, "uvlock": true }
                 },
                 {
                   "when": { "west": "low" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side", "y": 270, "uvlock": true }
                 },
                 {
                   "when": { "north": "tall" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side_tall", "uvlock": true }
                 },
                 {
                   "when": { "east": "tall" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side_tall", "y": 90, "uvlock": true }
                 },
                 {
                   "when": { "south": "tall" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side_tall", "y": 180, "uvlock": true }
                 },
                 {
                   "when": { "west": "tall" },
                   "apply": { "model": "%MOD_ID%:block/%BLOCK_ID%_side_tall", "y": 270, "uvlock": true }
                 }
               ]
             }
            """;

}
