package tech.sebazcrc.imposterdragonrush.Game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Joiner;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import tech.sebazcrc.imposterdragonrush.Main;
import tech.sebazcrc.imposterdragonrush.Utils.ProtocolUtils;
import tech.sebazcrc.imposterdragonrush.Utils.ScoreHelper;
import tech.sebazcrc.imposterdragonrush.Utils.ScoreStringBuilder;
import tech.sebazcrc.imposterdragonrush.Utils.Task.AbstractTask;
import tech.sebazcrc.imposterdragonrush.Utils.Task.CraftItemTask;
import tech.sebazcrc.imposterdragonrush.Utils.Task.RecolectStuffTask;
import tech.sebazcrc.imposterdragonrush.Utils.Task.TravelTask;
import tech.sebazcrc.imposterdragonrush.Utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Game {

    private SplittableRandom random;

    private GameConfiguration configuration;
    private GameState state;

    private List<GamePlayer> players;

    private String name;
    private int time;
    int starting;

    public Map<Player, Integer> currentSubString;
    private List<String> lines;
    private List<AbstractTask> possibleTasks;

    private BossBar bossBar;
    private Game gi;

    public Game(String name) {
        this.gi = this;
        this.random = new SplittableRandom();
        this.configuration = new GameConfiguration(this);
        this.players = new ArrayList<>();
        this.state = GameState.WAITING;
        this.name = name;
        this.time = 0;
        this.starting = 0;

        this.lines = new ArrayList<>();
        this.currentSubString = new HashMap<>();

        this.possibleTasks = new ArrayList<>();

        possibleTasks.add(new TravelTask(World.Environment.NETHER));
        possibleTasks.add(new CraftItemTask("Pico de Diamante", Material.DIAMOND_PICKAXE));
        possibleTasks.add(new CraftItemTask("Mesa de encantamientos", Material.ENCHANTING_TABLE));
        possibleTasks.add(new CraftItemTask("Mesa de herrería", Material.SMITHING_TABLE));
        possibleTasks.add(new CraftItemTask("Mechero", Material.FLINT_AND_STEEL));
        possibleTasks.add(new CraftItemTask("Lingote de oro", Material.GOLD_INGOT));
        possibleTasks.add(new CraftItemTask("Ojo de Ender", Material.ENDER_EYE));
        possibleTasks.add(new RecolectStuffTask(2, Material.ENDER_PEARL, "Perla de ender"));
        possibleTasks.add(new RecolectStuffTask(6, Material.BLAZE_ROD, "Blaze Rod"));
        possibleTasks.add(new RecolectStuffTask(8, Material.LAPIS_LAZULI, "Lapislázuli"));
        possibleTasks.add(new RecolectStuffTask(32, Material.EMERALD, "Esmeralda"));
        possibleTasks.add(new RecolectStuffTask(24, Material.IRON_INGOT, "Hierro"));
        possibleTasks.add(new RecolectStuffTask(10, Material.OBSIDIAN, "Obsidiana"));
        possibleTasks.add(new RecolectStuffTask(3, Material.DIAMOND, "Diamante"));
        possibleTasks.add(new RecolectStuffTask(500, Material.STICK, "Palos"));

        this.bossBar = Bukkit.createBossBar(Utils.format("&f&lTAREAS COMPLETADAS"), BarColor.GREEN, BarStyle.SOLID);

        lines.add("&6&lSebazCRC Projects");
        lines.add("&e&lS&6&lebazCRC Projects");
        lines.add("&e&lS&6&lebazCRC Projects");
        lines.add("&e&lSe&6&lbazCRC Projects");
        lines.add("&e&lSeb&6&lazCRC Projects");
        lines.add("&e&lSeba&6&lzCRC Projects");
        lines.add("&e&lSebaz&6&lCRC Projects");
        lines.add("&e&lSebazC&6&lRC Projects");
        lines.add("&e&lSebazCR&6&lC Projects");
        lines.add("&e&lSebazCRC &6&lProjects");
        lines.add("&e&lSebazCRC P&6&lrojects");
        lines.add("&e&lSebazCRC Pr&6&lojects");
        lines.add("&e&lSebazCRC Pro&6&ljects");
        lines.add("&e&lSebazCRC Proj&6&lects");
        lines.add("&e&lSebazCRC Proje&6&lcts");
        lines.add("&e&lSebazCRC Projec&6&lts");
        lines.add("&e&lSebazCRC Project&6&ls");
        lines.add("&e&lSebazCRC Projects");

        ProtocolUtils.register();
    }

    public void scheduleTask(Main instance) {
        Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
            @Override
            public void run() {

                if (isState(GameState.WAITING)) {
                    if (starting > 1) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendTitle(Utils.format("&6&l" + (starting-1)), "", 1, 20, 1);
                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100.0F, 10.0F);
                        }
                        starting--;
                    } else if (starting == 1){
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.sendTitle(Utils.format("&e¡Buena suerte!"), "", 1, 20, 1);
                            p.sendMessage(Main.prefix + Utils.format("Se revelarán los roles en &615 &fminutos."));
                            p.sendMessage(Main.prefix + Utils.format("Por ahora todos son &a&lINOCENTES"));
                            p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100.0F, 1.0F);

                            GamePlayer gp = new GamePlayer(p.getUniqueId(), gi);
                            getPlayers().add(gp);
                        }

                        state = GameState.PLAYING;
                    }
                } else {
                    tickGame();
                }

                tickPlayers(time*20);

                if (time > 15*60) {

                    int completed = getCompletedTaskPlayers().size();

                    double porcent;

                    if (completed == 0) {
                        porcent = 0.0D;
                    } else {
                        porcent = Math.round(completed*100.0D/getInnocents().stream().filter(player -> player.isAlive()).count());
                    }

                    bossBar.setProgress(porcent/100);
                    for (AbstractTask task : possibleTasks) {
                        task.tick();
                    }
                }
            }
        }, 0L, 20L);
    }


    public void start() {
        this.starting = 6;
    }

    public void tickGame() {
        if (this.time >= 14*60 && this.time < 15*60) {
            if (time == (14*60)+30 || time == (14*60)+40 || (time >= (14*60)+45)) {
                broadcastWithSound("Se revelarán los &c&lROLES&f en &b"+ ((15*60)-time) + "&f segundos.");
            }
        } else if (this.time == 15*60) {
            this.players = getOnlinePlayers();

            getPlayers().removeIf(gp -> !gp.isOnline());
            assignRolesAndTasks();

            getPlayers().forEach(gamePlayer -> {
                gamePlayer.sendMessage("¡Se han revelado los Roles!");

                playSound(gamePlayer, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 100.0F, 1.0F);
                if (gamePlayer.getAsOnlinePlayer().getGameMode() != GameMode.SPECTATOR) {
                    //assignRole(gamePlayer);
                    //assignTask(gamePlayer);
                    gamePlayer.getAsOnlinePlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0));
                    sendTitle(gamePlayer, gamePlayer.getRole().getTitleName(), gamePlayer.getRole().getSubTitleName());
                } else {
                    gamePlayer.getAsOnlinePlayer().sendMessage(Main.prefix + "No has recibido un rol por que eres espectador.");
                }
            });

            // ACTUALIZACIÓN DEL SISTEMA TAB
            /**
            getPlayers().forEach(gamePlayer -> {
                if (gamePlayer.getAsOnlinePlayer().getGameMode() != GameMode.SPECTATOR) {
                    gamePlayer.sortTAB();
                }
            });

            sendTabUpdatePacket();
             */
            registerScore();
        }

        this.time++;
    }

    private void assignRolesAndTasks() {
        ArrayList<GamePlayer> nl = new ArrayList<>(this.players);
        Collections.shuffle(nl);

        Bukkit.broadcastMessage("DEBUG: ");
        Bukkit.broadcastMessage("Original: " + Joiner.on(", ").join(this.players.stream().map(gp -> gp.getName()).collect(Collectors.toList())));
        Bukkit.broadcastMessage("Nueva: " + Joiner.on(", ").join(nl.stream().map(gp -> gp.getName()).collect(Collectors.toList())));
        Bukkit.broadcastMessage("");
        for (GamePlayer gp : getPlayers()) {
            assignRole(gp);
            assignTask(gp);
        }
    }

    public void registerScore() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (GamePlayer gp : getPlayers()) {
                    gp.sortTAB();

                    OfflinePlayer p = gp.getPlayer();
                    if (p instanceof Player) {
                        Player a = (Player) p;
                        a.setScoreboard(ScoreHelper.getByPlayer(a).getScoreboard());
                    }
                }
            }
        }, 1L);
    }

    private void sendTabUpdatePacket() {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        packet.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);

        List<PlayerInfoData> dataList = new ArrayList<>();

        for (Player on : Bukkit.getOnlinePlayers()) {
            dataList.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(on), 30, EnumWrappers.NativeGameMode.fromBukkit(on.getGameMode()), WrappedChatComponent.fromText(on.getPlayerListName())));
        }

        packet.getPlayerInfoDataLists().write(0, dataList);
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
    }

    public void tickPlayers(int ticks) {

        Bukkit.getOnlinePlayers().forEach(p -> {
            if (!ScoreHelper.hasScore(p)) {
                ScoreHelper.createScore(p).setTitle("&6&lSebazCRC Projects");
            } else {
                tickScoreboard(p, ticks);
            }

            if (!isState(GameState.WAITING)) {
                if (getPlayer(p.getName()) != null) getPlayer(p.getName()).tickPlayer(ticks);
            }

            if (time > 15*60) {
                if (!bossBar.getPlayers().contains(p)) {
                    bossBar.addPlayer(p);
                }
            }
        });
    }

    private void tickScoreboard(Player p, int ticks) {

        ScoreHelper helper = ScoreHelper.getByPlayer(p);

        if (p.getScoreboard() == null || p.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            p.setScoreboard(helper.getScoreboard());
        }

        String s = getScoreboardLines(p, ticks);

        String[] split = s.split("\n");
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < split.length; ++i) {
            String str2 = split[i];
            lines.add(Utils.format(str2));
        }

        helper.setSlotsFromList(lines);

        if (!this.currentSubString.containsKey(p)) {
            this.currentSubString.put(p, 0);
        } else {
            int plus = this.currentSubString.get(p) + 1;
            if (plus > this.lines.size()-1) {
                plus = 0;
            }
            this.currentSubString.replace(p, plus);
        }

        ScoreHelper.getByPlayer(p).setTitle(Utils.format(this.lines.get(this.currentSubString.get(p))));
    }

    private String getScoreboardLines(Player p, int ticks) {

        ScoreStringBuilder b = new ScoreStringBuilder(true);

        if (state == GameState.WAITING) {
            //b.add("&6&lESPERANDO...").space().add("Jugadores:").add("&a" + Bukkit.getOnlinePlayers().size()).space();
            b.add("&6&lJugadores&7:")
                    .add("&8> &f" + Bukkit.getOnlinePlayers().size())
                    .space()
                    .add("&7La partida comenzará")
                    .add("&7en breve...")
                    .space()
                    .add("&7¡Por favor, sé paciente!").space();
        } else {
            GamePlayer gp = getPlayer(p.getName());

            if (getTime() < 15*60) {
                b.add("&6&lJugadores&7:").add("&8> &f" + Bukkit.getOnlinePlayers().size()).space();
            } else {
                if ((ticks % 60) == 0){
                    int innocents = (int) getInnocents().stream().filter(GamePlayer::isAlive).count();
                    b.add("&6&lJugadores&7:").add("&8> &f" + innocents).space();
                } else {
                    int imposters = (int) getImposters().stream().filter(GamePlayer::isAlive).count();
                    b.add("&6&lImpostores&7:").add("&8> &f" + imposters).space();
                }
            }

            if (isState(GameState.ENDED)) {
                b.add("&7¡Partida finalizada!.").space();
            } else {
                b.add("&6&lTiempo&7:")
                        .add("&8> &f" +Utils.formatInterval(time))
                        .space();
            }

            if (time > 15*60) {
                if (!isState(GameState.ENDED)) {
                    b.add("&6&lRol&7:").add("&8> &f" + gp.getRole().getScoreName()).space();

                    if (gp.getRole() == Role.IMPOSTER && getConfiguration().getMaxImposters() > 1) {
                        b.add("&6&lEquipo&7:").add("&8> &e" + gp.getShowingStatus(1)).add("&8> &b" + gp.getShowingStatus(2));
                    } else {
                        if (gp.getTask() == null) {
                            b.add("&6&lTarea&7:").add("...").space();
                        } else {
                            b.add("&6&lTarea&7:").add("&8> &f" + gp.getTask().getScoreBoardLine(gp)).space();
                        }
                    }
                }
            }
        }

        return b.build();
    }

    public void processDeath(GamePlayer player, PlayerDeathEvent e) {
        if (player == null) return;
        if (state != GameState.PLAYING) return;
        if (!player.isAlive()) {
            e.setDeathMessage(null);
            return;
        }

        String confirmation = (player.getRole() == Role.IMPOSTER ? "" : "no ");

        player.onDeath(e);
        List<GamePlayer> imposters = getImposters().stream().filter(gamePlayer -> gamePlayer.isAlive() && gamePlayer.isOnline()).collect(Collectors.toList());
        List<GamePlayer> innocents = getInnocents().stream().filter(gamePlayer -> gamePlayer.isAlive() && gamePlayer.isOnline()).collect(Collectors.toList());

        e.setDeathMessage(null);
        broadcast(Utils.format("&b" + player.getName() + " &fha muerto, " + "&f" + confirmation + "era un Impostor, quedan &6" + imposters.size() + " &fimpostor(es)."));
        playSoundForAll(Sound.ENTITY_WITHER_DEATH, 100.0F, 1.0F);

        if (innocents.size() <= 0 && imposters.size() >= 1) {
            endGame(GameEndCause.IMPOSTER_WIN);
        } else if (imposters.size() <= 0 && innocents.size() >= 1){
            endGame(GameEndCause.INNOCENT_WIN);
        }
    }

    private void endGame(GameEndCause endCause) {
        broadcast("¡La partida ha acabado!");
        this.state = GameState.ENDED;

        if (endCause == GameEndCause.FORCE) return;

        getInnocents().forEach(gamePlayer -> {
            String title = (endCause == GameEndCause.IMPOSTER_WIN ? "&c&l¡DERROTA!" : "&a&l¡VICTORIA!");
            if (gamePlayer.isOnline()) {
                sendTitle(gamePlayer, title, "");
            }

            try {
                Scoreboard b = ScoreHelper.getByUUID(gamePlayer.getUUID()).getScoreboard();
                b.getTeam("02innocents").unregister();
            } catch (Exception x) {}
        });

        getImposters().forEach(gamePlayer -> {
            String title = (endCause == GameEndCause.INNOCENT_WIN ? "&c&l¡DERROTA!" : "&a&l¡VICTORIA!");

            if (gamePlayer.isOnline()) {
                sendTitle(gamePlayer, title, "");
            }

            try {
                Scoreboard b = ScoreHelper.getByUUID(gamePlayer.getUUID()).getScoreboard();
                b.getTeam("01imposters").unregister();
                b.getTeam("02innocents").unregister();
            } catch (Exception x) {}
        });


        //sendTabUpdatePacket();
    }


    private void assignRole(GamePlayer who) {
        int maxInnocents = getOnlinePlayers().size() - getConfiguration().getMaxImposters();

        if (getInnocents().size() < maxInnocents) {
            int r = random.nextInt(3);
            if (r == 0 && getImposters().size() < getConfiguration().getMaxImposters()) {
                who.setRole(Role.IMPOSTER);
            } else {
                who.setRole(Role.INNOCENT);
            }
        } else {
            who.setRole(Role.IMPOSTER);
        }
    }

    private void assignTask(GamePlayer player) {
        if (player.getRole() == Role.INNOCENT) {
            AbstractTask randomTask = this.possibleTasks.get(random.nextInt(this.possibleTasks.size()));
            randomTask.addPlayer(player);
            player.setTask(randomTask);

            player.sendMessage(Utils.format(Main.prefix + "Tu tarea es: &e" + randomTask.applyFormatToScoreStatus(player)));
        }
    }

    public void broadcast(String s) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Main.prefix + Utils.format(s));
        });
    }

    public void broadcastWithSound(String s) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(Main.prefix + Utils.format(s));
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 1.0F);
        });
    }

    public void sendActionBarForAll(String s, boolean playSound) {
        this.getOnlinePlayers().forEach(gamePlayer -> {
            sendActionBar(gamePlayer, s, playSound);
        });
    }

    public void sendActionBar(GamePlayer gamePlayer, String s, boolean playSound) {
        Player p = gamePlayer.getAsOnlinePlayer();
        p.sendActionBar(Utils.format(s));
        if (playSound) p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 10.0F, 1.0F);
    }

    public void sendTitleForAll(String title, String subtitle) {
        this.getOnlinePlayers().forEach(gamePlayer -> {
            sendTitle(gamePlayer, title, subtitle);
        });
    }

    public void sendTitle(GamePlayer gamePlayer, String title, String subtitle) {
        Player p = gamePlayer.getAsOnlinePlayer();
        p.sendTitle(Utils.format(title), Utils.format(subtitle), 1, 20*5, 10);
    }

    public void playSoundForAll(Sound s, Float volume, Float pitch) {
        this.getOnlinePlayers().forEach(gamePlayer -> {
            playSound(gamePlayer, s, volume, pitch);
        });
    }

    public void playSound(GamePlayer gamePlayer, Sound s, Float volume, Float pitch) {
        Player p = gamePlayer.getAsOnlinePlayer();
        p.playSound(p.getLocation(), s, volume, pitch);
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }

    public GamePlayer getPlayer(String name) {
        for (GamePlayer l : getPlayers()) {
            if (l.getName().equalsIgnoreCase(name)) return l;
        }

        return null;
    }

    public List<GamePlayer> getCompletedTaskPlayers() {
        return getPlayers().stream().filter(player -> player.isAlive() && player.getRole() == Role.INNOCENT && player.hasTask() && player.hasCompletedTask()).collect(Collectors.toList());
    }

    public boolean hasFinishedTasks() {
        if (getTime() < 15*60 + 1) {
            return false;
        }
        return getCompletedTaskPlayers().size() >= getAlivePlayers().size();
    }

    public List<GamePlayer> getOnlinePlayers() {
        return getPlayers().stream().filter(gamePlayer -> gamePlayer.isOnline()).collect(Collectors.toList());
    }

    public List<GamePlayer> getAlivePlayers() {
        return getPlayers().stream().filter(gamePlayer -> gamePlayer.isAlive()).collect(Collectors.toList());
    }

    public List<GamePlayer> getFakePlayers() {
        return getPlayers().stream().filter(gamePlayer -> gamePlayer.isFakePlayer()).collect(Collectors.toList());
    }

    public List<GamePlayer> getImposters() {
        return getPlayers().stream().filter(gamePlayer -> gamePlayer.getRole() == Role.IMPOSTER).collect(Collectors.toList());
    }

    public List<GamePlayer> getInnocents() {
        return getPlayers().stream().filter(gamePlayer -> gamePlayer.getRole() == Role.INNOCENT).collect(Collectors.toList());
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public boolean isState(GameState state) {
        return this.state == state;
    }

    public SplittableRandom getRandom() {
        return random;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public GameState getState() {
        return this.state;
    }

    public void broadcastImposters() {
        List<String> imp = Main.getInstance().getGame().getImposters().stream().map(player1 -> player1.getName()).collect(Collectors.toList());

        Collections.sort(imp, String.CASE_INSENSITIVE_ORDER);
        String list = Joiner.on(", ").join(imp);

        broadcastWithSound("Los impostores son: &3" + list + "&f.");
    }

    public enum GameState {
        WAITING, PLAYING, ENDED
    }

    public enum GameEndCause {
        FORCE, IMPOSTER_WIN, INNOCENT_WIN
    }
}
