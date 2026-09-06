package io.github.vampirestudios.obsidian.addonapi.model;

public class ActionDefinition {
	public String action;          // "run_command", "open_menu", "close_menu", "send_message", "client_quit"
	public String command;         // for run_command
	public String menu;            // for open_menu
	public String target;          // "self", "arg:<name>"
	public boolean fallback_to_self;

	public TextComponentDef message; // for send_message
}
