package selim.geyser.core.bukkit;

import java.io.IOException;

import javax.annotation.Nullable;

import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;

public class BukkitByteBufUtils {

	/**
	 * Write an {@link ItemStack} using minecraft compatible encoding.
	 *
	 * @param to
	 *            The buffer to write to
	 * @param stack
	 *            The itemstack to write
	 */
	public static void writeItemStack(ByteBuf to, ItemStack stack) {
		PacketDataSerializer serial = new PacketDataSerializer(to);
		serial.a(CraftItemStack.asNMSCopy(stack));
		// BukkitPacketBuffer pb = new BukkitPacketBuffer(to);
		// System.out.println("writing ItemStack 1");
		// pb.writeItemStack(stack);
	}

	/**
	 * Read an {@link ItemStack} from the byte buffer provided. It uses the
	 * minecraft encoding.
	 *
	 * @param from
	 *            The buffer to read from
	 * @return The itemstack read
	 */
	public static ItemStack readItemStack(ByteBuf from) {
		BukkitPacketBuffer pb = new BukkitPacketBuffer(from);
		try {
			return pb.readItemStack();
		} catch (IOException e) {
			// Unpossible?
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write an {@link NbtCompound} to the byte buffer. It uses the minecraft
	 * encoding.
	 *
	 * @param to
	 *            The buffer to write to
	 * @param tag
	 *            The tag to write
	 */
	public static void writeTag(ByteBuf to, NbtCompound tag) {
		BukkitPacketBuffer pb = new BukkitPacketBuffer(to);
		pb.writeCompoundTag(tag);
	}

	/**
	 * Read an {@link NbtCompound} from the byte buffer. It uses the minecraft
	 * encoding.
	 *
	 * @param from
	 *            The buffer to read from
	 * @return The read tag
	 */
	@Nullable
	public static NbtCompound readTag(ByteBuf from) {
		BukkitPacketBuffer pb = new BukkitPacketBuffer(from);
		try {
			return pb.readCompoundTag();
		} catch (IOException e) {
			// Unpossible?
			throw new RuntimeException(e);
		}
	}

}
