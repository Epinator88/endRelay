package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.checkerframework.checker.units.qual.A;

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
        if (ev.getPlayer().getInventory().getItemInMainHand().hasData(DataComponentTypes.LODESTONE_TRACKER)) {
            Bukkit.getServer().sendMessage(Component.text("REALEST!!!!"));
            Bukkit.getServer().sendMessage(Component.text(ev.getPlayer().getInventory().getItemInMainHand().getData(DataComponentTypes.LODESTONE_TRACKER).location().toString()));
        }
    }

    @EventHandler
    public void onCraftAnchor(CraftItemEvent ev) {
        if (ev.getRecipe().getResult().equals(EndRelay.instance.endAnchor)) {
            for (ItemStack i : ev.getInventory().getMatrix()) {
                Bukkit.getServer().sendMessage(Component.text(i.toString()));
                if (i.hasData(DataComponentTypes.LODESTONE_TRACKER)) {
                    Bukkit.getServer().sendMessage(Component.text("FOUND SOMETHING!!!!!"));
                    Location compass = i.getData(DataComponentTypes.LODESTONE_TRACKER).location();
                    Bukkit.getServer().sendMessage(Component.text("Got location " + compass));
                    ItemStack item = new ItemStack(EndRelay.instance.endAnchor.getType());
                    ItemMeta meta = item.getItemMeta();
                    meta.customName(Component.text("End Relay").style(Style.style(TextDecoration.ITALIC)));
                    item.setItemMeta(meta);
                    item.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker(compass, true));
                    ev.getInventory().setResult(item);
                } //works???? idrk make sure, also since this works try .setData now
            }
        }
    }
}
