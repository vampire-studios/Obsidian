package io.github.vampirestudios.obsidian.api.obsidian.block;

import blue.endless.jankson.annotation.SerializedName;
import com.electronwill.nightconfig.core.conversion.Path;
import io.github.vampirestudios.obsidian.api.obsidian.BlockSettings;
import io.github.vampirestudios.obsidian.api.obsidian.ItemSettings;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public class BlockInformation {
    public NameInformation name;

    @SerializedName("block_set_type")
    @com.google.gson.annotations.SerializedName("block_set_type")
    @Path("block_set_type")
    public Identifier blockSetType;

    @SerializedName("wood_type")
    @com.google.gson.annotations.SerializedName("wood_type")
    @Path("wood_type")
    public Identifier woodType;

    @SerializedName("parent_block")
    @com.google.gson.annotations.SerializedName("parent_block")
    @Path("parent_block")
    public Identifier parentBlock;

    /**
     * Shorthand for simple non-full blocks (decorations, plushies, etc.).
     * A 6-element array [x1, y1, z1, x2, y2, z2] in pixel space (0–16).
     * Sets both the collision and outline shape in one field.
     * If {@code collision_shape} or {@code outline_shape} are also specified,
     * they take priority over this shorthand for their respective shape.
     */
    public float[] shape;

    /**
     * Multi-box shorthand — an array of boxes, each a 6-element float array [x1,y1,z1,x2,y2,z2].
     * All boxes are combined into one composite shape using {@code Shapes.or()}.
     * Takes priority over {@code shape} when both are present.
     * Example: [[1,0,1,14,2,14], [0,2,0,16,9,16]]
     */
    public float[][] shapes;

    @SerializedName("collision_shape")
    @com.google.gson.annotations.SerializedName("collision_shape")
    @Path("collision_shape")
    public BoundingBox collisionShape;

    @SerializedName("outline_shape")
    @com.google.gson.annotations.SerializedName("outline_shape")
    @Path("outline_shape")
    public BoundingBox outlineShape;

    public int cake_slices = 1;

    public boolean has_item = true;
    public boolean wooden_button = true;
    public boolean powerable = false;
    public boolean toggleable = false;

    public List<String> removedTooltipSections;

    @SerializedName("block_properties")
    @com.google.gson.annotations.SerializedName("block_properties")
    @Path("block_properties")
    public Object blockSettings;

    public BlockSettings getBlockSettings() {
		return switch (blockSettings) {
			case Identifier Identifier -> ContentRegistries.BLOCK_SETTINGS.getValue(Identifier);
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				yield ContentRegistries.BLOCK_SETTINGS.getValue(location);
			}
			case BlockSettings blockSettings1 -> blockSettings1;
			case null, default -> new BlockSettings();
		};
    }

    @SerializedName("item_properties")
    @com.google.gson.annotations.SerializedName("item_properties")
    @Path("item_properties")
    public Object itemSettings;

    public ItemSettings getItemSettings() {
		return switch (itemSettings) {
			case Identifier Identifier -> ContentRegistries.ITEM_SETTINGS.getValue(Identifier);
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				yield ContentRegistries.ITEM_SETTINGS.getValue(location);
			}
			case ItemSettings itemSettings1 -> itemSettings1;
			case null, default -> new ItemSettings();
		};
    }

	public Map<String, String[]> properties;
	public Map<String, String> defaultValue;

    /*public RenderLayer getRenderLayer() {
        return switch(renderLayer) {
            case "SOLID" -> RenderLayer.getSolid();
            case "CUTOUT" -> RenderLayer.getCutout();
            case "CUTOUT_MIPPED" -> RenderLayer.getCutoutMipped();
            case "TRANSLUCENT" -> RenderLayer.getTranslucent();
            case "TRANSLUCENT_MOVING_BLOCK" -> RenderLayer.getTranslucentMovingBlock();
            case "TRANSLUCENT_NO_CRUMBLING" -> RenderLayer.getTranslucentNoCrumbling();
            case "WATER_MASK" -> RenderLayer.getWaterMask();
            case "END_PORTAL" -> RenderLayer.getEndPortal();
            case "END_GATEWAY" -> RenderLayer.getEndGateway();
            case "GLINT" -> RenderLayer.getGlint();
            case "DIRECT_GLINT" -> RenderLayer.getDirectGlint();
            case "TRIPWIRE" -> RenderLayer.getTripwire();
            default -> throw new IllegalStateException("Unexpected value: " + renderLayer);
        };
    }*/

    public static class BoundingBox {
        @SerializedName("collision_type")
        @com.google.gson.annotations.SerializedName("collision_type")
        @Path("collision_type")
        public CollisionType collisionType;
        public boolean advanced = false;
        // Single-box shapes
        public float[] full_shape = new float[] {0, 0, 0, 16, 16, 16};
        // Directional single-box overrides — null means "use full_shape for this direction"
        public float[] north_shape = null;
        public float[] south_shape = null;
        public float[] east_shape = null;
        public float[] west_shape = null;
        public float[] up_shape = null;
        public float[] down_shape = null;
        // Multi-box shapes — takes priority over the single-box equivalents above when set
        public float[][] full_shapes = null;
        public float[][] north_shapes = null;
        public float[][] south_shapes = null;
        public float[][] east_shapes = null;
        public float[][] west_shapes = null;
        public float[][] up_shapes = null;
        public float[][] down_shapes = null;

        public enum CollisionType {
            FULL_BLOCK,
            BOTTOM_SLAB,
            TOP_SLAB,
            CUSTOM,
            NONE
        }
    }

}