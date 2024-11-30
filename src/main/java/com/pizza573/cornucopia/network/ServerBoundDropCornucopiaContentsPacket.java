package com.pizza573.cornucopia.network;

import com.pizza573.cornucopia.Cornucopia;
import com.pizza573.cornucopia.init.ModDataComponents;
import com.pizza573.cornucopia.item.components.CornucopiaContents;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerBoundDropCornucopiaContentsPacket(String message) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ServerBoundDropCornucopiaContentsPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Cornucopia.MOD_ID, "quit_cornucopia_contents_packet"));
    // 编写发包用的数据流
    public static final StreamCodec<ByteBuf, ServerBoundDropCornucopiaContentsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            ServerBoundDropCornucopiaContentsPacket::message,
            ServerBoundDropCornucopiaContentsPacket::new
    );

    // 接收客户端发来的包
    public static void handle(final ServerBoundDropCornucopiaContentsPacket packet, final IPayloadContext context)
    {
        Player player = context.player();
        if (player instanceof ServerPlayer) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (dropContents(mainHandItem, player)) {
                playDropContentsSound(player);
                // 更新玩家统计数据
                player.awardStat(Stats.ITEM_USED.get(mainHandItem.getItem()));
            }
        }
    }

    private static void playDropContentsSound(Player player)
    {
        player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + player.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    private static boolean dropContents(ItemStack stack, Player player)
    {
        CornucopiaContents cornucopiaContents = stack.get(ModDataComponents.CORNUCOPIA_CONTENTS);
        if (cornucopiaContents != null && !cornucopiaContents.isEmpty()) {
            stack.set(ModDataComponents.CORNUCOPIA_CONTENTS, CornucopiaContents.EMPTY);
            if (player instanceof ServerPlayer) {
                cornucopiaContents.itemsCopy().forEach(itemstack -> player.drop(itemstack, true));
            }
            return true;
        } else {
            return false;
        }
    }

    private void playDropContentsSound(Entity entity)
    {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

}
