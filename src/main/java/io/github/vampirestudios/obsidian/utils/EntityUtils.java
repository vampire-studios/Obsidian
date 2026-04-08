package io.github.vampirestudios.obsidian.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;

public class EntityUtils {

    public static AttributeSupplier.Builder createGenericEntityAttributes(double maxHealth, double movementSpeed, Map<String, Double> customAttributes) {
        AttributeSupplier.Builder builder = PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, movementSpeed)
                .add(Attributes.MAX_HEALTH, maxHealth);

        if (customAttributes == null || customAttributes.isEmpty()) return builder;

        for (Map.Entry<String, Double> entry : customAttributes.entrySet()) {
            Identifier attributeId = Identifier.tryParse(entry.getKey());
            if (attributeId == null || entry.getValue() == null) continue;
            Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.get(attributeId).orElseThrow();
            if (!attribute.isBound()) continue;
            builder.add(attribute, entry.getValue());
        }

        return builder;
    }

}
