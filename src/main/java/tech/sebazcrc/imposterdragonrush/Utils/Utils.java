package tech.sebazcrc.imposterdragonrush.Utils;

import org.bukkit.ChatColor;

public final class Utils {

    public static String format(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String formatInterval(int totalTime) {
        int hrs = totalTime / 3600;
        int minAndSec = totalTime % 3600;
        int min = minAndSec / 60;
        int sec = minAndSec % 60;

        String s = String.format((hrs > 0 ? "%02d:" : ""), hrs);

        return String.format(s + "%02d:%02d", min, sec);
    }
}
