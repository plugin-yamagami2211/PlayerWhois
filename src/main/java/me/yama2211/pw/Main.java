package me.yama2211.pw;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class Main extends JavaPlugin {
    private String prefix;
    private String ver;
    @Override
    public void onEnable() {
        // Plugin startup logic
        prefix = "[PlayerWhois]";
        ver = Bukkit.getServer().getClass().getPackage().getName();
        ver = ver.substring(ver.lastIndexOf(".") + 1);

        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if(args.length == 0){
            sender.sendMessage(prefix + "/plwhois");
            return true;
        }
        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if(targetPlayer == null){
            sender.sendMessage(prefix + targetName + "はオンラインではありません。");
            return true;
        }
        Location loc = targetPlayer.getLocation();

        String loc_x = Integer.toString(loc.getBlockX());
        String loc_y = Integer.toString(loc.getBlockY());
        String loc_z = Integer.toString(loc.getBlockZ());
        String ping = Integer.toString(getPing(targetPlayer));
        String msg = getConfig().getString("msg");
        msg = msg.replace("%prefix",prefix);
        msg = msg.replace("%player",targetPlayer.getName());
        msg = msg.replace("%world",targetPlayer.getWorld().getName());
        msg = msg.replace("%loc_X",loc_x);
        msg = msg.replace("%loc_Y",loc_y);
        msg = msg.replace("%loc_Z",loc_z);
        msg = msg.replace("%uuid",targetPlayer.getUniqueId().toString());
        msg = msg.replace("%address",replaceLast(targetPlayer.getAddress().toString().replaceFirst("/", "").replace(String.valueOf(targetPlayer.getAddress().getPort()), ""), ":", ""));
        msg = msg.replace("%ping",ping);
        msg = msg.replace("%gamemode",targetPlayer.getGameMode().toString());
        msg = msg.replace("%flymode",(targetPlayer.getAllowFlight()? "ON": "OFF"));
        msg = msg.replaceAll("%n","\n");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',msg));
        return true;
    }

    private String replaceLast(String text, String regex, String replacement){
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    private int getPing(Player player) {
        int ping = -1;
        try {
            Class<?> cp = Class.forName("org.bukkit.craftbukkit."+ ver +".entity.CraftPlayer");
            Object cpc = cp.cast(player);
            Method m = cpc.getClass().getMethod("getHandle");
            Object o = m.invoke(cpc);
            Field f = o.getClass().getField("ping");
            ping = f.getInt(o);
        } catch (Exception e) {
            Bukkit.getLogger().warning(e.getLocalizedMessage());
        }
        return ping;
    }
}
