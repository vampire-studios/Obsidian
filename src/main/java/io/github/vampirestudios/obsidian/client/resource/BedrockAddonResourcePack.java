package io.github.vampirestudios.obsidian.client.resource;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.bedrock.IBedrockAddon;
import io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class BedrockAddonResourcePack implements ResourcePack {
	protected ResourcePack[] virtualPacks;

	public BedrockAddonResourcePack() {
		virtualPacks = BedrockAddonLoader.BEDROCK_ADDONS.stream().map(IBedrockAddon::getVirtualResourcePack).filter(Objects::nonNull).toArray(ResourcePack[]::new);
	}

	@Override
	public InputStream openRoot(String var1) throws IOException {
		for(ResourcePack virtualPack : virtualPacks) {
			try {
				return virtualPack.openRoot(var1);
			} catch (Throwable ignored) {}
		}
		throw new FileNotFoundException();
	}

	@Override
	public InputStream open(ResourceType var1, Identifier var2) throws IOException {
		for(ResourcePack virtualPack : virtualPacks) {
			try {
				return virtualPack.open(var1, var2);
			} catch (Throwable ignored) {}
		}
		throw new FileNotFoundException();
	}

	@Override
	public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
		Set<Identifier> resources = new HashSet<>();
		for(ResourcePack virtualPack : virtualPacks) {
			resources.addAll(virtualPack.findResources(type, namespace, prefix, maxDepth, pathFilter));
		}
		return resources;
	}

	@Override
	public boolean contains(ResourceType var1, Identifier var2) {
		for(ResourcePack virtualPack : virtualPacks) {
			if(virtualPack.contains(var1, var2))
				return true;
		}
		return false;
	}

	@Override
	public Set<String> getNamespaces(ResourceType var1) {
		Set<String> namespaces = new HashSet<>();
		for(ResourcePack virtualPack : virtualPacks) {
			namespaces.addAll(virtualPack.getNamespaces(var1));
		}
		return namespaces;
	}

	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metadataReader) {
		JsonObject object = new JsonObject();
		if(metadataReader.getKey().equals("pack")) {
			object.addProperty("description", "Default pack for bedrock addons.");
			object.addProperty("pack_format", 7);
		}
		return metadataReader.fromJson(object);
	}

	@Override
	public String getName() {
		return "obsidian/bedrock_addons";
	}

	@Override
	public void close() {
		for(ResourcePack virtualPack : virtualPacks) {
			try {
				virtualPack.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}