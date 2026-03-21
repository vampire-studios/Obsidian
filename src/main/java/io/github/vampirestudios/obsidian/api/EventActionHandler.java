package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class EventActionHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DEFAULT_AMOUNT_REQUIRED = 1;
    private static final int DEFAULT_PARTICLE_COUNT = 3;
    private static final float DEFAULT_SOUND_VOLUME = 0.5F;
    private static final float DEFAULT_SOUND_PITCH = 1.0F;
    private static final int DEFAULT_COOLDOWN = 0;
    private static final int DEFAULT_XP_AMOUNT = 0;

    public static void handleOnUse(Player player, Item item) {
        handleEventActions(player, item, "on_use");
    }

    public static void handleHurtEnemy(LivingEntity target, Player attacker, Item item) {
        handleEventActions(attacker, target, item, "hurt_enemy");
    }

    public static void handleOnMiningBlock(Player miningEntity, BlockState state, BlockPos pos, Item item, net.minecraft.world.item.Item minecraftItem) {
        List<Map<String, Object>> actions = item.getEventActions("mine_block");

        for (Map<String, Object> actionConfig : actions) {
            String action = (String) actionConfig.get("action");

            boolean shouldExecute = false;
            if (actionConfig.containsKey("blocks")) {
                List<Identifier> blocks = ((List<String>) actionConfig.get("blocks")).stream()
                        .map(Identifier::tryParse).toList();
                shouldExecute = blocks.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
            }

            if (actionConfig.containsKey("tags")) {
                List<String> tags = (List<String>) actionConfig.get("tags");
                for (String tag : tags) {
                    if (state.is(TagKey.create(Registries.BLOCK, Identifier.tryParse(tag)))) {
                        shouldExecute = true;
                        break;
                    }
                }
            }

            if (pos != null) {
                if (actionConfig.containsKey("position")) {
                    List<Double> position = (List<Double>) actionConfig.get("position");
                    if (pos.equals(new BlockPos(position.get(0).intValue(), position.get(1).intValue(), position.get(2).intValue()))) {
                        shouldExecute = true;
                    }
                }

                if (actionConfig.containsKey("min_position") && actionConfig.containsKey("max_position")) {
                    List<Double> minPosition = (List<Double>) actionConfig.get("min_position");
                    List<Double> maxPosition = (List<Double>) actionConfig.get("max_position");

                    int minX = minPosition.get(0).intValue();
                    int minY = minPosition.get(1).intValue();
                    int minZ = minPosition.get(2).intValue();
                    int maxX = maxPosition.get(0).intValue();
                    int maxY = maxPosition.get(1).intValue();
                    int maxZ = maxPosition.get(2).intValue();

                    if (pos.getX() >= minX && pos.getX() <= maxX &&
                            pos.getY() >= minY && pos.getY() <= maxY &&
                            pos.getZ() >= minZ && pos.getZ() <= maxZ) {
                        shouldExecute = true;
                    }
                }
            }

            if (!actionConfig.containsKey("blocks") && !actionConfig.containsKey("tags")
                    && !actionConfig.containsKey("position")
                    && !(actionConfig.containsKey("min_position") && actionConfig.containsKey("max_position"))
                    && !actionConfig.containsKey("radius")
                    && !(actionConfig.containsKey("width") && actionConfig.containsKey("height") && actionConfig.containsKey("depth"))
            ) {
                shouldExecute = true; // For generic actions
            }

            if (shouldExecute) {
                ItemStack stack = minecraftItem.getDefaultInstance();
                if (minecraftItem.getDefaultInstance().has(OItemComponents.MINING_RADIUS)) {
                    int radius = stack.get(OItemComponents.MINING_RADIUS);
                    executeMiningInRadius(miningEntity, pos, radius);
                }
                if (minecraftItem.getDefaultInstance().has(OItemComponents.MINING_AREA)) {
                    Vector3fc vector3f = stack.get(OItemComponents.MINING_AREA);
					assert vector3f != null;
					executeMiningInArea(miningEntity, pos, (int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z());
                }

                handleEventActions(miningEntity, null, action, actionConfig);
            }
        }
    }

    private static void executeMiningInRadius(Player player, BlockPos centerPos, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                BlockPos pos = centerPos.offset(x, y, 0);
                player.level().destroyBlock(pos, true);
            }
        }
    }

    private static void executeMiningInArea(Player player, BlockPos centerPos, int width, int height, int depth) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int halfDepth = depth / 2;

        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int y = -halfHeight; y <= halfHeight; y++) {
                for (int z = -halfDepth; z <= halfDepth; z++) {
                    BlockPos pos = centerPos.offset(x, y, z);
                    player.level().destroyBlock(pos, true);
                }
            }
        }
    }

    public static void handleOnItemCrafted(Player player, Item item) {
        handleEventActions(player, item, "craft");
    }

    public static void handleOnUseTick(Player player, Item item) {
        handleEventActions(player, item, "use_tick");
    }

    public static void handleOnUseOn(UseOnContext useOnContext, Item item) {
        handleEventActions(item, useOnContext, "use_on");
    }

    public static void handleOnFinishUsing(Player player, Item item) {
        handleEventActions(player, item, "finish_using");
    }

    public static void handleOnInventoryTick(Player player, Item item) {
        handleEventActions(player, item, "inventory_tick");
    }

    private static void handleEventActions(Player player, Item item, String event) {
        List<Map<String, Object>> actions = item.getEventActions(event);

        for (Map<String, Object> actionConfig : actions) {
            String action = (String) actionConfig.get("action");
            handleEventActions(player, null, action, actionConfig);
        }
    }

    private static void handleEventActions(Item item, UseOnContext useOnContext, String event) {
        List<Map<String, Object>> actions = item.getEventActions(event);

        for (Map<String, Object> actionConfig : actions) {
            String action = (String) actionConfig.get("action");
            handleEventActions(useOnContext, item, action, actionConfig);
        }
    }

    private static void handleEventActions(Player player, LivingEntity target, Item item, String event) {
        List<Map<String, Object>> actions = item.getEventActions(event);

        for (Map<String, Object> actionConfig : actions) {
            String action = (String) actionConfig.get("action");
            handleEventActions(player, target, action, actionConfig);
        }
    }

    private static void handleEventActions(Player player, LivingEntity target, String action, Map<String, Object> actionConfig) {
        try {
            switch (action) {
                case "send_message":
                    sendMessageAction(player, actionConfig);
                    break;
                case "apply_effect":
                    applyEffectAction(player, actionConfig);
                    break;
                case "give_experience":
                    giveExperienceAction(player, actionConfig);
                    break;
                case "play_sound":
                    playSoundAction(player, actionConfig);
                    break;
                case "spawn_particles":
                    spawnParticlesAction(player, actionConfig);
                    break;
                case "modify_attribute":
                    modifyAttributeAction(player, actionConfig);
                    break;
                case "execute_command":
                    executeCommandAction(player, actionConfig);
                    break;
                case "teleport":
                    teleportAction(player, actionConfig);
                    break;
                case "ignite":
                    ignite(target, actionConfig);
                    break;
                default:
                    LOGGER.warn("Unknown action: {}", action);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to handle action: {}", action, e);
        }
    }

    private static void handleEventActions(UseOnContext useOnContext, Item item, String action, Map<String, Object> actionConfig) {
        try {
			if (action.equals("convert_item")) {
				convertItem(useOnContext, actionConfig);
			} else {
				LOGGER.warn("Unknown action: {}", action);
			}
        } catch (Exception e) {
            LOGGER.error("Failed to handle action: {}", action, e);
        }
    }

    private static void sendMessageAction(Player player, Map<String, Object> actionConfig) {
        String message = (String) actionConfig.get("message");
        player.sendOverlayMessage(Component.literal(message));
    }

    private static void applyEffectAction(Player player, Map<String, Object> actionConfig) {
        String effect = (String) actionConfig.get("effect");
        int duration = ((Number) actionConfig.get("duration")).intValue();
        int amplifier = ((Number) actionConfig.get("amplifier")).intValue();
        player.addEffect(new MobEffectInstance(
                BuiltInRegistries.MOB_EFFECT.getOrThrow(ResourceKey.create(Registries.MOB_EFFECT, Identifier.parse(effect))),
                duration,
                amplifier
        ));
    }

    private static void giveExperienceAction(Player player, Map<String, Object> actionConfig) {
        int amount = ((Number) actionConfig.get("amount")).intValue();
        player.giveExperiencePoints(amount);
    }

    private static void playSoundAction(Player player, Map<String, Object> actionConfig) {
        String soundId = (String) actionConfig.get("sound");
        SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.tryParse(soundId));
        float volume = ((Number) actionConfig.get("volume")).floatValue();
        float pitch = ((Number) actionConfig.get("pitch")).floatValue();
        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.PLAYERS, volume, pitch);
    }

    private static void spawnParticlesAction(Player player, Map<String, Object> actionConfig) {
        String particleId = (String) actionConfig.get("particle");
        int count = ((Number) actionConfig.get("count")).intValue();
        double speedX = ((Number) actionConfig.get("speed")).doubleValue();
        double speedY = ((Number) actionConfig.get("speed")).doubleValue();
        double speedZ = ((Number) actionConfig.get("speed")).doubleValue();
        double offsetX = ((Number) actionConfig.get("offsetX")).doubleValue();
        double offsetY = ((Number) actionConfig.get("offsetY")).doubleValue();
        double offsetZ = ((Number) actionConfig.get("offsetZ")).doubleValue();
        // Note: ParticleRegistry should be defined to handle custom particles
        for (int i = 0; i < count; i++) {
            player.level().addParticle((ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.getOrThrow(ResourceKey.create(Registries.PARTICLE_TYPE,
                            Identifier.parse(particleId)
            )), player.getX() + offsetX + i, player.getY() + offsetY + i, player.getZ() + offsetZ + i,
                    speedX + i, speedY + i, speedZ + i
            );
        }
    }

    private static void modifyAttributeAction(Player player, Map<String, Object> actionConfig) {
        // Example: modify speed attribute
        Identifier name = Identifier.tryParse((String) actionConfig.get("id"));
        String attribute = (String) actionConfig.get("attribute");
        double amount = ((Number) actionConfig.get("amount")).doubleValue();
        AttributeModifier.Operation operation = switch (((String) actionConfig.get("operation"))) {
            case "add_value" -> AttributeModifier.Operation.ADD_VALUE;
            case "add_multiplied_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
            case "add_multiplied_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
			default -> throw new IllegalStateException("Unexpected value: " + actionConfig.get("operation"));
		};
        player.getAttribute(BuiltInRegistries.ATTRIBUTE.getOrThrow(ResourceKey.create(Registries.ATTRIBUTE, Identifier.parse(attribute))))
                .addOrUpdateTransientModifier(new AttributeModifier(name, amount, operation));
    }

    private static void executeCommandAction(Player player, Map<String, Object> actionConfig) {
        if (player instanceof ServerPlayer serverPlayer) {
            String command = (String) actionConfig.get("command");
            serverPlayer.level().getServer().getCommands().performPrefixedCommand(serverPlayer.createCommandSourceStack(), command);
        }
    }

    private static void teleportAction(Player player, Map<String, Object> actionConfig) {
        double x = ((Number) actionConfig.get("x")).doubleValue();
        double y = ((Number) actionConfig.get("y")).doubleValue();
        double z = ((Number) actionConfig.get("z")).doubleValue();
        player.teleportTo(x, y, z);
    }

    private static void ignite(LivingEntity target, Map<String, Object> actionConfig) {
        if (target == null) return;

        int duration = ((Number) actionConfig.get("duration_in_seconds")).intValue();
        target.igniteForSeconds(duration);
    }

    private static void convertItem(UseOnContext useOnContext, Map<String, Object> actionConfig) {
        Player player = useOnContext.getPlayer();
        if (player == null) {
            LOGGER.warn("Player is null in convertItem method.");
            return;
        }

        Level level = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = level.getBlockState(pos);

        Optional<Block> optionalBlock = Optional.ofNullable((String) actionConfig.get("block"))
                .map(id -> BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(id)));
        Block block = optionalBlock.orElse(null);

        if (block == null || state.is(block)) {
            processItemConversion(player, level, pos, useOnContext.getHand(), actionConfig, block != null);
        } else {
            LOGGER.info("Block does not match for conversion at position: {}", pos);
        }
    }

    private static void processItemConversion(Player player, Level level, BlockPos pos, InteractionHand hand, Map<String, Object> actionConfig, boolean playEffects) {
        ItemStack stack = player.getItemInHand(hand);

        int requiredAmount = ((Number) actionConfig.getOrDefault("amount_required", DEFAULT_AMOUNT_REQUIRED)).intValue();
        if (stack.getCount() < requiredAmount) {
            LOGGER.info("Insufficient items for conversion. Required: {}, Available: {}", requiredAmount, stack.getCount());
            return;
        }

        if (!checkCooldown(player, actionConfig)) {
            LOGGER.info("Player {} is on cooldown for conversion.", player.getName().getString());
            return;
        }

        String newItemId = (String) actionConfig.get("converted_item");
        if (newItemId == null) {
            LOGGER.warn("Converted item ID is null in actionConfig.");
            return;
        }

        ItemStack newItem = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.tryParse(newItemId)));

        if (playEffects) {
            if (level.isClientSide()) {
                spawnParticles(level, pos, stack, actionConfig);
                player.swing(hand);
            }
            playSound(level, pos, actionConfig);
        }

        player.startUsingItem(hand);
        player.releaseUsingItem();

        stack.shrink(requiredAmount);
        if (!player.addItem(newItem)) {
            player.drop(newItem, false);
        }

        displayCustomMessage(player, actionConfig);
        applyCustomDurability(stack, player, hand, actionConfig);
        giveExperience(player, actionConfig);
        setCooldown(player, actionConfig);
    }

    private static void spawnParticles(Level level, BlockPos pos, ItemStack stack, Map<String, Object> actionConfig) {
        ItemStack particleItem = Optional.ofNullable((String) actionConfig.get("particle_item"))
                .map(id -> new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(id))))
                .orElse(stack);
        int count = ((Number) actionConfig.getOrDefault("max_particle_count", DEFAULT_PARTICLE_COUNT)).intValue();
        ParticleUtils.spawnParticlesOnBlockFaces(level, pos, new ItemParticleOption(ParticleTypes.ITEM, particleItem.getItem()), UniformInt.of(1, count));
    }

    private static void playSound(Level level, BlockPos pos, Map<String, Object> actionConfig) {
        SoundEvent soundEvent = Optional.ofNullable((String) actionConfig.get("sound"))
                .map(id -> BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(id)))
                .orElse(SoundEvents.GRINDSTONE_USE);
        float volume = ((Number) actionConfig.getOrDefault("sound_volume", DEFAULT_SOUND_VOLUME)).floatValue();
        float pitch = ((Number) actionConfig.getOrDefault("sound_pitch", DEFAULT_SOUND_PITCH)).floatValue();
        level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
    }

    private static void displayCustomMessage(Player player, Map<String, Object> actionConfig) {
        String message = (String) actionConfig.get("message");
        if (message != null) {
            player.sendOverlayMessage(Component.literal(message));
        }
    }

    private static void applyCustomDurability(ItemStack stack, Player player, InteractionHand hand, Map<String, Object> actionConfig) {
        int durabilityLoss = ((Number) actionConfig.getOrDefault("durability_loss", 0)).intValue();
        if (durabilityLoss > 0 && stack.has(DataComponents.DAMAGE) || stack.has(DataComponents.MAX_DAMAGE)) {
            stack.hurtAndBreak(durabilityLoss, player, hand.asEquipmentSlot());
        }
    }

    private static boolean checkCooldown(Player player, Map<String, Object> actionConfig) {
        int cooldown = ((Number) actionConfig.getOrDefault("cooldown", DEFAULT_COOLDOWN)).intValue();
		return cooldown <= 0 || !player.getCooldowns().isOnCooldown(player.getItemInHand(InteractionHand.MAIN_HAND));
	}

    private static void setCooldown(Player player, Map<String, Object> actionConfig) {
        int cooldown = ((Number) actionConfig.getOrDefault("cooldown", DEFAULT_COOLDOWN)).intValue();
        if (cooldown > 0) {
            player.getCooldowns().addCooldown(player.getItemInHand(InteractionHand.MAIN_HAND), cooldown * 20);
        }
    }

    private static void giveExperience(Player player, Map<String, Object> actionConfig) {
        int xpAmount = ((Number) actionConfig.getOrDefault("xp_amount", DEFAULT_XP_AMOUNT)).intValue();
        XPType xpType = XPType.valueOf((String) actionConfig.getOrDefault("xp_type", XPType.POINTS.name().toLowerCase(Locale.ROOT)));
        if (xpAmount > 0) {
            switch (xpType) {
				case POINTS -> player.giveExperiencePoints(xpAmount);
				case LEVELS -> player.giveExperienceLevels(xpAmount);
			}
        }
    }

    private enum XPType { POINTS, LEVELS }

    /*private static void unlockRecipeAction(Player player, Map<String, Object> actionConfig) {
        if (player instanceof ServerPlayer serverPlayer) {
            String recipeId = (String) actionConfig.get("recipe");
            Recipe<?> recipe = serverPlayer.server.getRecipeManager().getRecipe(Identifier.tryParse(recipeId)).orElse(null);
            if (recipe != null) {
                serverPlayer.awardRecipes(List.of(recipe));
            }
        }
    }*/
}
