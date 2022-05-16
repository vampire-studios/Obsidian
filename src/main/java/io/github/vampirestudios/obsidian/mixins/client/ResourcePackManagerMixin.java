package io.github.vampirestudios.obsidian.mixins.client;

import com.google.common.collect.ImmutableSet;
import io.github.vampirestudios.obsidian.client.resource.ObsidianAddonResourcePackProvider;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import net.minecraft.client.resource.ClientBuiltinResourcePackProvider;
import net.minecraft.resource.pack.ResourcePackManager;
import net.minecraft.resource.pack.ResourcePackProfile;
import net.minecraft.resource.pack.ResourcePackProvider;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {

	@Shadow private List<ResourcePackProfile> enabled;
	@Shadow private Map<String, ResourcePackProfile> profiles;
	@Shadow @Final private Set<ResourcePackProvider> providers;

	/**
	 * Injects our custom resource pack provider.
	 */
	@Redirect(method = "<init>(Lnet/minecraft/resource/pack/ResourcePackProfile$Factory;[Lnet/minecraft/resource/pack/ResourcePackProvider;)V",
			at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
	private <E> ImmutableSet<Object> injectSPResourcePack(E[] elements) {
		//System.out.println("### injectSPResourcePack");
		boolean isClient = false;
		boolean providerPresent = false;

//		ObsidianAddonResourcePackProvider clientProvider;
//		SPServerResourcePackProvider serverProvider;

		for (E element : elements) {
			if (element instanceof ClientBuiltinResourcePackProvider) {
				isClient = true;
			}

			if (element instanceof ObsidianAddonResourcePackProvider) {
				isClient = true;
				providerPresent = true;
				break;
			}

//			if (element instanceof SPServerResourcePackProvider) {
//				providerPresent = true;
//				break;
//			}
		}

		if (!providerPresent) {
			if (isClient) {
				System.out.println("### adding client");
				ObsidianAddonLoader.OBSIDIAN_ADDONS.stream().forEach(addonPack ->
						ArrayUtils.add(elements, new ObsidianAddonResourcePackProvider(addonPack)));
				return ImmutableSet.copyOf(elements);
			}/* else {
				//System.out.println("### adding server");
				serverProvider = new SPServerResourcePackProvider();
				return ImmutableSet.copyOf(ArrayUtils.add(elements, serverProvider));
			}*/
		}

		return ImmutableSet.copyOf(elements);
	}

}
