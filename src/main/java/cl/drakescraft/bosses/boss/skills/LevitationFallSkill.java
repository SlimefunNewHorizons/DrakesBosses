package cl.drakescraft.bosses.boss.skills;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import cl.drakescraft.bosses.boss.OdysseyBoss;

public class LevitationFallSkill implements BossSkill {

    @Override
    public void execute(OdysseyBoss boss, Player target) {
        if (boss.getEntity() == null || boss.getEntity().isDead() || target == null || !target.isOnline()) {
            return;
        }

        Location targetLoc = target.getLocation();
        targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_SHULKER_SHOOT, 1.5f, 0.7f);
        targetLoc.getWorld().spawnParticle(Particle.CLOUD, targetLoc, 20, 0.5, 0.5, 0.5, 0.1);

        // Apply high levitation for 2 seconds (40 ticks)
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 35, 4));
        target.sendMessage("§9§lPoseidon manipulates ocean gravity and lifts you into the air!");

        // Play falling sound after levitation ends
        org.bukkit.Bukkit.getScheduler().runTaskLater(cl.drakescraft.bosses.DrakesBosses.getInstance(), () -> {
            if (target.isOnline() && !target.isDead()) {
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_PLAYER_SMALL_FALL, 1.5f, 0.8f);
            }
        }, 40L);
    }
}
