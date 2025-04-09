package ep1n.endRelay;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class EndRelay extends JavaPlugin {

    public static EndRelay instance;

    public ItemStack endAnchor;

    public NamespacedKey endAnchorKey;

    @Override
    public void onEnable() {
        instance = this;

        endAnchorKey = new NamespacedKey(this, "end_anchor");
        endAnchor = new ItemStack(Material.DEAD_HORN_CORAL_BLOCK);
        ItemMeta endAnchorMeta = endAnchor.getItemMeta();
        endAnchorMeta.customName(Component.text("End Relay"));
        endAnchor.setItemMeta(endAnchorMeta);
        //dead fire coral texture = charged
        //dead horn coral texture = uncharged
        //do this tmr in skuu
        // Plugin startup logic
        getCommand("giveme").setExecutor(new giveRelayCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
