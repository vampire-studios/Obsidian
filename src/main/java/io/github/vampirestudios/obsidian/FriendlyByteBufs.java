/*
 * Copyright (c) 2023 OliviaTheVampire
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.vampirestudios.obsidian;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

/**
 * Helper methods for working with and creating {@link FriendlyByteBuf}s.
 */
public final class FriendlyByteBufs {
	private static final FriendlyByteBuf EMPTY_PACKET_BYTE_BUF = new FriendlyByteBuf(Unpooled.EMPTY_BUFFER);

	/**
	 * Returns an empty instance of packet byte buffer.
	 *
	 * @return an empty buffer
	 */
	public static FriendlyByteBuf empty() {
		return EMPTY_PACKET_BYTE_BUF;
	}

	/**
	 * Returns a new heap memory-backed instance of packet byte buffer.
	 *
	 * @return a new buffer
	 */
	public static FriendlyByteBuf create() {
		return new FriendlyByteBuf(Unpooled.buffer());
	}

	// Convenience methods for byte buffer methods that return a new byte buffer

	/**
	 * Wraps the newly created buf from {@code buf.readBytes} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param length the number of bytes to transfer
	 * @return the transferred bytes
	 * @see ByteBuf#readBytes(int)
	 */
	public static FriendlyByteBuf readBytes(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.readBytes(length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.readSlice} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readSlice(int)
	 */
	public static FriendlyByteBuf readSlice(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.readSlice(length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.readRetainedSlice} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param length the size of the new slice
	 * @return the newly created slice
	 * @see ByteBuf#readRetainedSlice(int)
	 */
	public static FriendlyByteBuf readRetainedSlice(ByteBuf buf, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.readRetainedSlice(length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.copy} in a packet byte buffer.
	 *
	 * @param buf the original buffer
	 * @return a copy of the buffer
	 * @see ByteBuf#copy()
	 */
	public static FriendlyByteBuf copy(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.copy());
	}

	/**
	 * Wraps the newly created buffer from {@code buf.copy} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a copy of the buffer
	 * @see ByteBuf#copy(int, int)
	 */
	public static FriendlyByteBuf copy(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.copy(index, length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.slice} in a packet byte buffer.
	 *
	 * @param buf the original buffer
	 * @return a slice of the buffer
	 * @see ByteBuf#slice()
	 */
	public static FriendlyByteBuf slice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.slice());
	}

	/**
	 * Wraps the newly created buffer from {@code buf.retainedSlice} in a packet byte buffer.
	 *
	 * @param buf the original buffer
	 * @return a slice of the buffer
	 * @see ByteBuf#retainedSlice()
	 */
	public static FriendlyByteBuf retainedSlice(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.retainedSlice());
	}

	/**
	 * Wraps the newly created buffer from {@code buf.slice} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buffer
	 * @see ByteBuf#slice(int, int)
	 */
	public static FriendlyByteBuf slice(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.slice(index, length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.retainedSlice} in a packet byte buffer.
	 *
	 * @param buf    the original buffer
	 * @param index  the starting index
	 * @param length the size of the copy
	 * @return a slice of the buffer
	 * @see ByteBuf#retainedSlice(int, int)
	 */
	public static FriendlyByteBuf retainedSlice(ByteBuf buf, int index, int length) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.retainedSlice(index, length));
	}

	/**
	 * Wraps the newly created buffer from {@code buf.duplicate} in a packet byte buffer.
	 *
	 * @param buf the original buffer
	 * @return a duplicate of the buffer
	 * @see ByteBuf#duplicate()
	 */
	public static FriendlyByteBuf duplicate(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.duplicate());
	}

	/**
	 * Wraps the newly created buffer from {@code buf.retainedDuplicate} in a packet byte buffer.
	 *
	 * @param buf the original buffer
	 * @return a duplicate of the buffer
	 * @see ByteBuf#retainedDuplicate()
	 */
	public static FriendlyByteBuf retainedDuplicate(ByteBuf buf) {
		Objects.requireNonNull(buf, "ByteBuf cannot be null");

		return new FriendlyByteBuf(buf.retainedDuplicate());
	}

	private FriendlyByteBufs() {
	}
}