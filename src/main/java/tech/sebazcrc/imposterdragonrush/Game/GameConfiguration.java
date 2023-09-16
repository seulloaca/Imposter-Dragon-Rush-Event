package tech.sebazcrc.imposterdragonrush.Game;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class GameConfiguration {

    private Game handle;

    private int imposters = 1;
    private World world;

    public GameConfiguration(Game handle) {
        this.handle = handle;
        this.world = Bukkit.getWorld("world");
    }

    public Game getHandle() {
        return handle;
    }

    public int getMaxImposters() {
        return imposters;
    }

    public boolean setMaxImposters(int imposters) {
        if (imposters < 1 || imposters > 8) {
            return false;
        } else {
            this.imposters = imposters;
            return true;
        }
    }

    public World getWorld() {
        return world;
    }
}
