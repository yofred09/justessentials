package yofred.dev.justessentials;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;

final class TeleportScheduler {
    private record Pending(long executeAt, String dimension, double x, double y, double z, Runnable action) {}
    private static final Map<UUID, Pending> PENDING = new ConcurrentHashMap<>();
    static void schedule(ServerPlayer player, Runnable action) {
        int seconds=EssentialsConfig.TELEPORT_WARMUP.get(); if(seconds<=0){action.run();return;}
        PENDING.put(player.getUUID(),new Pending(System.currentTimeMillis()+seconds*1000L,player.level().dimension().location().toString(),player.getX(),player.getY(),player.getZ(),action));
        player.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_TELEPORT_WARMUP.get(),java.util.Map.of("seconds",Integer.toString(seconds))));
    }
    static void tick(ServerPlayer player) {
        Pending p=PENDING.get(player.getUUID()); if(p==null)return; double tolerance=EssentialsConfig.TELEPORT_MOVE_TOLERANCE.get();
        if(!p.dimension().equals(player.level().dimension().location().toString())||player.distanceToSqr(p.x(),p.y(),p.z())>tolerance*tolerance){PENDING.remove(player.getUUID());player.sendSystemMessage(Messages.message(EssentialsConfig.MESSAGE_TELEPORT_CANCELLED.get()));return;}
        if(System.currentTimeMillis()>=p.executeAt()&&PENDING.remove(player.getUUID(),p))p.action().run();
    }
    static void cancel(ServerPlayer player){PENDING.remove(player.getUUID());}
    private TeleportScheduler(){}
}
