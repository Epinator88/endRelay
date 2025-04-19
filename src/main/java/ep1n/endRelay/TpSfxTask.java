package ep1n.endRelay;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TpSfxTask extends BukkitRunnable {

    private Player p;

    public TpSfxTask(Player p) {
        this.p = p;
    }

    @Override
    public void run() {
        p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1F, 1F);
    }
}
