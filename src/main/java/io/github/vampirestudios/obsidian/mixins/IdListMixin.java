package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.ExtendedIdList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(IdList.class)
public abstract class IdListMixin<T> implements ExtendedIdList<T> {
    @Shadow @Final private Object2IntMap<T> idMap;

    @Shadow public abstract int getRawId(T value);

    @Shadow @Final private List<T> list;
    @Shadow private int nextId;
    private final IntList obsidian$freeIds = new IntArrayList();

    @Override
    public void obsidian$remove(T value) {
        int id = getRawId(value);
        idMap.removeInt(value);
        list.set(id, null);

        obsidian$freeIds.add(id);
    }

    @Redirect(method = "add", at = @At(value = "FIELD", target = "Lnet/minecraft/util/collection/IdList;nextId:I"))
    private int tryUseFreeId(IdList<T> instance) {
        if (!obsidian$freeIds.isEmpty())
            return obsidian$freeIds.removeInt(0);

        return nextId;
    }
}