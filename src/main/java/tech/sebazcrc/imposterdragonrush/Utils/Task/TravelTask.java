package tech.sebazcrc.imposterdragonrush.Utils.Task;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Main;

public class TravelTask extends AbstractTask implements Listener {
    private World.Environment type;

    public TravelTask(World.Environment t) {
        super(TaskType.TRAVEL, "Viaja al " + (t == World.Environment.THE_END ? "End" : "Nether"));
        this.type = t;
        Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        GamePlayer gp = Main.getInstance().getGamePlayer(p.getName());

        if (this.getHandlingPlayers().contains(gp) && p.getWorld().getEnvironment() == type) {
            complete(gp);
        }
    }
}
