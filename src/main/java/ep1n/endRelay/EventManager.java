package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import javax.swing.text.html.HTML;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.jar.Attributes;

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
    public void onRightClickCompass(PlayerInteractEvent ev) { //this part works
        if (ev.getPlayer().getInventory().getItemInMainHand().hasData(DataComponentTypes.LODESTONE_TRACKER)) {
            Vector v = ev.getPlayer().getVelocity();
            ev.getPlayer().teleport(ev.getPlayer().getInventory().getItemInMainHand().getData(DataComponentTypes.LODESTONE_TRACKER).location().add(.5,1,.5).setDirection(ev.getPlayer().getLocation().getDirection()));
            ev.getPlayer().setVelocity(v);
        }
        if (EndRelay.instance.blockMap.containsKey(ev.getClickedBlock().getLocation())) {
            for (DataComponentType type : ev.getPlayer().getInventory().getItemInMainHand().getDataTypes()) {
                Bukkit.getServer().sendMessage(Component.text(ev.getPlayer().getInventory().getItemInMainHand().getData((DataComponentType.Valued) type).toString()));
            }
        }
    }

    @EventHandler
    public void onCraftAnchor(PrepareItemCraftEvent ev) {
        if (ev.getRecipe() != null && ev.getRecipe().getResult().equals(EndRelay.instance.endAnchor)) {
            for (ItemStack i : ev.getInventory().getMatrix()) {
                Bukkit.getServer().sendMessage(Component.text(i.toString()));
                if (i.hasData(DataComponentTypes.LODESTONE_TRACKER)) {
                    Bukkit.getServer().sendMessage(Component.text("FOUND SOMETHING!!!!!"));
                    Location compass = i.getData(DataComponentTypes.LODESTONE_TRACKER).location();
                    Bukkit.getServer().sendMessage(Component.text("Got location " + compass));
                    ItemMeta meta = ev.getRecipe().getResult().getItemMeta();
                    ArrayList<Component> list = new ArrayList<>();
                    list.add(Component.text("Bonded to lodestone at " + compass.blockX() + " " + compass.blockY() + " " + compass.blockZ()));
                    meta.lore(list);
                    ev.getRecipe().getResult().setItemMeta(meta); //DOESNT WORK AT ALL FIX
                }
            }
        }
    }
}
