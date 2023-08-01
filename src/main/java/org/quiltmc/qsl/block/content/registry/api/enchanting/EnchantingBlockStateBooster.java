package org.quiltmc.qsl.block.content.registry.api.enchanting;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public record EnchantingBlockStateBooster() implements EnchantingBooster {
    public static EnchantingBoosterType TYPE = EnchantingBoosters.register(new ResourceLocation("quilt", "block_state_booster"),
            new EnchantingBoosterType(Codec.unit(EnchantingBlockStateBooster::new), Optional.of(new EnchantingBlockStateBooster())));

    @Override
    public float getEnchantingBoost(Level world, BlockState state, BlockPos pos) {
        if (!state.hasProperty(BlockStateProperties.POWER)) {
            return 0;
        }

        return state.getValue(BlockStateProperties.POWER) / 15f;
    }

    @Override
    public EnchantingBoosterType getType() {
        return TYPE;
    }
}