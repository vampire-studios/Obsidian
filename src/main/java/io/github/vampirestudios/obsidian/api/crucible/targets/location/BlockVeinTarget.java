package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockVeinTarget extends LocationTarget {
    private String blockType;
    private int limit;
    private boolean originMustMatch;
    private List<String> wantedBlockTypes;

    public BlockVeinTarget(String blockType, int limit, boolean originMustMatch) {
        super(List.of("blockVein", "vein", "bv"));
        this.blockType = blockType;
        this.limit = limit;
        this.originMustMatch = originMustMatch;
    }

    @Override
    public List<Vec3> getTargets(LivingEntity caster) {
        List<Vec3> targets = new ArrayList<>();
        BlockPos origin = caster.blockPosition();
        this.wantedBlockTypes = List.of(this.blockType.split(","));
        findConnectedBlocks(caster, targets, origin, this.limit, true);
        return targets;
    }

    private void findConnectedBlocks(LivingEntity caster, List<Vec3> targets, BlockPos origin, int limit, boolean first) {
        if (limit == 0 || targets.size() >= limit)
            return;
        if (first && this.originMustMatch) {
            Block originBlock = caster.level().getBlockState(origin).getBlock();
            if (!blockMatches(originBlock, this.wantedBlockTypes))
                return;
        }
        List<int[]> directions = generateRandomizedDirections();
        for (int[] direction : directions) {
            int x = direction[0], y = direction[1], z = direction[2];
            BlockPos neighbor = new BlockPos(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
            if (!targets.contains(neighbor.getCenter())) {
                Block material = caster.level().getBlockState(neighbor).getBlock();
                if (blockMatches(material, this.wantedBlockTypes)) {
                    targets.add(neighbor.getCenter());
                    findConnectedBlocks(caster, targets, neighbor, limit - 1, false);
                }
            }
        }
    }

    List<int[]> generateRandomizedDirections() {
        List<int[]> directions = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0)
                        directions.add(new int[] { x, y, z });
                }
            }
        }
        Collections.shuffle(directions);
        return directions;
    }
}