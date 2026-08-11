package yofred.dev.justessentials.mixin;

import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yofred.dev.justessentials.ActivityMessages;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ConnectionMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void justessentials$filterVanillaActivity(Packet<?> packet, CallbackInfo ci) {
        if (isVanillaActivity(packet)) ci.cancel();
    }

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("HEAD"), cancellable = true)
    private void justessentials$filterVanillaActivity(Packet<?> packet, PacketSendListener callback, CallbackInfo ci) {
        if (isVanillaActivity(packet)) ci.cancel();
    }

    private static boolean isVanillaActivity(Packet<?> packet) {
        if (!ActivityMessages.replacesVanilla() || !(packet instanceof ClientboundSystemChatPacket system)) return false;
        Component content = system.content();
        if (!(content.getContents() instanceof TranslatableContents translated)) return false;
        return "multiplayer.player.joined".equals(translated.getKey()) || "multiplayer.player.left".equals(translated.getKey());
    }
}
