package io.github.vampirestudios.obsidian.api.obsidian.block;

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
	public transient Identifier id;

	@com.google.gson.annotations.SerializedName("block_set_type")
	@Path("block_set_type")
	public Identifier blockSetType;

	@com.google.gson.annotations.SerializedName("wood_type")
	@Path("wood_type")
	public Identifier woodType;

	@com.google.gson.annotations.SerializedName("parent_block")
	@Path("parent_block")
	public Identifier parentBlock;

	public float[] shape;
	public float[][] shapes;

	@com.google.gson.annotations.SerializedName("collision_shape")
	@Path("collision_shape")
	public BoundingBox collisionShape;

	@com.google.gson.annotations.SerializedName("outline_shape")
	@Path("outline_shape")
	public BoundingBox outlineShape;

	/**
	 * Per-placement geometry, overriding the block-wide shape for the surface the block was placed against.
	 * See {@link PlacementVariants}.
	 */
	@com.google.gson.annotations.SerializedName("placement_shapes")
	@Path("placement_shapes")
	public PlacementShapes placementShapes;

	/**
	 * Per-cell geometry of a multi-block, keyed by cell name ({@code "<x>_<y>_<z>"}). See
	 * {@link MultiBlockVariants}.
	 */
	@com.google.gson.annotations.SerializedName("part_shapes")
	@Path("part_shapes")
	public Map<String, ShapeSet> partShapes;

	public int cake_slices = 1;

	public boolean has_item = true;
	public boolean wooden_button = true;
	public boolean powerable = false;
	public boolean toggleable = false;

	public List<String> removedTooltipSections;

	@com.google.gson.annotations.SerializedName("block_properties")
	@Path("block_properties")
	public Object blockSettings;

	/**
	 * Settings written out in this file, kept once they have been read — registration asks for them
	 * several times per block. Settings named by id are looked up fresh, since the entry they name may
	 * not have been registered the first time we were asked.
	 */
	private transient BlockSettings resolvedBlockSettings;
	private transient ItemSettings resolvedItemSettings;

	private static boolean inline(Object declaration) {
		return !(declaration instanceof String) && !(declaration instanceof Identifier);
	}

	/**
	 * The block's settings, whether they were written out here, named as an entry in {@code block/property},
	 * or inherited from a {@code parent}.
	 */
	public BlockSettings getBlockSettings() {
		if (inline(blockSettings) && resolvedBlockSettings != null) return resolvedBlockSettings;

		BlockSettings settings = BlockSettings.resolve(blockSettings);
		if (inline(blockSettings)) resolvedBlockSettings = settings;
		return settings;
	}

	@com.google.gson.annotations.SerializedName("item_properties")
	@Path("item_properties")
	public Object itemSettings;

	/** The block item's settings, resolved the same way as {@link #getBlockSettings()}. */
	public ItemSettings getItemSettings() {
		if (inline(itemSettings) && resolvedItemSettings != null) return resolvedItemSettings;

		ItemSettings settings = ItemSettings.resolve(itemSettings);
		if (inline(itemSettings)) resolvedItemSettings = settings;
		return settings;
	}

	public Map<String, String[]> properties;
	public Map<String, String> defaultValue;

	/**
	 * Names of vanilla block state properties to add to this block, e.g. {@code "facing"} or
	 * {@code "lit"}. Resolved against every property declared on {@code BlockStateProperties}.
	 */
	@com.google.gson.annotations.SerializedName("vanilla_properties")
	@Path("vanilla_properties")
	public String[] vanillaProperties;

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

	/**
	 * Geometry for each surface a block can be placed against. A variant left out is not registered as a
	 * possible value of the {@code placement} property unless it declares a model instead.
	 */
	public static class PlacementShapes {
		public ShapeSet floor;
		public ShapeSet wall;
		public ShapeSet ceiling;

		public ShapeSet get(String placement) {
			return switch (placement) {
				case PlacementVariants.FLOOR -> floor;
				case PlacementVariants.WALL -> wall;
				case PlacementVariants.CEILING -> ceiling;
				default -> null;
			};
		}
	}

	/** One variant's geometry, in the same four fields as the block itself. Anything left out falls back to it. */
	public static class ShapeSet {
		public float[] shape;
		public float[][] shapes;

		@com.google.gson.annotations.SerializedName("collision_shape")
		@Path("collision_shape")
		public BoundingBox collisionShape;

		@com.google.gson.annotations.SerializedName("outline_shape")
		@Path("outline_shape")
		public BoundingBox outlineShape;
	}

	public static class BoundingBox {
		@com.google.gson.annotations.SerializedName("collision_type")
		@Path("collision_type")
		public CollisionType collisionType;
		public float[] up_shape = null;
		public float[] down_shape = null;
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