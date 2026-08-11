package yofred.dev.justessentials;

import net.minecraft.server.level.ServerPlayer;
import yofred.dev.justcore.api.JustCoreApi;

final class JustVanishCompat {
    private static final String STATE = "vanish";
    private static final String PREVIOUS = "StaffPreviousVanish";
    static void enterStaffMode(ServerPlayer player) { if(!available()||!EssentialsConfig.JUST_VANISH_STAFF_MODE.get())return; boolean previous=isVanishedForTab(player); PlayerState.setIntegrationFlag(player,PREVIOUS,previous); JustCoreApi.setState(STATE,player,true); }
    static void leaveStaffMode(ServerPlayer player) { if(!available()||!EssentialsConfig.JUST_VANISH_STAFF_MODE.get())return; JustCoreApi.setState(STATE,player,PlayerState.integrationFlag(player,PREVIOUS)); PlayerState.removeIntegrationFlag(player,PREVIOUS); }
    static boolean available(){return JustCoreApi.playerState(STATE).isPresent();}
    static boolean canSee(ServerPlayer viewer,ServerPlayer target){return JustCoreApi.canViewState(STATE,viewer,target);}
    static boolean isVanishedForTab(ServerPlayer player){return JustCoreApi.hasState(STATE,player);}
    private JustVanishCompat(){}
}
