package tech.sebazcrc.imposterdragonrush.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tech.sebazcrc.imposterdragonrush.Main;
import tech.sebazcrc.imposterdragonrush.Utils.ScoreHelper;
import tech.sebazcrc.imposterdragonrush.Utils.Task.AbstractTask;
import tech.sebazcrc.imposterdragonrush.Utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class GamePlayer {

    private UUID uuid;
    private String name;

    private boolean fakePlayer;
    private boolean alive;

    private int currentSubString = 0;

    private Game game;
    private Role role;

    private GamePlayer showingMate;
    private int savedHealth;
    private int posX;
    private int posZ;

    private AbstractTask task;
    private boolean completedTask;

    public GamePlayer(UUID uuid, Game game) {
        this.role = Role.NOT_DEFINED;
        this.game = game;
        this.alive = true;
        if (uuid == null) {
            setFakePlayer("FakePlayer_N" + game.getPlayers().size()+1);
            return;
        }
        this.uuid = uuid;
        this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    private void setFakePlayer(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.fakePlayer = true;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isFakePlayer() {
        return fakePlayer;
    }

    public boolean isAlive() {
        return alive;
    }

    public Game getGame() {
        return game;
    }

    public Role getRole() {
        return this.role;
    }

    public boolean isOnline() {
        if (isFakePlayer()) return true;
        return Bukkit.getOfflinePlayer(uuid).isOnline();
    }

    public OfflinePlayer getPlayer() {
        if (isFakePlayer()) return null;
        return (Bukkit.getPlayer(uuid) == null ? Bukkit.getOfflinePlayer(uuid) : Bukkit.getPlayer(uuid));
    }

    public Player getAsOnlinePlayer() {
        if (getPlayer() == null) return null;
        return ((Player)getPlayer());
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void sendMessage(String s) {
        if (!isOnline()) return;
        getAsOnlinePlayer().sendMessage(Main.prefix + Utils.format(s));
    }

    public int getCurrentSubString() {
        return currentSubString;
    }

    public void setCurrentSubString(int currentSubString) {
        this.currentSubString = currentSubString;
    }

    public void onJoin(PlayerJoinEvent e) {
    }

    public void onDeath(PlayerDeathEvent e) {
        this.setAlive(false);

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                getAsOnlinePlayer().spigot().respawn();
                getAsOnlinePlayer().setGameMode(GameMode.SPECTATOR);
            }
        }, 2L);
    }

    public boolean isSimilar(GamePlayer player) {
        return player.getUUID().toString().equalsIgnoreCase(getUUID().toString());
    }

    public void tickPlayer(int ticks) {
        if (isOnline()) {

            this.posX = getAsOnlinePlayer().getLocation().getBlockX();
            this.posZ = getAsOnlinePlayer().getLocation().getBlockZ();
            this.savedHealth = (int) Math.round(getAsOnlinePlayer().getHealth()/2);

            if ((ticks % 60) == 0) tickShowingMate();
            if (getRole() == Role.IMPOSTER) {
                getAsOnlinePlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(32.0D);
                //getAsOnlinePlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*5, 0, false, false));
                //getAsOnlinePlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*5, 0, false, false));

                if (ScoreHelper.getByUUID(uuid).getScoreboard().getObjective("sort") == null) {
                    getOrCreateImposterTeam();
                    getOrCreateInnocentTeam();
                    ScoreHelper.getByUUID(uuid).getScoreboard().registerNewObjective("sort", "dummy", "Lista");
                }
            } else {
                getAsOnlinePlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0D);
            }
        }
    }

    public int getCurrentHealth() {
        return savedHealth;
    }

    public int getPositionX() {
        return posX;
    }

    public int getPositionZ() {
        return posZ;
    }

    public void tickShowingMate() {
        if (getRole() != Role.IMPOSTER) return;
        if (getGame().getConfiguration().getMaxImposters() == 1) return;

        GamePlayer next = null;
        java.util.List<GamePlayer> imposters = game.getImposters();

        while (next == null) {
            GamePlayer t = imposters.get(game.getRandom().nextInt(imposters.size()));
            boolean bl = (imposters.size() > 2 ? (showingMate == null ? true : !showingMate.isSimilar(t)) : true);

            if (!t.isSimilar(this) && bl) next = t;
        }

        this.showingMate = next;
    }

    public String getShowingStatus(int slot) {
        if (showingMate == null) return "...";

        String health = Utils.format((showingMate.isAlive() ? "&c " + showingMate.getCurrentHealth() + " ❤" : "&7 ☠"));
        //String pos = Utils.format((showingMate.isAlive() ? "&7 (&b" + showingMate.getPositionX() + " " + showingMate.getPositionZ() + "&7)" : ""));
          String pos = Utils.format((showingMate.isAlive() ? "&b" + showingMate.getPositionX() + "&7, &b" + showingMate.getPositionZ() : ""));

        return Utils.format((slot == 1 ? showingMate.getName() + "" + health : pos));
    }


    // Actualización de ProtocolLib 1.1

    public void sortTAB() {
        if (!isOnline()) return;

        for (GamePlayer players : game.getPlayers()) {
            if (players.getRole() == Role.IMPOSTER && getRole() == Role.IMPOSTER) {
                addPlayerToTab(players, true);
            } else {
                addPlayerToTab(players, false);
            }
        }
    }

    public void addPlayerToTab(GamePlayer player, boolean imposterTab) {
        Team team = (imposterTab ? getOrCreateImposterTeam() : getOrCreateInnocentTeam());
        team.addEntry(player.getName());
    }

    private Team getOrCreateImposterTeam() {
        //return ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("01imposters") == null ? ScoreHelper.getByUUID(uuid).getScoreboard().registerNewTeam("01imposters") : ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("01imposters");
        Team team = ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("01imposters") == null ? ScoreHelper.getByUUID(uuid).getScoreboard().registerNewTeam("01imposters") : ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("01imposters");
        team.setColor(ChatColor.RED);
        return team;
    }

    private Team getOrCreateInnocentTeam() {
        //return ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("02innocents") == null ? ScoreHelper.getByUUID(uuid).getScoreboard().registerNewTeam("02innocents") : ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("02innocents");
        Team team = ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("02innocents") == null ? ScoreHelper.getByUUID(uuid).getScoreboard().registerNewTeam("02innocents") : ScoreHelper.getByUUID(uuid).getScoreboard().getTeam("02innocents");
        team.setColor(ChatColor.GREEN);
        return team;
    }

    public boolean hasCompletedTask() {
        return completedTask;
    }

    public AbstractTask getTask() {
        return task;
    }

    public void completeTask(AbstractTask task) {
        completedTask = true;
    }

    public void setTask(AbstractTask randomTask) {
        this.task = randomTask;
    }

    public boolean hasTask() {
        return task != null;
    }

}
