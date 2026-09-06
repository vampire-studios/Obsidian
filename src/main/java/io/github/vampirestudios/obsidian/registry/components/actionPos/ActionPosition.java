package io.github.vampirestudios.obsidian.registry.components.actionPos;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.UseOnContext;

public interface ActionPosition {

	Codec<ActionPosition> CODEC = new Codec<>() {
		@Override
		public <T> DataResult<Pair<ActionPosition, T>> decode(DynamicOps<T> ops, T input) {
			// Case 3: "user"
			var asString = ops.getStringValue(input);
			if (asString.result().isPresent()) {
				return asString.flatMap(s ->
						ContextedPosition.CODEC.parse(ops, input)
								.map(p -> (ActionPosition) p)
				).map(p -> Pair.of(p, ops.empty()));
			}

			// Otherwise must be an object with "type"
			return ops.getMap(input).flatMap(map -> {
				T typeVal = map.get("type");
				if (typeVal == null) {
					return DataResult.error(() -> "ActionPosition object missing 'type'");
				}

				return ops.getStringValue(typeVal).flatMap(typeRaw -> {
					String type = typeRaw.toLowerCase(java.util.Locale.ROOT);
					return switch (type) {
						case "fixed" ->
								FixedPosition.CODEC.decode(ops, input).map(p -> p.mapFirst(v -> (ActionPosition) v));
						case "relative" ->
								RelativePosition.CODEC.decode(ops, input).map(p -> p.mapFirst(v -> (ActionPosition) v));
						default -> DataResult.error(() -> "Unknown ActionPosition type: " + type);
					};
				});
			});
		}

		@Override
		public <T> DataResult<T> encode(ActionPosition input, DynamicOps<T> ops, T prefix) {
			// Encode ContextedPosition as bare string:  "user"
			if (input instanceof ContextedPosition cp) {
				return ContextedPosition.CODEC.encode(cp, ops, prefix);
			}

			// Encode object forms (with "type")
			if (input instanceof FixedPosition fp) {
				return FixedPosition.CODEC.encode(fp, ops, prefix).flatMap(obj ->
						ops.mergeToMap(obj, ops.createString("type"), ops.createString("fixed"))
				);
			}

			if (input instanceof RelativePosition rp) {
				return RelativePosition.CODEC.encode(rp, ops, prefix).flatMap(obj ->
						ops.mergeToMap(obj, ops.createString("type"), ops.createString("relative"))
				);
			}

			return DataResult.error(() -> "Unknown ActionPosition impl: " + input.getClass().getName());
		}
	};

	BlockPos getPosition(UseOnContext context);
}
