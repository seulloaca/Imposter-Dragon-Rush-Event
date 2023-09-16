package tech.sebazcrc.imposterdragonrush.Utils.Task;

import org.bukkit.Material;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;

import java.util.HashMap;

public class LocateStrongholdTask extends AbstractTask {

    private HashMap<GamePlayer, Integer> players;

    private int collectAmount;
    private Material material;

    public LocateStrongholdTask(int collectAmount, Material material, String mat) {
        super(TaskType.COLLECT_STUFF, "Recolecta " + mat + " (%current%/%needed%)");
        this.collectAmount = collectAmount;
        this.material = material;
        this.players = new HashMap<>();
    }


    @Override
    public void tick() {
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
    public String applyFormatToScoreStatus(GamePlayer player) {
        return getScoreboardBasicStatus().replace("%current%", String.valueOf(players.get(player)).replace("%needed%", String.valueOf(collectAmount)));
    }
}
