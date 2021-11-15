package io.github.vampirestudios.obsidian.client;

import com.google.gson.JsonElement;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Consumer;

public class AssetJsonGenerator extends JsonGenerator {
	public AssetJsonGenerator(Map<Identifier, JsonElement> m) {
		super(Obsidian.LOGGER, m);
	}

	public void blockState(Identifier id, Consumer<VariantBlockStateGenerator> consumer) {
		VariantBlockStateGenerator gen = new VariantBlockStateGenerator();
		consumer.accept(gen);
		json(new Identifier(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void multipartState(Identifier id, Consumer<MultipartBlockStateGenerator> consumer) {
		MultipartBlockStateGenerator gen = new MultipartBlockStateGenerator();
		consumer.accept(gen);
		json(new Identifier(id.getNamespace(), "blockstates/" + id.getPath()), gen.toJson());
	}

	public void blockModel(Identifier id, Consumer<ModelGenerator> consumer) {
		ModelGenerator gen = new ModelGenerator();
		consumer.accept(gen);
		json(new Identifier(id.getNamespace(), "models/block/" + id.getPath()), gen.toJson());
	}

	public void itemModel(Identifier id, Consumer<ModelGenerator> consumer) {
		ModelGenerator gen = new ModelGenerator();
		consumer.accept(gen);
		json(new Identifier(id.getNamespace(), "models/item/" + id.getPath()), gen.toJson());
	}
}