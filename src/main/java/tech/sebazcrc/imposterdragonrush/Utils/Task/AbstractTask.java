package tech.sebazcrc.imposterdragonrush.Utils.Task;

import com.google.common.base.Joiner;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Main;
import tech.sebazcrc.imposterdragonrush.Utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTask {

    private List<GamePlayer> handlingPlayers;

    private TaskType type;
    private String scoreboardBasicStatus;

    public AbstractTask(TaskType type, String scoreboardBasicStatus) {
        this.handlingPlayers = new ArrayList<>();
        this.type = type;
        this.scoreboardBasicStatus = scoreboardBasicStatus;
    }

    public void tick() {}

    public void complete(GamePlayer player) {
        if (!hasCompleted(player)) {
            player.completeTask(this);
            Main.getInstance().getGame().broadcastWithSound("&fEl jugador &3&k" + player.getName() + "&r&f ha completado su tarea.");

            if (Main.getInstance().getGame().hasFinishedTasks()) {
                Main.getInstance().getGame().broadcastImposters();
            }
        }
    }

    public boolean hasCompleted(GamePlayer player) {
        return player.hasCompletedTask();
    }

    public int getItemAmount(GamePlayer player, Material mat) {
        if (!player.isOnline()) return 0;

        int amount = 0;

        Player p = player.getAsOnlinePlayer();
        HashMap<Integer, ? extends ItemStack> map = p.getInventory().all(mat);

        for (Integer slot : map.keySet()) {
            amount+=p.getInventory().getItem(slot).getAmount();
        }

        return amount;
    }

    public String applyFormatToScoreStatus(GamePlayer player) {
        return scoreboardBasicStatus;
    }

    public String getScoreBoardLine(GamePlayer player) {
        String s = applyFormatToScoreStatus(player);

        if (hasCompleted(player)) {
            s = "&a&n" + s;
        } else {
            s = "&e" + s;
        }

        return Utils.format(s);
    }

    public List<GamePlayer> getHandlingPlayers() {
        return handlingPlayers;
    }

    public TaskType getType() {
        return type;
    }

    public String getScoreboardBasicStatus() {
        return scoreboardBasicStatus;
    }

    public void addPlayer(GamePlayer player) {
        this.handlingPlayers.add(player);
    }
}
