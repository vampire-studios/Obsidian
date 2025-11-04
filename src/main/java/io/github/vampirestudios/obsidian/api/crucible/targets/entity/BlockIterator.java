/*
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Direction;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BlockIterator implements Iterator<Block> {
    private final Level world;
    private final int maxDistance;
    private static final int gridSize = 16777216;
    private boolean end;
    private Block[] blockQueue;
    private int currentBlock;
    private int currentDistance;
    private int maxDistanceInt;
    private int secondError;
    private int thirdError;
    private int secondStep;
    private int thirdStep;
    private Direction mainFace;
    private Direction secondFace;
    private Direction thirdFace;

    public BlockIterator(@NotNull Level world, @NotNull BlockPos start, @NotNull BlockPos direction, int yOffset, int maxDistance) {
        this.end = false;
        this.blockQueue = new Block[3];
        this.currentBlock = 0;
        this.currentDistance = 0;
        Preconditions.checkArgument(world != null, "world must not be null");
        Preconditions.checkArgument(start != null, "start must not be null");
        Preconditions.checkArgument(direction != null, "direction must not be null");
        this.world = world;
        this.maxDistance = maxDistance;
        BlockPos startClone = new BlockPos(start);
        startClone.atY(startClone.getY() + yOffset);
        this.currentDistance = 0;
        double mainDirection = 0.0;
        double secondDirection = 0.0;
        double thirdDirection = 0.0;
        double mainPosition = 0.0;
        double secondPosition = 0.0;
        double thirdPosition = 0.0;
        Block startBlock = this.world.getBlockState(new BlockPos(NumberConversions.floor(startClone.getX()), NumberConversions.floor(startClone.getY()), NumberConversions.floor(startClone.getZ()))).getBlock();
        if (this.getXLength(direction) > mainDirection) {
            this.mainFace = this.getXFace(direction);
            mainDirection = this.getXLength(direction);
            mainPosition = this.getXPosition(direction, startClone, startBlock);
            this.secondFace = this.getYFace(direction);
            secondDirection = this.getYLength(direction);
            secondPosition = this.getYPosition(direction, startClone, startBlock);
            this.thirdFace = this.getZFace(direction);
            thirdDirection = this.getZLength(direction);
            thirdPosition = this.getZPosition(direction, startClone, startBlock);
        }

        if (this.getYLength(direction) > mainDirection) {
            this.mainFace = this.getYFace(direction);
            mainDirection = this.getYLength(direction);
            mainPosition = this.getYPosition(direction, startClone, startBlock);
            this.secondFace = this.getZFace(direction);
            secondDirection = this.getZLength(direction);
            secondPosition = this.getZPosition(direction, startClone, startBlock);
            this.thirdFace = this.getXFace(direction);
            thirdDirection = this.getXLength(direction);
            thirdPosition = this.getXPosition(direction, startClone, startBlock);
        }

        if (this.getZLength(direction) > mainDirection) {
            this.mainFace = this.getZFace(direction);
            mainDirection = this.getZLength(direction);
            mainPosition = this.getZPosition(direction, startClone, startBlock);
            this.secondFace = this.getXFace(direction);
            secondDirection = this.getXLength(direction);
            secondPosition = this.getXPosition(direction, startClone, startBlock);
            this.thirdFace = this.getYFace(direction);
            thirdDirection = this.getYLength(direction);
            thirdPosition = this.getYPosition(direction, startClone, startBlock);
        }

        double d = mainPosition / mainDirection;
        double secondd = secondPosition - secondDirection * d;
        double thirdd = thirdPosition - thirdDirection * d;
        this.secondError = NumberConversions.floor(secondd * 1.6777216E7);
        this.secondStep = NumberConversions.round(secondDirection / mainDirection * 1.6777216E7);
        this.thirdError = NumberConversions.floor(thirdd * 1.6777216E7);
        this.thirdStep = NumberConversions.round(thirdDirection / mainDirection * 1.6777216E7);
        if (this.secondError + this.secondStep <= 0) {
            this.secondError = -this.secondStep + 1;
        }

        if (this.thirdError + this.thirdStep <= 0) {
            this.thirdError = -this.thirdStep + 1;
        }

        Block lastBlock = startBlock.getRelative(this.mainFace.getOppositeFace());
        if (this.secondError < 0) {
            this.secondError += 16777216;
            lastBlock = lastBlock.getRelative(this.secondFace.getOppositeFace());
        }

        if (this.thirdError < 0) {
            this.thirdError += 16777216;
            lastBlock = lastBlock.et(this.thirdFace.getOppositeFace());
        }

        this.secondError -= 16777216;
        this.thirdError -= 16777216;
        this.blockQueue[0] = lastBlock;
        this.currentBlock = -1;
        this.scan();
        boolean startBlockFound = false;

        for(int cnt = this.currentBlock; cnt >= 0; --cnt) {
            if (this.blockEquals(this.blockQueue[cnt], startBlock)) {
                this.currentBlock = cnt;
                startBlockFound = true;
                break;
            }
        }

        if (!startBlockFound) {
            throw new IllegalStateException("Start block missed in BlockIterator");
        } else {
            this.maxDistanceInt = NumberConversions.round((double)maxDistance / (Math.sqrt(mainDirection * mainDirection + secondDirection * secondDirection + thirdDirection * thirdDirection) / mainDirection));
        }
    }

    private boolean blockEquals(@NotNull Block a, @NotNull Block b) {
        return a.() == b.getX() && a.getY() == b.getY() && a.getZ() == b.getZ();
    }

    private Direction getXFace(@NotNull BlockPos direction) {
        return direction.getX() > 0.0 ? Direction.EAST : Direction.WEST;
    }

    private Direction getYFace(@NotNull BlockPos direction) {
        return direction.getY() > 0.0 ? Direction.UP : Direction.DOWN;
    }

    private Direction getZFace(@NotNull BlockPos direction) {
        return direction.getZ() > 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private double getXLength(@NotNull BlockPos direction) {
        return Math.abs(direction.getX());
    }

    private double getYLength(@NotNull BlockPos direction) {
        return Math.abs(direction.getY());
    }

    private double getZLength(@NotNull BlockPos direction) {
        return Math.abs(direction.getZ());
    }

    private double getPosition(double direction, double position, int blockPosition) {
        return direction > 0.0 ? position - (double)blockPosition : (double)(blockPosition + 1) - position;
    }

    private double getXPosition(@NotNull BlockPos direction, @NotNull BlockPos position, @NotNull Block block) {
        return this.getPosition(direction.getX(), position.getX(), block.getX());
    }

    private double getYPosition(@NotNull BlockPos direction, @NotNull BlockPos position, @NotNull Block block) {
        return this.getPosition(direction.getY(), position.getY(), block.getY());
    }

    private double getZPosition(@NotNull BlockPos direction, @NotNull BlockPos position, @NotNull Block block) {
        return this.getPosition(direction.getZ(), position.getZ(), block.getZ());
    }

    public BlockIterator(@NotNull Location loc, double yOffset, int maxDistance) {
        this(loc.getWorld(), loc.toBlockPos(), loc.getDirection(), yOffset, maxDistance);
    }

    public BlockIterator(@NotNull Location loc, double yOffset) {
        this(loc.getWorld(), loc.toBlockPos(), loc.getDirection(), yOffset, 0);
    }

    public BlockIterator(@NotNull Location loc) {
        this(loc, 0.0);
    }

    public BlockIterator(@NotNull LivingEntity entity, int maxDistance) {
        this(entity.getLocation(), entity.getEyeHeight(), maxDistance);
    }

    public BlockIterator(@NotNull LivingEntity entity) {
        this(entity, 0);
    }

    public boolean hasNext() {
        this.scan();
        return this.currentBlock != -1;
    }

    public @NotNull Block next() throws NoSuchElementException {
        this.scan();
        if (this.currentBlock <= -1) {
            throw new NoSuchElementException();
        } else {
            return this.blockQueue[this.currentBlock--];
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("[BlockIterator] doesn't support block removal");
    }

    private void scan() {
        if (this.currentBlock < 0) {
            if (this.maxDistance != 0 && this.currentDistance > this.maxDistanceInt) {
                this.end = true;
            } else if (!this.end) {
                ++this.currentDistance;
                this.secondError += this.secondStep;
                this.thirdError += this.thirdStep;
                if (this.secondError > 0 && this.thirdError > 0) {
                    this.blockQueue[2] = this.blockQueue[0].getRelative(this.mainFace);
                    if ((long)this.secondStep * (long)this.thirdError < (long)this.thirdStep * (long)this.secondError) {
                        this.blockQueue[1] = this.blockQueue[2].getRelative(this.secondFace);
                        this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
                    } else {
                        this.blockQueue[1] = this.blockQueue[2].getRelative(this.thirdFace);
                        this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
                    }

                    this.thirdError -= 16777216;
                    this.secondError -= 16777216;
                    this.currentBlock = 2;
                } else if (this.secondError > 0) {
                    this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
                    this.blockQueue[0] = this.blockQueue[1].getRelative(this.secondFace);
                    this.secondError -= 16777216;
                    this.currentBlock = 1;
                } else if (this.thirdError > 0) {
                    this.blockQueue[1] = this.blockQueue[0].getRelative(this.mainFace);
                    this.blockQueue[0] = this.blockQueue[1].getRelative(this.thirdFace);
                    this.thirdError -= 16777216;
                    this.currentBlock = 1;
                } else {
                    this.blockQueue[0] = this.blockQueue[0].getRelative(this.mainFace);
                    this.currentBlock = 0;
                }
            }
        }
    }
}
*/
