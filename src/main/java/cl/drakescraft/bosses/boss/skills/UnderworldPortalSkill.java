package cl.drakescraft.bosses.boss.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import cl.drakescraft.bosses.boss.OdysseyBoss;

import java.util.Random;

public class UnderworldPortalSkill implements BossSkill {

    private final Random random = new Random();

    @Override
    public void execute(OdysseyBoss boss, Player target) {
        if (boss.getEntity() == null || boss.getEntity().isDead()) {
            return;
        }

        Location loc = boss.getEntity().getLocation();

        // Control de spam: maximo 4 espectros activos alrededor de Hades
        long existingVexes = loc.getWorld().getNearbyEntities(loc, 22, 22, 22, e -> e instanceof Vex).size();
        if (existingVexes >= 4) {
            return;
        }

        loc.getWorld().playSound(loc, Sound.BLOCK_PORTAL_TRAVEL, 1.2f, 0.8f);
        loc.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(0, 1.0, 0), 60, 1.0, 1.0, 1.0, 0.1);

        int toSpawn = Math.min(2, (int) (4 - existingVexes));
        for (int i = 0; i < toSpawn; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 4;
            double offsetZ = (random.nextDouble() - 0.5) * 4;
            Location spawnLoc = loc.clone().add(offsetX, 1.2, offsetZ);

            Vex specter = (Vex) loc.getWorld().spawnEntity(spawnLoc, EntityType.VEX);
            specter.setCustomName("§7§lEspectro del Inframundo");
            specter.setCustomNameVisible(true);
            specter.setRemoveWhenFarAway(true);
            specter.setTarget(target);
            specter.setLimitedLifetime(true);
            specter.setLifeTicks(20 * 20); // 20s de vida maximo

            var speedAttr = specter.getAttribute(org.bukkit.attribute.Attribute.MOVEMENT_SPEED);
            if (speedAttr != null) {
                speedAttr.setBaseValue(0.32);
            }
        }

        target.sendMessage("§5§lHades opens a portal to the Underworld! Several spectres emerge...");
    }
}
