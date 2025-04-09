package ep1n.endRelay;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class EndRelay extends JavaPlugin {

    public EndRelay instance;

    public ItemStack endAnchor;

    public NamespacedKey endAnchorKey;

    @Override
    public void onEnable() {
        instance = this;

        endAnchorKey = new NamespacedKey(this, "end_anchor");
        //dead fire coral texture = charged
        //dead horn coral texture = uncharged
        //do this tmr in skuu
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
