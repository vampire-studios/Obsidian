package io.github.vampirestudios.obsidian.client;

public class JsonTemplates {

    public static final String DOOR_BLOCKSTATE = """
                {
                    "variants": {
                        "facing=east,half=lower,hinge=left,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom" },
                        "facing=south,half=lower,hinge=left,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 90 },
                        "facing=west,half=lower,hinge=left,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 180 },
                        "facing=north,half=lower,hinge=left,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 270 },
                        "facing=east,half=lower,hinge=right,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge" },
                        "facing=south,half=lower,hinge=right,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 90 },
                        "facing=west,half=lower,hinge=right,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 180 },
                        "facing=north,half=lower,hinge=right,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 270 },
                        "facing=east,half=lower,hinge=left,open=true":\t{ "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 90 },
                        "facing=south,half=lower,hinge=left,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 180 },
                        "facing=west,half=lower,hinge=left,open=true":\t{ "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge", "y": 270 },
                        "facing=north,half=lower,hinge=left,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom_hinge" },
                        "facing=east,half=lower,hinge=right,open=true":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 270 },
                        "facing=south,half=lower,hinge=right,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom" },
                        "facing=west,half=lower,hinge=right,open=true":  { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 90 },
                        "facing=north,half=lower,hinge=right,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 180 },
                        "facing=east,half=upper,hinge=left,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top" },
                        "facing=south,half=upper,hinge=left,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 90 },
                        "facing=west,half=upper,hinge=left,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 180 },
                        "facing=north,half=upper,hinge=left,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 270 },
                        "facing=east,half=upper,hinge=right,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge" },
                        "facing=south,half=upper,hinge=right,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 90 },
                        "facing=west,half=upper,hinge=right,open=false":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 180 },
                        "facing=north,half=upper,hinge=right,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 270 },
                        "facing=east,half=upper,hinge=left,open=true":\t{ "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 90 },
                        "facing=south,half=upper,hinge=left,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 180 },
                        "facing=west,half=upper,hinge=left,open=true":\t{ "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge", "y": 270 },
                        "facing=north,half=upper,hinge=left,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_top_hinge" },
                        "facing=east,half=upper,hinge=right,open=true":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 270 },
                        "facing=south,half=upper,hinge=right,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_top" },
                        "facing=west,half=upper,hinge=right,open=true":  { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 90 },
                        "facing=north,half=upper,hinge=right,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 180 }
                    }
                }""";

    public static final String TRAPDOOR_BLOCKSTATE = """
                {
                    "variants": {
                        "facing=north,half=bottom,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom" },
                        "facing=south,half=bottom,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 180 },
                        "facing=east,half=bottom,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 90 },
                        "facing=west,half=bottom,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_bottom", "y": 270 },
                        "facing=north,half=top,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top" },
                        "facing=south,half=top,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 180 },
                        "facing=east,half=top,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 90 },
                        "facing=west,half=top,open=false": { "model": "%MOD_ID%:block/%BLOCK_ID%_top", "y": 270 },
                        "facing=north,half=bottom,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open" },
                        "facing=south,half=bottom,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "y": 180 },
                        "facing=east,half=bottom,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "y": 90 },
                        "facing=west,half=bottom,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "y": 270 },
                        "facing=north,half=top,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "x": 180, "y": 180 },
                        "facing=south,half=top,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "x": 180, "y": 0 },
                        "facing=east,half=top,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "x": 180, "y": 270 },
                        "facing=west,half=top,open=true": { "model": "%MOD_ID%:block/%BLOCK_ID%_open", "x": 180, "y": 90 }
                    }
                }
                """;

    public static final String HORIZONTAL_FACING_ON_OFF_BLOCKSTATE = """
            {
              "variants": {
                "facing=east,lit=false": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%",
                  "y": 90
                },
                "facing=east,lit=true": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%_on",
                  "y": 90
                },
                "facing=north,lit=false": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%"
                },
                "facing=north,lit=true": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%_on"
                },
                "facing=south,lit=false": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%",
                  "y": 180
                },
                "facing=south,lit=true": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%_on",
                  "y": 180
                },
                "facing=west,lit=false": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%",
                  "y": 270
                },
                "facing=west,lit=true": {
                  "model": "%MOD_ID%:block/%BLOCK_ID%_on",
                  "y": 270
                }
              }
            }""";

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
