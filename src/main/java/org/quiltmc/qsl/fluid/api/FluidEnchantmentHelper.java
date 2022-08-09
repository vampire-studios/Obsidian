package org.quiltmc.qsl.fluid.api;

public record FluidEnchantmentHelper(float horizontalViscosity, float speed) {
	public float getHorizontalViscosity() {
		return horizontalViscosity;
	}

	public float getSpeed() {
		return speed;
	}
}