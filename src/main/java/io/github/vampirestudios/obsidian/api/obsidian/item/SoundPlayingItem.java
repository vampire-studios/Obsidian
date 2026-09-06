package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;

/**
 * POJO for items that play a data-driven sound — either a music disc or a goat
 * horn (and any future variants). The {@code type} field selects the behaviour;
 * {@code sound} provides the resource key of the relevant data-driven entry.
 *
 * <h3>Types</h3>
 * <dl>
 *   <dt>{@code "music_disc"}</dt>
 *   <dd>{@code sound} is a {@code JukeboxSong} resource key
 *       (e.g. {@code "mymod:my_song"}).  Define the song in
 *       {@code data/<namespace>/jukebox_song/<name>.json}.</dd>
 *   <dt>{@code "goat_horn"}</dt>
 *   <dd>{@code sound} is an {@code Instrument} resource key
 *       (e.g. {@code "minecraft:ponder_goat_horn"}).  Define the instrument in
 *       {@code data/<namespace>/instrument/<name>.json}.</dd>
 * </dl>
 *
 * <h3>JSON examples</h3>
 * <pre>{@code
 * // Music disc
 * {
 *   "sound_type": "music_disc",
 *   "sound": "mymod:my_song",
 *   "information": { ... }
 * }
 *
 * // Goat horn
 * {
 *   "sound_type": "goat_horn",
 *   "sound": "minecraft:ponder_goat_horn",
 *   "information": { ... }
 * }
 * }</pre>
 */
public class SoundPlayingItem extends Item {

	/**
	 * Which kind of sound-playing item to create. Required.
	 * Valid values: {@code "music_disc"}, {@code "goat_horn"}.
	 */
	@SerializedName("sound_type")
	public String sound_type;

	/**
	 * Resource key of the data-driven sound entry. Required.
	 * Interpreted as a {@code JukeboxSong} key for {@code "music_disc"}, or as
	 * an {@code Instrument} key for {@code "goat_horn"}.
	 */
	@SerializedName("sound")
	public String sound;

}
