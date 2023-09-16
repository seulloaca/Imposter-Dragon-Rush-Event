package tech.sebazcrc.imposterdragonrush.Utils.Task;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import tech.sebazcrc.imposterdragonrush.Game.Game;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Main;

import java.util.HashMap;

public class CraftItemTask extends AbstractTask implements Listener {
    private Material material;

    public CraftItemTask(String what, Material mat) {
        super(TaskType.CRAFT_ITEM, "Craftea un(a) " + what + "");
        this.material = mat;

        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player p = (Player) e.getWhoClicked();

        if (e.getInventory().getResult() != null && Main.getInstance().getGame().getState() == Game.GameState.PLAYING && getHandlingPlayers().contains(Main.getInstance().getGamePlayer(p.getName())) && e.getRecipe().getResult().getType() == material && !hasCompleted(Main.getInstance().getGamePlayer(p.getName()))) {
            complete(Main.getInstance().getGamePlayer(p.getName()));
        }
    }
}
