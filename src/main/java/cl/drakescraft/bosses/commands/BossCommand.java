package cl.drakescraft.bosses.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import cl.drakescraft.bosses.DrakesBosses;
import cl.drakescraft.bosses.boss.BossManager;

import java.util.ArrayList;
import java.util.List;

public final class BossCommand implements CommandExecutor, TabCompleter {

    private final DrakesBosses plugin;
    private final BossManager bossManager;

    public BossCommand(DrakesBosses plugin, BossManager bossManager) {
        this.plugin = plugin;
        this.bossManager = bossManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("drakesbosses.admin")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to use this command."));
            return true;
        }

        if (command.getName().equalsIgnoreCase("spawnallbosses")) {
            return spawnAllBosses(sender);
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&c&lUSAGE: &e/boss <spawn|spawnall|give> ... &7| &fSpecial: &ddragon&f, &5wither"));
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("spawnall")) {
            return spawnAllBosses(sender);
        }

        // ── SUBCOMANDO: SPAWN ──
        if (sub.equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cOnly players can use this command."));
                return true;
            }

            if (args.length < 2) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lUSAGE: &e/boss spawn <type> &7| &fExample: &e/boss spawn dragon"));
                return true;
            }

            String bossType = args[1].toLowerCase();
            if (!isValidBossType(bossType)) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lERROR: &eThe mythical boss '" + bossType + "' does not exist."));
                return true;
            }

            var result = plugin.getBossArenas().startForced(bossType, List.of(player));
            if (result.started()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&a&lSUCCESS! &eThe mythical boss &f" + bossType + " &ehas been moved to a safe arena."));
            } else {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCould not create the arena: " + result.error()));
            }
            return true;
        }

        // ── SUBCOMANDO: GIVE (ENTREGAR INVOCADOR) ──
        if (sub.equalsIgnoreCase("give")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lUSAGE: &e/boss give <player> <type>"));
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lERROR: &ePlayer '" + args[1] + "' is not online."));
                return true;
            }

            String bossType = args[2].toLowerCase();
            if (!isValidBossType(bossType)) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&c&lERROR: &eThe mythical boss '" + bossType + "' does not exist."));
                return true;
            }

            org.bukkit.inventory.ItemStack summoner = cl.drakescraft.bosses.items.OdysseyItemManager.createBossSummoner(bossType);
            java.util.Map<Integer, org.bukkit.inventory.ItemStack> leftovers = target.getInventory().addItem(summoner);
            for (org.bukkit.inventory.ItemStack drop : leftovers.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), drop);
            }

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&a&lSUCCESS! &eDelivered a " + bossType.toUpperCase() + " Summoner to " + target.getName()));
            target.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&d&l⚡ &eYou received a &d" + bossType.toUpperCase() + " Summoner&e! Right-click to awaken it."));
            return true;
        }

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c&lUSAGE: &e/boss <spawn|spawnall|give> ..."));
        return true;
    }

    /**
     * Retires the legacy world-spawn test path. Boss tests must use isolated
     * arenas so a staff experiment cannot leak entities into survival worlds.
     */
    private boolean spawnAllBosses(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&e[BossArena] Mass tests outside an arena were removed for safety."));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&7Use &f/bosswarp staff <boss> <player> &7to create an isolated test."));
        return true;
    }

    private boolean isValidBossType(String type) {
        return type.equals("circe") || type.equals("polifemo")
                || type.equals("dios_corrupto") || type.equals("dios-corrupto")
                || type.equals("thor") || type.equals("ares")
                || type.equals("hades") || type.equals("poseidon")
                || type.equals("zeus") || type.equals("loki")
                || type.equals("odin") || type.equals("kratos")
                || type.equals("heimdall") || type.equals("hidra")
                || type.equals("cerbero") || type.equals("artemisa")
                || type.equals("tifon") || type.equals("tifón")
                || type.equals("prometeo") || type.equals("coloso_end")
                || type.equals("coloso-end") || type.equals("coloso")
                || type.equals("wither") || type.equals("wither_storm") || type.equals("wither-storm")
                || type.equals("witherstorm") || type.equals("dragon_ancestral")
                || type.equals("dragon-ancestral") || type.equals("dragon")
                || type.equals("ra") || type.equals("isis") || type.equals("anubis") || type.equals("set")
                || type.equals("garou") || type.equals("garou_cosmico") || type.equals("garou-cosmico")
                || type.equals("jax") || type.equals("ajax");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (!sender.hasPermission("drakesbosses.admin")) {
            return completions;
        }

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            if ("spawn".startsWith(input)) completions.add("spawn");
            if ("spawnall".startsWith(input)) completions.add("spawnall");
            if ("give".startsWith(input)) completions.add("give");
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("spawn")) {
                String input = args[1].toLowerCase();
                List<String> all = getBossList();
                for (String b : all) {
                    if (b.startsWith(input)) completions.add(b);
                }
            } else if (args[0].equalsIgnoreCase("give")) {
                // Autocompletar jugadores conectados
                String input = args[1].toLowerCase();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(input)) {
                        completions.add(p.getName());
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            String input = args[2].toLowerCase();
            List<String> all = getBossList();
            for (String b : all) {
                if (b.startsWith(input)) completions.add(b);
            }
        }

        return completions;
    }

    private List<String> getBossList() {
        return List.of(
                "circe", "polifemo", "dios_corrupto",
                "thor", "ares", "hades", "poseidon", "zeus",
                "loki", "odin", "kratos",
                "heimdall", "hidra", "cerbero", "artemisa", "tifon", "prometeo",
                "coloso_end", "wither", "dragon", "ra", "isis", "anubis", "set", "garou", "jax"
        );
    }
}
