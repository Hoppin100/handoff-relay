package com.kingra007.handoffrelay.mixin;

import com.kingra007.handoffrelay.HandoffState;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class BlockCommandMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
    private void handoffRelay$blockDangerousCommands(ServerboundChatCommandPacket packet, CallbackInfo ci) {
        String command = packet.command().trim().toLowerCase();

        if (isBlockedCommand(command)) {
            MinecraftServer server = player.level().getServer();

            if (server == null) {
                return;
            }

            HandoffState state = HandoffState.load(server);
            state.integrityLocked = true;
            state.integrityLockReason = "Blocked command tampering detected: /" + command;
            state.save(server);

            player.connection.disconnect(Component.literal(
                    "Handoff Relay integrity lock: blocked command tampering detected."
            ));

            ci.cancel();
        }
    }

    private static boolean isBlockedCommand(String command) {
        return command.equals("give")
                || command.startsWith("give ")
                || command.equals("gamemode")
                || command.startsWith("gamemode ")
                || command.equals("tp")
                || command.startsWith("tp ")
                || command.equals("teleport")
                || command.startsWith("teleport ")
                || command.equals("effect")
                || command.startsWith("effect ")
                || command.equals("enchant")
                || command.startsWith("enchant ")
                || command.equals("summon")
                || command.startsWith("summon ")
                || command.equals("setblock")
                || command.startsWith("setblock ")
                || command.equals("fill")
                || command.startsWith("fill ")
                || command.equals("item")
                || command.startsWith("item ")
                || command.equals("experience")
                || command.startsWith("experience ")
                || command.equals("xp")
                || command.startsWith("xp ")
                || command.equals("advancement")
                || command.startsWith("advancement ")
                || command.equals("op")
                || command.startsWith("op ")
                || command.equals("deop")
                || command.startsWith("deop ");
    }
}