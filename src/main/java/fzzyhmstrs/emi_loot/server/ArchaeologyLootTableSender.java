package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.util.cleancode.Identifier;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.connection.ConnectionType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ArchaeologyLootTableSender implements LootSender<ArchaeologyLootPoolBuilder> {

	private final String idToSend;
	final List<ArchaeologyLootPoolBuilder> builderList = new LinkedList<>();
	HashMap<ItemStack, Float> floatMap = new HashMap<>();
	public static ResourceLocation ARCHAEOLOGY_SENDER = Identifier.of("e_1", "a_s");

	public ArchaeologyLootTableSender(ResourceLocation id) {
		this.idToSend = LootSender.getIdToSend(id);
	}

	@Override
	public String getId() {
		return idToSend;
	}

	@Override
	public void send(ServerPlayer player) {
		if (!PacketSender.s2c(player).canSend(ARCHAEOLOGY_SENDER)) return;
		RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), player.server.registryAccess(), ConnectionType.NEOFORGE);
		buf.writeUtf(idToSend);
		buf.writeShort(floatMap.size());
		floatMap.forEach((item, floatWeight) -> {
			ItemStack.STREAM_CODEC.encode(buf, item);
			buf.writeFloat(floatWeight);
		});
		PacketSender.s2c(player).send(ARCHAEOLOGY_SENDER, buf);
	}

	@Override
	public void addBuilder(ArchaeologyLootPoolBuilder builder) {
		builderList.add(builder);
	}

	@Override
	public List<ArchaeologyLootPoolBuilder> getBuilders() {
		return builderList;
	}

	@Override
	public void build() {
		builderList.forEach(builder -> {
			builder.build();
			builder.builtMap.forEach((item, weight) -> {
				if(floatMap.containsKey(item)) {
					float oldWeight = floatMap.getOrDefault(item, 0f);
					floatMap.put(item, oldWeight + weight);
				} else {
					floatMap.put(item, weight);
				}
			});
		});
	}
}