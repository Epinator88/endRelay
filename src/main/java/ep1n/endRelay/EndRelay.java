package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
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

    public Map<Location, ItemStack> blockMap;

    public NamespacedKey locArr;

    @Override
    public void onEnable() {
        
        instance = this;
        blockMap = new HashMap<>();

        File storedRelays = findStoredRelays();
        //for every line
        //first 3 ints are coords of relay, last 3 are of lodestone

        locArr = new NamespacedKey(this, "LODE_COORDINATES");

        endAnchorKey = new NamespacedKey(this, "end_anchor");
        endAnchor = new ItemStack(Material.DEAD_HORN_CORAL_BLOCK);
        ItemMeta endAnchorMeta = endAnchor.getItemMeta();
        endAnchorMeta.customName(Component.text("End Relay"));
        endAnchorMeta.setMaxStackSize(1);
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
        //dead fire coral texture = charged
        //dead horn coral texture = uncharged
        //do this tmr in skuu
        // Plugin startup logic
        getCommand("giveme").setExecutor(new giveRelayCommand());
        getLogger().info("End Relays loaded!");
        //have the locations stored in a file, and read/write when needed.
        //do this when you get to utah gng, see how boonmoygamer did it

        //store the location of the relay as the lodestone location, on every place write those to a file somewhere. DONE
        //onEnable, add every string to blockMap, as <Location, ItemStack.setData(Location)>
        //its bbq chicken from there (if it works lmfao)

        //make it dragon and tnt indestructible
        //make it so hoppers can supply end crystals



        //this goes ALL THE WAY AT THE END it clears the storedRelays file
        //so any relays have to be loaded before this is run
        try {
            Files.writeString(storedRelays.toPath(), null, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        File storedRelays = findStoredRelays();
        StringBuilder build = new StringBuilder();
        for(Location l : blockMap.keySet()) {
            build.append(l.getBlockX());
            build.append(',');
            build.append(l.getBlockY());
            build.append(',');
            build.append(l.getBlockZ());
            build.append(',');
            Location lode = blockMap.get(l).getData(DataComponentTypes.LODESTONE_TRACKER).location();
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
