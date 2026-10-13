package cl.drakescraft.bosses.gui;

import cl.drakescraft.bosses.DrakesBosses;
import cl.drakescraft.bosses.boss.arena.BossArenaService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public final class BossMenu implements Listener, InventoryHolder {

    private static final String GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&8&l⚡ &6&lBOSS ARENAS &8&l⚡");
    private final DrakesBosses plugin;
    private final BossArenaService arenaService;
    private final Inventory inventory;

    private static record BossEntry(
            String id,
            String displayName,
            Material icon,
            int slot,
            String difficulty,
            String health,
            String rewards,
            String description
    ) {}

    private static final List<BossEntry> BOSS_ENTRIES = List.of(
            // Tier 1: Starter / Heroes
            new BossEntry("circe", "&d&lCirce the Sorceress", Material.BREWING_STAND, 1,
                    "&a★☆☆☆☆ &7(Easy)", "1,200", "Arcane Potions, Gold, Slimefun", "Mistress of potions and arcane curses."),
            new BossEntry("polifemo", "&2&lPolyphemus the Cyclops", Material.ZOMBIE_HEAD, 3,
                    "&a★☆☆☆☆ &7(Easy)", "1,500", "Mythic Meat, Ores, Dragmas", "One-eyed giant with crushing brute strength."),
            new BossEntry("jax", "&7&lAjax / Jax", Material.IRON_BLOCK, 5,
                    "&a★☆☆☆☆ &7(Easy)", "1,800", "Refined Iron, Cores, XP", "Impenetrable war automaton of antiquity."),
            new BossEntry("dios_corrupto", "&5&lCorrupted God", Material.WITHER_ROSE, 7,
                    "&e★★☆☆☆ &7(Normal)", "2,500", "Dark Essences, Cosmic Dust", "Ancient deity consumed by abyssal corruption."),

            // Tier 2: Olympian Gods
            new BossEntry("ares", "&c&lAres - God of War", Material.NETHERITE_SWORD, 10,
                    "&e★★☆☆☆ &7(Normal)", "3,500", "Legendary Swords, Ingots, Dragmas", "Relentless fury and ferocious melee combat."),
            new BossEntry("poseidon", "&b&lPoseidon - King of the Seas", Material.TRIDENT, 12,
                    "&e★★☆☆☆ &7(Normal)", "4,000", "Mythic Tridents, Heart of the Sea, XP", "Absolute command over tides and sea storms."),
            new BossEntry("loki", "&a&lLoki - God of Deceit", Material.ENDER_EYE, 14,
                    "&6★★★☆☆ &7(Challenging)", "4,200", "Illusions, Emeralds, Runes", "Deceptive clones, teleports and deadly tricks."),
            new BossEntry("zeus", "&e&lZeus - King of Olympus", Material.LIGHTNING_ROD, 16,
                    "&6★★★☆☆ &7(Challenging)", "5,000", "Divine Bolts, Arcane Focus, Dragmas", "Lord of the skies and devastating lightning."),

            // Tier 3: Norse Mythology and Beasts
            new BossEntry("hades", "&8&lHades - Lord of the Underworld", Material.SOUL_LANTERN, 19,
                    "&6★★★☆☆ &7(Challenging)", "4,800", "Titanic Bones, Souls, Slimefun", "Commander of the wandering souls of the underworld."),
            new BossEntry("thor", "&9&lThor - God of Thunder", Material.NETHERITE_AXE, 20,
                    "&6★★★☆☆ &7(Challenging)", "5,500", "Mjölnir Shards, Pure Iron, Bolts", "Thunderous power that shakes the earth."),
            new BossEntry("heimdall", "&f&lHeimdall - Guardian of Bifröst", Material.SHIELD, 21,
                    "&6★★★☆☆ &7(Challenging)", "5,200", "Divine Shields, Watcher Eyes", "The sentinel who sees everything and lets nothing pass."),
            new BossEntry("hidra", "&2&lLernaean Hydra", Material.POISONOUS_POTATO, 22,
                    "&c★★★★☆ &7(Hard)", "6,000", "Pure Venom, Legendary Scales", "Many-headed beast whose venom melts armor."),
            new BossEntry("cerbero", "&4&lCerberus - Abyssal Guardian", Material.FIRE_CHARGE, 23,
                    "&c★★★★☆ &7(Hard)", "6,200", "Hellfire, Magma Hide", "The three-headed hound that spits abyssal fire."),
            new BossEntry("artemisa", "&a&lArtemis - Goddess of the Hunt", Material.BOW, 24,
                    "&c★★★★☆ &7(Hard)", "5,800", "Lunar Arrows, Sacred Bows", "Pinpoint aim and arrows of celestial light."),
            new BossEntry("prometeo", "&6&lPrometheus - Bringer of Fire", Material.BLAZE_ROD, 25,
                    "&c★★★★☆ &7(Hard)", "6,500", "Primordial Flame, Cosmic Ash", "The rebel titan who defied the gods with fire."),

            // Tier 4: Egyptian Gods and Extreme Mythics
            new BossEntry("ra", "&e&lRa - God of the Sun", Material.SUNFLOWER, 28,
                    "&c★★★★☆ &7(Mythic)", "7,500", "Solar Gold, Divine Flash, Dragmas", "The sun disk that scorches any impure armor."),
            new BossEntry("isis", "&d&lIsis - Queen of Magic", Material.FEATHER, 29,
                    "&c★★★★☆ &7(Mythic)", "7,000", "Veil of Life, Sacred Magic", "Supreme sorceress of resurrection and healing arts."),
            new BossEntry("anubis", "&8&lAnubis - Judge of Souls", Material.BONE, 30,
                    "&c★★★★☆ &7(Mythic)", "7,500", "Scale of Osiris, Necromancy", "Weighs your heart against the feather of truth."),
            new BossEntry("set", "&6&lSet - Lord of Chaos", Material.REDSTONE_BLOCK, 31,
                    "&c★★★★☆ &7(Mythic)", "7,800", "Bloody Sand, Chaos Fragment", "God of sandstorms and betrayal."),
            new BossEntry("odin", "&9&lOdin - The All-Father", Material.SPECTRAL_ARROW, 32,
                    "&4★★★★★ &7(Extreme)", "8,500", "Spear Gungnir, Runes of Wisdom", "King of Asgard, master of knowledge and battle."),
            new BossEntry("kratos", "&c&lKratos - The Ghost of Sparta", Material.NETHERITE_INGOT, 33,
                    "&4★★★★★ &7(Extreme)", "9,000", "Blades of Chaos, Spartan Rage", "The godslayer consumed by vengeance."),
            new BossEntry("coloso_end", "&5&lEnd Colossus", Material.END_STONE, 34,
                    "&4★★★★★ &7(Extreme)", "8,000", "Void Stones, Resonant Pearls", "Stone giant imbued with the energy of the End."),

            // Tier 5: Cataclysms and Final Bosses
            new BossEntry("tifon", "&4&lTyphon - Father of Monsters", Material.MAGMA_BLOCK, 38,
                    "&4★★★★★ &7(Cataclysm)", "12,000", "Cosmic Lava, Typhon Scales", "The deadliest tempest ever raised against Olympus."),
            new BossEntry("dragon", "&5&lAncient Dragon", Material.DRAGON_HEAD, 40,
                    "&5★★★★★ &7(Legendary)", "15,000", "Pure Breath, Dragon Eggs, SF", "The first dragon, who shaped the empty dimensions."),
            new BossEntry("wither_storm", "&0&lWither Storm", Material.NETHER_STAR, 42,
                    "&5★★★★★ &7(Apocalypse)", "25,000", "Void Stars, Tractor Beam", "The world-devouring mass with a tractor beam."),
            new BossEntry("garou", "&1&lCosmic Garou", Material.BEACON, 44,
                    "&5★★★★★ &7(Supreme God)", "35,000", "Awakened Mode, Cosmic Essence", "The absolute terror that copies any martial art.")
    );

    public BossMenu(DrakesBosses plugin, BossArenaService arenaService) {
        this.plugin = plugin;
        this.arenaService = arenaService;
        this.inventory = Bukkit.createInventory(this, 54, GUI_TITLE);
        populateInventory();
    }

    public void open(Player player) {
        player.openInventory(this.inventory);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    private void populateInventory() {
        // Glass frame
        ItemStack borderGlass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        ItemStack darkGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, borderGlass);
        }

        for (BossEntry entry : BOSS_ENTRIES) {
            double fee = arenaService.entryFee(entry.id());
            String formattedFee = String.format(Locale.ROOT, "%,.0f", fee);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&', "&8» " + entry.description()));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&7Difficulty: " + entry.difficulty()));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&c❤ Estimated Health: &f" + entry.health() + " HP"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&e✦ Rewards: &f" + entry.rewards()));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&6💰 Entry Fee: &e" + formattedFee + " Dragmas"));
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&a▶ Left Click: &fChallenge Solo"));
            lore.add(ChatColor.translateAlternateColorCodes('&', "&e▶ Right Click: &fGroup Info"));

            ItemStack item = createItem(entry.icon(), ChatColor.translateAlternateColorCodes('&', entry.displayName()), lore);
            inventory.setItem(entry.slot(), item);
        }

        // Bottom bar
        ItemStack infoItem = createItem(Material.BOOK, ChatColor.translateAlternateColorCodes('&', "&6&lℹ ARENA INFORMATION"), List.of(
                ChatColor.translateAlternateColorCodes('&', "&7Boss arenas are isolated, protected"),
                ChatColor.translateAlternateColorCodes('&', "&7dimensions inside &fboss_arena&7."),
                "",
                ChatColor.translateAlternateColorCodes('&', "&f• No interference and no grief losses."),
                ChatColor.translateAlternateColorCodes('&', "&f• When the boss falls, rewards drop on the ground."),
                ChatColor.translateAlternateColorCodes('&', "&f• If you die, you respawn safely."),
                ChatColor.translateAlternateColorCodes('&', "&f• Group run: &e/bosswarp <boss> grupo <friend1> <friend2>")
        ));
        inventory.setItem(48, infoItem);

        ItemStack closeItem = createItem(Material.BARRIER, ChatColor.translateAlternateColorCodes('&', "&c&lClose Menu"), List.of(
                ChatColor.translateAlternateColorCodes('&', "&7Click to exit.")
        ));
        inventory.setItem(49, closeItem);

        ItemStack spectateItem = createItem(Material.SPYGLASS, ChatColor.translateAlternateColorCodes('&', "&b&lSpectator Mode"), List.of(
                ChatColor.translateAlternateColorCodes('&', "&7Want to watch a friend's battle?"),
                "",
                ChatColor.translateAlternateColorCodes('&', "&eUse: &f/bosswarp spectate <player>")
        ));
        inventory.setItem(50, spectateItem);
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) {
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createItem(Material mat, String name) {
        return createItem(mat, name, null);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof BossMenu)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        for (BossEntry entry : BOSS_ENTRIES) {
            if (entry.slot() == slot) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.0f);

                if (event.isRightClick()) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6[BossArena] &eTo challenge " + entry.displayName() + " &ewith a group, use:"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&f/bosswarp " + entry.id() + " grupo <player1> <player2>..."));
                    return;
                }

                // Left click: solo
                player.closeInventory();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6[BossArena] &eStarting a solo arena against " + entry.displayName() + "&e..."));

                var result = arenaService.start(entry.id(), Set.of(player), false);
                if (!result.started()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&c[BossArena] " + result.error()));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                } else {
                    if (result.feePerPlayer() > 0.0D) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&6[BossArena] &eEntry fee charged: &6" + String.format(Locale.ROOT, "%,.0f", result.feePerPlayer())
                                        + " Dragmas&e."));
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                }
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof BossMenu) {
            event.setCancelled(true);
        }
    }
}
