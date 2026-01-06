package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Locale;

public record AmmoSpec(/*Type type, */Identifier id) {

	public static final Codec<AmmoSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
//			Type.CODEC.optionalFieldOf("type", Type.ITEM).forGetter(AmmoSpec::type),
			Identifier.CODEC.fieldOf("id").forGetter(AmmoSpec::id)
	).apply(i, AmmoSpec::new));


	public static final StreamCodec<ByteBuf, AmmoSpec> STREAM_CODEC = StreamCodec.composite(
//			Type.STREAM_CODEC,  AmmoSpec::type,
			Identifier.STREAM_CODEC, AmmoSpec::id,
			AmmoSpec::new
	);

//	public static AmmoSpec item(Identifier itemId) {
//		return new AmmoSpec(Type.ITEM, itemId);
//	}
//
//	public static AmmoSpec tag(Identifier tagId) {
//		return new AmmoSpec(Type.TAG, tagId);
//	}

	public enum Type {
		ITEM, TAG;

		public static final Codec<Type> CODEC =
				Codec.STRING.xmap(Type::fromId, Type::id);


		public static final StreamCodec<ByteBuf, Type> STREAM_CODEC =
				ByteBufCodecs.VAR_INT.map(
						v -> v == 1 ? TAG : ITEM,
						t -> t == TAG ? 1 : 0
				);

		public static Type fromId(String s) {
			if (s.toLowerCase(Locale.ROOT).equals("tag")) {
				return TAG;
			}
			return ITEM;
		}

		public String id() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
