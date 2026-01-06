package org.quiltmc.qsl.block.content.registry.api.enchanting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

public record EnchantingBlockStateBooster() implements EnchantingBooster {
    public static EnchantingBoosterType TYPE = EnchantingBoosters.register(Identifier.fromNamespaceAndPath("quilt", "block_state_booster"),
            new EnchantingBoosterType(MapCodec.unit(EnchantingBlockStateBooster::new), Optional.of(new EnchantingBlockStateBooster())));

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