package de.unknowncity.astralib.paper.api.command.sender;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaperPlayerCommandSource extends PaperCommandSource {

    public PaperPlayerCommandSource(Player platformCommandSender, CommandSourceStack commandSourceStack) {
        super(platformCommandSender, commandSourceStack);
    }

}
