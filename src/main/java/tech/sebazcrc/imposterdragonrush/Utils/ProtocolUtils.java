package tech.sebazcrc.imposterdragonrush.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import tech.sebazcrc.imposterdragonrush.Game.Game;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Game.Role;
import tech.sebazcrc.imposterdragonrush.Main;

import java.util.*;

public class ProtocolUtils {
    private static ProtocolUtils protocolUtils;

    public ProtocolUtils() {
        if (protocolUtils != null) return;

        protocolUtils = this;

        /**
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.isPlayerTemporary()) return;

                Player p = event.getPlayer();
                boolean isPlayerImposter = (Main.getInstance().getGame().isState(Game.GameState.PLAYING) ? Main.getInstance().getGamePlayer(p.getName()).getRole() == Role.IMPOSTER : false);

                List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();
                List<PlayerInfoData> playerInfoDataList = event.getPacket().getPlayerInfoDataLists().read(0);

                for (PlayerInfoData playerInfoData : playerInfoDataList) {

                    //if (playerInfoData == null || playerInfoData.getProfile() == null || Bukkit.getPlayer(playerInfoData.getProfile().getUUID()) == null) {
                        //newPlayerInfoDataList.add(playerInfoData);
                        //continue;
                    //}
                    WrappedGameProfile profile = playerInfoData.getProfile();

                    Player player = Bukkit.getPlayer(profile.getUUID());
                    boolean isPacketImposter = (Main.getInstance().getGame().isState(Game.GameState.PLAYING) ? Main.getInstance().getGamePlayer(player.getName()).getRole() == Role.IMPOSTER : false);

                    String color = Utils.format((isPacketImposter ? (isPlayerImposter ? "&c" : "&a") : "&a"));

                    try {
                        if (profile != null) {
                            profile = profile.withName(player.getName());
                        }
                    } catch (Exception x) {}

                    PlayerInfoData newPlayerInfoData = new PlayerInfoData(profile, playerInfoData.getLatency(), playerInfoData.getGameMode(), WrappedChatComponent.fromText(color + profile.getName()));
                    newPlayerInfoDataList.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().write(0, newPlayerInfoDataList);
            }
        });
         */
    }

    public static void register() {
        if (protocolUtils != null) {
            ProtocolLibrary.getProtocolManager().removePacketListeners(Main.getInstance());
            protocolUtils = null;
        }

        protocolUtils = new ProtocolUtils();
    }
}
