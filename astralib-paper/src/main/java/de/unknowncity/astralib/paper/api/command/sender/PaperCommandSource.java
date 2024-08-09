package de.unknowncity.astralib.paper.api.command.sender;

import de.unknowncity.astralib.common.command.sender.CommandSource;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;

public class PaperCommandSource extends CommandSource<CommandSender> {
    private final CommandSourceStack commandSourceStack;
    public PaperCommandSource(CommandSender platformCommandSender, CommandSourceStack commandSourceStack) {
        super(platformCommandSender);
        this.commandSourceStack = commandSourceStack;
    }

    public CommandSourceStack commandSourceStack() {
        return commandSourceStack;
    }
}
