package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;
import org.checkerframework.checker.units.qual.N;

import javax.naming.Name;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class EndRelay extends JavaPlugin {

    public static EndRelay instance;

    public ItemStack endAnchor;

    public NamespacedKey endAnchorKey;

    public Map<Location, Location> locToLode;

    public NamespacedKey locArr;

    @Override
    public void onEnable() {
        
        instance = this;
        locToLode = new HashMap<>();

        File storedRelays = findStoredRelays();
        locArr = new NamespacedKey(this, "LODE_COORDINATES");
        endAnchorKey = new NamespacedKey(this, "end_anchor");
        endAnchor = new ItemStack(Material.DEAD_HORN_CORAL_BLOCK);
        ItemMeta endAnchorMeta = endAnchor.getItemMeta();
        endAnchorMeta.customName(Component.text("End Relay"));
        endAnchor.setItemMeta(endAnchorMeta);
        getServer().getPluginManager().registerEvents(new EventManager(), this);

        ShapedRecipe anchorRecipe = new ShapedRecipe(endAnchorKey, endAnchor);
        anchorRecipe.shape("POP",
                           "OCO",
                           "POP");
        anchorRecipe.setIngredient('P', Material.POPPED_CHORUS_FRUIT);
        anchorRecipe.setIngredient('O', Material.OBSIDIAN);
        anchorRecipe.setIngredient('C', Material.COMPASS);
        getServer().addRecipe(anchorRecipe);
        getCommand("relayrecipe").setExecutor(new GiveRecipeCommand());
        //dead fire coral texture = charged
        //dead horn coral texture = uncharged
        try {
            for(String s : Files.readAllLines(storedRelays.toPath())) {
                String[] stored = s.split(",");
                Location loc = new Location(getServer().getWorld(new NamespacedKey("minecraft", stored[0].substring(stored[0].indexOf(':')+1))), Integer.parseInt(stored[1]), Integer.parseInt(stored[2]), Integer.parseInt(stored[3]));
                Location lode = new Location(getServer().getWorld(new NamespacedKey("minecraft", stored[4].substring(stored[4].indexOf(':')+1))), Integer.parseInt(stored[5]), Integer.parseInt(stored[6]), Integer.parseInt(stored[7]));
                locToLode.put(loc, lode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("End Relays loaded!");
        //this goes ALL THE WAY AT THE END it clears the storedRelays file
        //so any relays have to be loaded before this is run
        try {
            Files.delete(storedRelays.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        File storedRelays = findStoredRelays();
        StringBuilder build = new StringBuilder();
        for(Location l : locToLode.keySet()) {
            build.append(l.getWorld().getKey());
            build.append(',');
            build.append(l.getBlockX());
            build.append(',');
            build.append(l.getBlockY());
            build.append(',');
            build.append(l.getBlockZ());
            build.append(',');
            Location lode = locToLode.get(l);
            build.append(lode.getWorld().getKey());
            build.append(',');
            build.append(lode.getBlockX());
            build.append(',');
            build.append(lode.getBlockY());
            build.append(',');
            build.append(lode.getBlockZ());
            build.append(new StringBuffer("\n"));
        }
        try {
            assert storedRelays != null;
            Files.writeString(storedRelays.toPath(), build.toString(), StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File findStoredRelays() {
        File dir = Bukkit.getPluginsFolder();
        boolean hasFile = false;
        for (File f : dir.listFiles()) {
            if (f.isFile() && f.getName().equals("relayStored.txt")) hasFile = true;
        }
        File storedRelays = null;
        if (hasFile) {
            for (File f : dir.listFiles()) {
                if (f.isFile() && f.getName().equals("relayStored.txt")) {
                    storedRelays = new File(f.getAbsolutePath());
                    Bukkit.getLogger().info("File found!");
                }
            }
        } else {
            storedRelays = new File(dir.getName() + "\\relayStored.txt");
            try {
                if (!storedRelays.createNewFile()) Bukkit.getLogger().info("Error creating file.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Bukkit.getLogger().info("Created file, no previous file was found");
        }
        if (storedRelays != null) {
            Bukkit.getLogger().info(storedRelays.getAbsolutePath());
            return storedRelays;
        }
        return null;
    }
}
