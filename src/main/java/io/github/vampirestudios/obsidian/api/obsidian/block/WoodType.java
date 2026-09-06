package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class WoodType {
	public Identifier id;

	@SerializedName("set_type")
	public Identifier setType;
	@SerializedName("sound_type")
	public Identifier soundType;
	@SerializedName("hanging_sign_sound_type")
	public Identifier hangingSignSoundType;
	@SerializedName("fence_gate_close_sound_type")
	public Identifier fenceGateClose;
	@SerializedName("fence_gate_open_sound_type")
	public Identifier fenceGateOpen;
}
