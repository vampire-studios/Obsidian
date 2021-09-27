package io.github.vampirestudios.obsidian.client;

public class JsonTemplates {

    public static final String DOOR_BLOCKSTATE = "{\n" +
            "                    \"variants\": {\n" +
            "                        \"facing=east,half=lower,hinge=left,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\" },\n" +
            "                        \"facing=south,half=lower,hinge=left,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 90 },\n" +
            "                        \"facing=west,half=lower,hinge=left,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 180 },\n" +
            "                        \"facing=north,half=lower,hinge=left,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 270 },\n" +
            "                        \"facing=east,half=lower,hinge=right,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\" },\n" +
            "                        \"facing=south,half=lower,hinge=right,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 90 },\n" +
            "                        \"facing=west,half=lower,hinge=right,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 180 },\n" +
            "                        \"facing=north,half=lower,hinge=right,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 270 },\n" +
            "                        \"facing=east,half=lower,hinge=left,open=true\":\t{ \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 90 },\n" +
            "                        \"facing=south,half=lower,hinge=left,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 180 },\n" +
            "                        \"facing=west,half=lower,hinge=left,open=true\":\t{ \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\", \"y\": 270 },\n" +
            "                        \"facing=north,half=lower,hinge=left,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom_hinge\" },\n" +
            "                        \"facing=east,half=lower,hinge=right,open=true\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 270 },\n" +
            "                        \"facing=south,half=lower,hinge=right,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\" },\n" +
            "                        \"facing=west,half=lower,hinge=right,open=true\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 90 },\n" +
            "                        \"facing=north,half=lower,hinge=right,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 180 },\n" +
            "                        \"facing=east,half=upper,hinge=left,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\" },\n" +
            "                        \"facing=south,half=upper,hinge=left,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 90 },\n" +
            "                        \"facing=west,half=upper,hinge=left,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 180 },\n" +
            "                        \"facing=north,half=upper,hinge=left,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 270 },\n" +
            "                        \"facing=east,half=upper,hinge=right,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\" },\n" +
            "                        \"facing=south,half=upper,hinge=right,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 90 },\n" +
            "                        \"facing=west,half=upper,hinge=right,open=false\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 180 },\n" +
            "                        \"facing=north,half=upper,hinge=right,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 270 },\n" +
            "                        \"facing=east,half=upper,hinge=left,open=true\":\t{ \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 90 },\n" +
            "                        \"facing=south,half=upper,hinge=left,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 180 },\n" +
            "                        \"facing=west,half=upper,hinge=left,open=true\":\t{ \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\", \"y\": 270 },\n" +
            "                        \"facing=north,half=upper,hinge=left,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top_hinge\" },\n" +
            "                        \"facing=east,half=upper,hinge=right,open=true\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 270 },\n" +
            "                        \"facing=south,half=upper,hinge=right,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\" },\n" +
            "                        \"facing=west,half=upper,hinge=right,open=true\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 90 },\n" +
            "                        \"facing=north,half=upper,hinge=right,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 180 }\n" +
            "                    }\n" +
            "                }";

    public static final String TRAPDOOR_BLOCKSTATE = "{\n" +
            "                    \"variants\": {\n" +
            "                        \"facing=north,half=bottom,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\" },\n" +
            "                        \"facing=south,half=bottom,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 180 },\n" +
            "                        \"facing=east,half=bottom,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 90 },\n" +
            "                        \"facing=west,half=bottom,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_bottom\", \"y\": 270 },\n" +
            "                        \"facing=north,half=top,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\" },\n" +
            "                        \"facing=south,half=top,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 180 },\n" +
            "                        \"facing=east,half=top,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 90 },\n" +
            "                        \"facing=west,half=top,open=false\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_top\", \"y\": 270 },\n" +
            "                        \"facing=north,half=bottom,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\" },\n" +
            "                        \"facing=south,half=bottom,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"y\": 180 },\n" +
            "                        \"facing=east,half=bottom,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"y\": 90 },\n" +
            "                        \"facing=west,half=bottom,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"y\": 270 },\n" +
            "                        \"facing=north,half=top,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"x\": 180, \"y\": 180 },\n" +
            "                        \"facing=south,half=top,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"x\": 180, \"y\": 0 },\n" +
            "                        \"facing=east,half=top,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"x\": 180, \"y\": 270 },\n" +
            "                        \"facing=west,half=top,open=true\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_open\", \"x\": 180, \"y\": 90 }\n" +
            "                    }\n" +
            "                }";

    public static final String STAIRS_BLOCKSTATE = "{\n" +
            "                \"variants\": {\n" +
            "                    \"facing=east,half=bottom,shape=straight\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\" },\n" +
            "                    \"facing=west,half=bottom,shape=straight\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=bottom,shape=straight\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=bottom,shape=straight\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=east,half=bottom,shape=outer_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\" },\n" +
            "                    \"facing=west,half=bottom,shape=outer_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=bottom,shape=outer_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=bottom,shape=outer_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=east,half=bottom,shape=outer_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=west,half=bottom,shape=outer_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=south,half=bottom,shape=outer_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\" },\n" +
            "                    \"facing=north,half=bottom,shape=outer_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=east,half=bottom,shape=inner_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\" },\n" +
            "                    \"facing=west,half=bottom,shape=inner_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=bottom,shape=inner_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=bottom,shape=inner_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=east,half=bottom,shape=inner_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=west,half=bottom,shape=inner_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=south,half=bottom,shape=inner_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\" },\n" +
            "                    \"facing=north,half=bottom,shape=inner_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=east,half=top,shape=straight\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"x\": 180, \"uvlock\": true },\n" +
            "                    \"facing=west,half=top,shape=straight\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=top,shape=straight\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=top,shape=straight\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=east,half=top,shape=outer_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=west,half=top,shape=outer_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=south,half=top,shape=outer_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=north,half=top,shape=outer_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"uvlock\": true },\n" +
            "                    \"facing=east,half=top,shape=outer_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"uvlock\": true },\n" +
            "                    \"facing=west,half=top,shape=outer_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=top,shape=outer_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=top,shape=outer_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_outer\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=east,half=top,shape=inner_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=west,half=top,shape=inner_right\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 270, \"uvlock\": true },\n" +
            "                    \"facing=south,half=top,shape=inner_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=north,half=top,shape=inner_right\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"uvlock\": true },\n" +
            "                    \"facing=east,half=top,shape=inner_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"uvlock\": true },\n" +
            "                    \"facing=west,half=top,shape=inner_left\":  { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 180, \"uvlock\": true },\n" +
            "                    \"facing=south,half=top,shape=inner_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 90, \"uvlock\": true },\n" +
            "                    \"facing=north,half=top,shape=inner_left\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_inner\", \"x\": 180, \"y\": 270, \"uvlock\": true }\n" +
            "                }\n" +
            "            }";

    public static final String WALL_BLOCKSTATE = "{\n" +
            "               \"multipart\": [\n" +
            "                 {\n" +
            "                   \"when\": { \"up\": \"true\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_post\" }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"north\": \"low\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side\", \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"east\": \"low\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side\", \"y\": 90, \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"south\": \"low\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side\", \"y\": 180, \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"west\": \"low\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side\", \"y\": 270, \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"north\": \"tall\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side_tall\", \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"east\": \"tall\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side_tall\", \"y\": 90, \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"south\": \"tall\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side_tall\", \"y\": 180, \"uvlock\": true }\n" +
            "                 },\n" +
            "                 {\n" +
            "                   \"when\": { \"west\": \"tall\" },\n" +
            "                   \"apply\": { \"model\": \"%MOD_ID%:block/%BLOCK_ID%_side_tall\", \"y\": 270, \"uvlock\": true }\n" +
            "                 }\n" +
            "               ]\n" +
            "             }";

}
