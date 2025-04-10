package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.util.Vector;

import java.net.http.WebSocket;

public class EventManager implements Listener {

    @EventHandler
    public void onPlaceCustom(BlockPlaceEvent ev) {
        if (ev.getItemInHand().equals(EndRelay.instance.endAnchor)) {
            EndRelay.instance.blockMap.put(ev.getBlockPlaced().getLocation(), EndRelay.instance.endAnchor);
        }
    }

    @EventHandler
    public void onBreakCustom(BlockBreakEvent ev) {
        if (EndRelay.instance.blockMap.get(ev.getBlock().getLocation()) != null) {
            ev.setCancelled(true);
            EndRelay.instance.blockMap.remove(ev.getBlock().getLocation());
            ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation().add(.5,.5,.5), EndRelay.instance.endAnchor);
            ev.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void onRightClickCompass(PlayerInteractEvent ev) {
        if (ev.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS) && ev.getAction().isRightClick()) {
            Vector v = ev.getPlayer().getVelocity();
            ev.getPlayer().teleport(ev.getPlayer().getInventory().getItemInMainHand().getData(DataComponentTypes.LODESTONE_TRACKER).location().add(.5,1,.5).setDirection(ev.getPlayer().getLocation().getDirection()));
            ev.getPlayer().setVelocity(v);
        }
    }
}
