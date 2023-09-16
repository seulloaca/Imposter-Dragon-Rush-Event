package tech.sebazcrc.imposterdragonrush.Utils.Task;

import org.bukkit.Material;
import tech.sebazcrc.imposterdragonrush.Game.Game;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Main;

import java.util.HashMap;

public class RecolectStuffTask extends AbstractTask {

    private HashMap<GamePlayer, Integer> players;

    private int collectAmount;
    private Material material;

    public RecolectStuffTask(int collectAmount, Material material, String mat) {
        super(TaskType.COLLECT_STUFF,  mat + " (%current%/%needed%)");
        this.collectAmount = collectAmount;
        this.material = material;
        this.players = new HashMap<>();
    }


    @Override
    public void tick() {

        if (Main.getInstance().getGame().getState() != Game.GameState.PLAYING) return;

        for (GamePlayer player : this.players.keySet()) {
            if ((!player.isOnline()) || hasCompleted(player)) return;

            int size = getItemAmount(player, material);
            if (this.players.get(player) != size) this.players.replace(player, size);

            if (size >= collectAmount) {
                complete(player);
            }
        }
    }

    @Override
    public void complete(GamePlayer player) {
        super.complete(player);
    }

    @Override
    public void addPlayer(GamePlayer player) {
        this.players.put(player, 0);
    }

    @Override
    public String applyFormatToScoreStatus(GamePlayer player) {
        return getScoreboardBasicStatus().replace("%current%", String.valueOf(players.get(player))).replace("%needed%", String.valueOf(collectAmount));
    }
}
