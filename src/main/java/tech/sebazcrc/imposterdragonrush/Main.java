package tech.sebazcrc.imposterdragonrush;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import tech.sebazcrc.imposterdragonrush.Game.Game;
import tech.sebazcrc.imposterdragonrush.Game.GamePlayer;
import tech.sebazcrc.imposterdragonrush.Utils.Utils;

public final class Main extends JavaPlugin implements CommandExecutor {

    private static Main instance;
    public static String prefix = "";

    private Game game;

    @Override
    public void onEnable() {
        instance = this;
        prefix = Utils.format("&8[&3&lDragonRush&8] &7&l➤ &r&f");

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        this.game = new Game("juego");

        getGame().scheduleTask(this);
        
        getCommand("idr").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("idr")) {

            if (!sender.hasPermission("admin")) {
                sender.sendMessage(Utils.format("&cDebes ser administrador."));
                return false;
            }

            if (args[0].equalsIgnoreCase("start")) {
                int imposters = Integer.parseInt(args[1]);
                Main.getInstance().getGame().getConfiguration().setMaxImposters(imposters);
                Main.getInstance().getGame().start();
                return true;
            } else if (args[0].equalsIgnoreCase("debugtime")) {
                Main.getInstance().getGame().setTime(14*60+57);
                return true;
            } else if (args[0].equalsIgnoreCase("impostores")) {
                getGame().broadcastImposters();
            } else if (args[0].equalsIgnoreCase("taskStatus")) {
                sender.sendMessage("Completadas: " + game.hasFinishedTasks());
                sender.sendMessage("Completed size: " + game.getCompletedTaskPlayers().size());
                sender.sendMessage("Alive size: " + game.getAlivePlayers().size());
            }
        }
        return false;
    }

    public static Main getInstance() {
        return instance;
    }

    public Game getGame() {
        return game;
    }

    public GamePlayer getGamePlayer(String name) {
        GamePlayer gp = game.getPlayer(name);
        if (gp != null) {
            return gp;
        }

        return null;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
