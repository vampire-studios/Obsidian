package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record IdOrTag(Identifier id, Identifier tag) {
  public static final Codec<IdOrTag> CODEC = RecordCodecBuilder.create(i -> i.group(
      Identifier.CODEC.optionalFieldOf("id").forGetter(t -> java.util.Optional.ofNullable(t.id())),
      Identifier.CODEC.optionalFieldOf("tag").forGetter(t -> java.util.Optional.ofNullable(t.tag()))
  ).apply(i, (idOpt, tagOpt) -> new IdOrTag(idOpt.orElse(null), tagOpt.orElse(null))));
}
