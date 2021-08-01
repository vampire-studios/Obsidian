package io.github.vampirestudios.obsidian.api.dataexchange;

import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class DataHandlerDescriptor {
	public DataHandlerDescriptor(Identifier identifier, Supplier<DataHandler> instancer){
		this(identifier, instancer, instancer, false);
	}

	public DataHandlerDescriptor(Identifier identifier, Supplier<DataHandler> instancer, boolean sendOnJoin){
		this(identifier, instancer, instancer, sendOnJoin);
	}
	public DataHandlerDescriptor(Identifier identifier, Supplier<DataHandler> receiv_instancer, Supplier<DataHandler> join_instancer, boolean sendOnJoin){
		this.INSTANCE = receiv_instancer;
		this.JOIN_INSTANCE = join_instancer;
		this.IDENTIFIER = identifier;

		this.sendOnJoin = sendOnJoin;
	}

	public final boolean sendOnJoin;
	public final Identifier IDENTIFIER;
	public final Supplier<DataHandler> INSTANCE;
	public final Supplier<DataHandler> JOIN_INSTANCE;
}