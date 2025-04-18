package ep1n.endRelay;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.A;

import javax.swing.text.html.HTML;
import javax.xml.crypto.Data;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;

public class EventManager implements Listener {

    @EventHandler
    public void onPlaceCustom(BlockPlaceEvent ev) {
        if (ev.getItemInHand().hasData(DataComponentTypes.LODESTONE_TRACKER)) { //literally the only block with this kind of data so the only possible outcome lmfao
            EndRelay.instance.locToLode.put(ev.getBlockPlaced().getLocation(), ev.getItemInHand().getData(DataComponentTypes.LODESTONE_TRACKER).location());
            ev.getBlockPlaced().getState().setMetadata("lodestone", new FixedMetadataValue(EndRelay.instance, (ev.getItemInHand().getData(DataComponentTypes.LODESTONE_TRACKER).location())));
            ev.getBlockPlaced().getState().update();
            Bukkit.getLogger().info("Has data? " + ev.getBlockPlaced().getState().hasMetadata("lodestone"));
            Bukkit.getLogger().info(ev.getBlockPlaced().getMetadata("lodestone").getFirst().value().toString());
        }
    }

    @EventHandler
    public void onBreakCustom(BlockBreakEvent ev) {
        if (EndRelay.instance.locToLode.get(ev.getBlock().getLocation()) != null) {
            ev.setDropItems(false);
            if (ev.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                ItemStack item = new ItemStack(Material.DEAD_HORN_CORAL_BLOCK);
                item.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker((Location) ev.getBlock().getMetadata("lodestone").getFirst().value(), true));
                ItemMeta meta = item.getItemMeta();
                meta.customName(Component.text("End Relay"));
                meta.setMaxStackSize(1);
                item.setItemMeta(meta);
                ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation().add(.5, .5, .5), item);
            } else {
                ItemStack item = new ItemStack(Material.COMPASS);
                item.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker((Location) ev.getBlock().getMetadata("lodestone").getFirst().value(), true));
                ev.getBlock().getWorld().dropItemNaturally(ev.getBlock().getLocation().add(.5, .5, .5), item);
            }
            EndRelay.instance.locToLode.remove(ev.getBlock().getLocation());
            ev.getBlock().setType(Material.AIR);
        }
    }

    @EventHandler
    public void playerInteractRelayEvent (PlayerInteractEvent ev) {
        if (ev.getAction().isRightClick() && ev.getClickedBlock() != null && EndRelay.instance.locToLode.containsKey(ev.getClickedBlock().getLocation())) {
            if (ev.getClickedBlock().getType().equals(Material.DEAD_HORN_CORAL_BLOCK)) {
                if (ev.getItem() != null && ev.getItem().getType().equals(Material.END_CRYSTAL)) {
                    ev.getClickedBlock().getWorld().playSound(ev.getClickedBlock().getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1F, 1F);
                    ev.getClickedBlock().getWorld().spawnParticle(Particle.REVERSE_PORTAL, ev.getInteractionPoint(), 30);
                    ev.getPlayer().getInventory().getItem(ev.getHand()).setAmount(ev.getPlayer().getInventory().getItem(ev.getHand()).getAmount() - 1);
                    ev.setUseItemInHand(Event.Result.DEFAULT);
                    ev.getClickedBlock().setType(Material.DEAD_FIRE_CORAL_BLOCK);
                } else {
                    return;
                }
            } else if (ev.getClickedBlock().getType().equals(Material.DEAD_FIRE_CORAL_BLOCK) && !(ev.getPlayer().isSneaking())) {
                Location baseLoq = (Location) ev.getClickedBlock().getMetadata("lodestone").getFirst().value();
                Location loq = baseLoq.clone();
                if (ev.getPlayer().getWorld().getKey().equals(loq.getWorld().getKey()) && ev.getPlayer().getWorld().getKey().asString().equalsIgnoreCase("minecraft:the_end")) {
                    if (loq.getBlock().getType().equals(Material.LODESTONE)) {
                        ev.getPlayer().teleport(loq.add(.5, 1, .5).setDirection(ev.getPlayer().getLocation().getDirection()));
                        ev.getClickedBlock().setType(Material.DEAD_HORN_CORAL_BLOCK);
                        ev.setUseItemInHand(Event.Result.ALLOW);
                        ev.getClickedBlock().getWorld().playSound(ev.getClickedBlock().getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1F, 1F);
                    } else {
                        //no lodestone
                        ev.getClickedBlock().setType(Material.DEAD_HORN_CORAL_BLOCK);
                        ev.getClickedBlock().getWorld().playSound(ev.getClickedBlock().getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1F, 1F);
                        ev.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE, ev.getInteractionPoint(), 40);
                        ev.setUseItemInHand(Event.Result.ALLOW);
                    }
                } else {
                    //wrong dimension
                    ev.getClickedBlock().setType(Material.DEAD_HORN_CORAL_BLOCK);
                    ev.getClickedBlock().getWorld().playSound(ev.getClickedBlock().getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, 1F, 1F);
                    ev.getClickedBlock().getWorld().spawnParticle(Particle.SMOKE, ev.getInteractionPoint(), 40);
                    ev.setUseItemInHand(Event.Result.ALLOW);
                    ev.getInteractionPoint().createExplosion(7F);
                }
            }
        }
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent ev) {
        for (Block b : ev.getBlocks()) {
            if (EndRelay.instance.locToLode.containsKey(b.getLocation())) {
                ev.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onPiston(BlockPistonRetractEvent ev) {
        for (Block b : ev.getBlocks()) {
            if (EndRelay.instance.locToLode.containsKey(b.getLocation())) {
                ev.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCraftAnchor(CraftItemEvent ev) {
        if (ev.getRecipe().getResult().equals(EndRelay.instance.endAnchor)) {
            for (ItemStack i : ev.getInventory().getMatrix()) {
                if (i.hasData(DataComponentTypes.LODESTONE_TRACKER)) {
                    Location compass = i.getData(DataComponentTypes.LODESTONE_TRACKER).location();
                    if (!compass.getWorld().equals(ev.getView().getPlayer().getWorld())) ev.getView().getPlayer().getLocation().createExplosion(7F);
                    ItemStack item = new ItemStack(EndRelay.instance.endAnchor.getType());
                    ItemMeta meta = item.getItemMeta();
                    meta.customName(Component.text("End Relay").style(Style.style(TextDecoration.ITALIC)));
                    meta.setMaxStackSize(1);
                    item.setItemMeta(meta);
                    item.setData(DataComponentTypes.LODESTONE_TRACKER, LodestoneTracker.lodestoneTracker(compass, true));
                    ev.getInventory().setResult(item);
                    Bukkit.getLogger().info("Has location? " + ev.getInventory().getResult().hasData(DataComponentTypes.LODESTONE_TRACKER));
                } else if(i.getType().equals(Material.COMPASS)) {
                    //compass with no lodestone tracker
                    ev.getView().getPlayer().getLocation().createExplosion(7F);
                }
            }
        }
    }
}
