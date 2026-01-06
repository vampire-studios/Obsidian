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

    /*public static final MapCodec<BlockInformation> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Description.CODEC.fieldOf("description").forGetter(block -> block.description),
            Codec.STRING.fieldOf("block_type").forGetter(block -> block.block_type),

    ).apply(instance, BlockInformation::new));*/

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

    public List<String> removedTooltipSections;

    @SerializedName("block_properties")
    @com.google.gson.annotations.SerializedName("block_properties")
    @Path("block_properties")
    public Object blockSettings;

    public BlockSettings getBlockSettings() {
		switch (blockSettings) {
			case Identifier Identifier -> {
				return ContentRegistries.BLOCK_SETTINGS.getValue(Identifier);
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				return ContentRegistries.BLOCK_SETTINGS.getValue(location);
			}
			case BlockSettings blockSettings1 -> {
                return blockSettings1;
			}
			case null, default -> {
				return null;
			}
		}
    }

    @SerializedName("item_properties")
    @com.google.gson.annotations.SerializedName("item_properties")
    @Path("item_properties")
    public Object itemSettings;

    public ItemSettings getItemSettings() {
		switch (itemSettings) {
			case Identifier Identifier -> {
				return ContentRegistries.ITEM_SETTINGS.getValue(Identifier);
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				return ContentRegistries.ITEM_SETTINGS.getValue(location);
			}
			case ItemSettings itemSettings1 -> {
				return itemSettings1;
			}
			case null, default -> {
				return new ItemSettings();
			}
		}
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
        public float[] full_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] north_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] south_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] east_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] west_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] up_shape = new float[] {0, 0, 0, 16, 16, 16};
        public float[] down_shape = new float[] {0, 0, 0, 16, 16, 16};

        public enum CollisionType {
            FULL_BLOCK,
            BOTTOM_SLAB,
            TOP_SLAB,
            CUSTOM,
            NONE
        }
    }

}