package cl.drakescraft.bosses.commands;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import cl.drakescraft.bosses.DrakesBosses;
import cl.drakescraft.bosses.boss.arena.BossArenaService;

/** Public entry point for isolated boss sessions. */
public final class BossWarpCommand implements CommandExecutor, TabCompleter {
    private static final List<String> ARENA_BOSS_TYPES = List.of(
            "circe", "polifemo", "dios_corrupto", "thor", "ares", "hades", "poseidon", "zeus",
            "loki", "odin", "kratos", "heimdall", "hidra", "cerbero", "artemisa", "tifon",
            "prometeo", "coloso_end", "wither_storm", "dragon_ancestral", "ra", "isis", "anubis",
            "set", "jax");
    private final DrakesBosses plugin;
    private final BossArenaService arenas;
    public BossWarpCommand(DrakesBosses plugin, BossArenaService arenas) {
        this.plugin = plugin;
        this.arenas = arenas;
    }
    @Override public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (label.equalsIgnoreCase("bosses") || label.equalsIgnoreCase("jefes") || args.length < 1) {
            new cl.drakescraft.bosses.gui.BossMenu(plugin, arenas).open(player);
            return true;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("menu")) {
            new cl.drakescraft.bosses.gui.BossMenu(plugin, arenas).open(player);
            return true;
        }
        if (args.length >= 1 && args[0].equalsIgnoreCase("staff")) {
            return startForcedArena(player, args);
        }
        if (args.length == 1 && (args[0].equalsIgnoreCase("precios") || args[0].equalsIgnoreCase("precio"))) {
            sendPrices(player);
            return true;
        }
        if (args.length >= 1 && (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("salir"))) {
            if (!arenas.leave(player)) {
                player.sendMessage("§cYou are not in an active arena or spectator mode.");
            }
            return true;
        }
        if (args.length >= 2 && args[0].equalsIgnoreCase("spectate")) {
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null || !arenas.spectate(player, target)) player.sendMessage("§cThat player is not in an active arena.");
            return true;
        }
        boolean group = args.length > 1 && args[1].equalsIgnoreCase("grupo");
        Set<Player> roster = new LinkedHashSet<>();
        roster.add(player);
        if (group) {
            for (int index = 2; index < args.length; index++) {
                Player member = Bukkit.getPlayerExact(args[index]);
                if (member == null) { player.sendMessage("§cNot online: " + args[index]); return true; }
                roster.add(member);
            }
            if (roster.size() < 2) { player.sendMessage("§cA group requires at least two players."); return true; }
        }
        var result = arenas.start(args[0].toLowerCase(Locale.ROOT), roster, group);
        if (!result.started()) {
            player.sendMessage("§c[BossArena] " + result.error());
            return true;
        }
        if (result.feePerPlayer() > 0.0D) {
            player.sendMessage("§6[BossArena] §eEntry fee charged: §6" + formatFee(result.feePerPlayer())
                    + " Dragmas §eper player.");
        } else {
            player.sendMessage("§6[BossArena] §eArena started with no entry fee.");
        }
        return true;
    }

    /** Staff-only arena orchestration for testing encounters with named online players. */
    private boolean startForcedArena(Player staff, String[] args) {
        if (!hasStaffAccess(staff)) {
            staff.sendMessage("§cYou do not have permission to force arenas.");
            return true;
        }
        if (args.length < 3) {
            staff.sendMessage("§eStaff usage: /bosswarp staff <boss> <player> [player...]");
            return true;
        }
        Set<Player> roster = new LinkedHashSet<>();
        for (int index = 2; index < args.length; index++) {
            Player member = Bukkit.getPlayerExact(args[index]);
            if (member == null) {
                staff.sendMessage("§cNot online: " + args[index]);
                return true;
            }
            roster.add(member);
        }
        var result = arenas.startForced(args[1].toLowerCase(Locale.ROOT), roster);
        if (!result.started()) {
            staff.sendMessage("§c[BossArena] " + result.error());
            return true;
        }
        staff.sendMessage("§a[BossArena] Staff arena created with no entry fee for " + roster.size() + " player(s).");
        return true;
    }

    private void sendPrices(Player player) {
        player.sendMessage("§6§l[BossArena] §eEntry fee per player in Dragmas");
        player.sendMessage(line("Circe", "circe", "Polifemo", "polifemo", "Dios Corrupto", "dios_corrupto"));
        player.sendMessage(line("Thor", "thor", "Ares", "ares", "Poseidón", "poseidon"));
        player.sendMessage(line("Loki", "loki", "Zeus", "zeus", "Hades", "hades"));
        player.sendMessage(line("Jax/Ajax", "jax", "Heimdall", "heimdall", "Hidra", "hidra"));
        player.sendMessage(line("Cerbero", "cerbero", "Artemisa", "artemisa", "Prometeo", "prometeo"));
        player.sendMessage(line("Coloso End", "coloso_end", "Ra", "ra", "Isis", "isis"));
        player.sendMessage(line("Anubis", "anubis", "Set", "set", "Odín", "odin"));
        player.sendMessage(line("Kratos", "kratos", "Tifón", "tifon", "Dragón", "dragon"));
        player.sendMessage("§7Wither Storm §6" + formatFee(arenas.entryFee("wither_storm")));
        player.sendMessage("§8In groups, every member pays. A creation failure reverses the charge once.");
    }

    private String line(String firstName, String firstId, String secondName, String secondId, String thirdName, String thirdId) {
        return "§7" + firstName + " §6" + formatFee(arenas.entryFee(firstId))
                + " §8| §7" + secondName + " §6" + formatFee(arenas.entryFee(secondId))
                + " §8| §7" + thirdName + " §6" + formatFee(arenas.entryFee(thirdId));
    }

    private static String formatFee(double fee) {
        return String.format(Locale.ROOT, "%,.0f", fee);
    }

    /** Preserves historical LuckPerms grants while roles migrate to the new module namespace. */
    private static boolean hasStaffAccess(CommandSender sender) {
        return sender.hasPermission("drakesbosses.bosswarp.staff")
                || sender.hasPermission("odysseia.bosswarp.staff");
    }

    @Override public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(ARENA_BOSS_TYPES);
            options.addAll(List.of("precios", "spectate", "leave"));
            if (hasStaffAccess(s)) options.add("staff");
            return options;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("staff")) return ARENA_BOSS_TYPES;
        if (args.length >= 3 && args[0].equalsIgnoreCase("staff")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        if (args.length == 2 && !args[0].equalsIgnoreCase("spectate")) return List.of("solo", "grupo");
        if (args.length >= 3 && args[1].equalsIgnoreCase("grupo")) return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        return List.of();
    }
}
