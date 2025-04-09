package ep1n.endRelay;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class giveRelayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (sender instanceof Player p) {
            p.getInventory().addItem(EndRelay.instance.endAnchor);
            return true;
        }
        return false;
    }
}
