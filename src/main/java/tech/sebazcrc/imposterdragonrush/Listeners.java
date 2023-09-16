package tech.sebazcrc.imposterdragonrush;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tech.sebazcrc.imposterdragonrush.Game.Game;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Utils.Utils;

import java.util.SplittableRandom;

public class Listeners implements Listener {

    private Main instance;
    private SplittableRandom random = new SplittableRandom();

    public Listeners() {
        this.instance = Main.getInstance();
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (instance.getGamePlayer(e.getPlayer().getName()) == null && instance.getGame().getTime() > 15*20) {
            e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            e.setKickMessage(Utils.format("&cLa partida ya ha elegido los roles."));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (Main.getInstance().getGamePlayer(e.getPlayer().getName()) == null) {
            if (!Main.getInstance().getGame().isState(Game.GameState.WAITING)) {
                GamePlayer gp = new GamePlayer(e.getPlayer().getUniqueId(), Main.getInstance().getGame());
                Main.getInstance().getGame().getPlayers().add(gp);
            }

            /**
            Player p = e.getPlayer();
            for (Player on : Bukkit.getOnlinePlayers()) {
                addPlayerHide(p, on);
                addPlayerHide(on, p);
            }
             */
        }
    }

    private void addPlayerHide(Player p, Player on) {
        Scoreboard board = p.getScoreboard();

        Team team = board.getTeam("notag") == null ? board.registerNewTeam("notag") : board.getTeam("notag");
        if (!team.hasEntry(on.getPlayer().getName())) {
            team.addEntry(on.getPlayer().getName());
        }

        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        GamePlayer gp = instance.getGamePlayer(e.getEntity().getName());
        if (gp != null) {
            instance.getGame().processDeath(gp, e);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp() && Main.getInstance().getGame().getState() == Game.GameState.WAITING) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPVP(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (Main.getInstance().getGame().getState() == Game.GameState.WAITING) {
                e.setCancelled(true);
            }

            if (e.getCause() == EntityDamageEvent.DamageCause.VOID && ((Player) e.getEntity()).getGameMode() == GameMode.SPECTATOR) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getEntity() instanceof Monster && e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && e.getLocation().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (random.nextInt(750) != 1) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            for (Player on : Bukkit.getOnlinePlayers()) {
                if (on.getGameMode() == GameMode.SPECTATOR) {
                    on.sendMessage(Utils.format("&7[ESPECTADOR] " + e.getPlayer().getName() + " &8> &f" + e.getMessage()));
                }
            }
        } else {
            for (Player on : Bukkit.getOnlinePlayers()) {
                on.sendMessage(Utils.format("&7" + e.getPlayer().getName() + " &8> &f" + e.getMessage()));
            }
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if (Main.getInstance().getGame().getState() == Game.GameState.WAITING) {
            e.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent e) {
        String cmd = e.getMessage().replace("/", "");
        if (cmd.startsWith("tell") || cmd.startsWith("msg") || cmd.startsWith("minecraft:tell") || cmd.startsWith("minecraft:msg") || cmd.startsWith("me") || cmd.startsWith("minecraft:me")) {
            e.getPlayer().sendMessage(Utils.format("&cEse comando está deshabilitado."));
            e.setCancelled(true);
        }
    }
}
