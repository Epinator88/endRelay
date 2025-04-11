package ep1n.endRelay;

import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.DataComponentValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.N;

import javax.naming.Name;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class EndRelay extends JavaPlugin {

    public static EndRelay instance;

    public ItemStack endAnchor;

    public NamespacedKey endAnchorKey;

    public Map<Location, ItemStack> blockMap;

    public Map<Location, Location> relayToLodestone;

    public NamespacedKey locArr;

    @Override
    public void onEnable() {
        instance = this;
        blockMap = new HashMap<>();
        relayToLodestone = new HashMap<>();

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
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
