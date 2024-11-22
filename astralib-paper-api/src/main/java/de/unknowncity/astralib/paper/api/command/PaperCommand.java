package de.unknowncity.astralib.paper.api.command;

import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.astralib.common.command.AbstractCommand;
import org.bukkit.command.CommandSender;

public abstract class PaperCommand<I extends PaperAstraPlugin> extends AbstractCommand<CommandSender, I> {
    public PaperCommand(I plugin) {
        super(plugin);
    }
}
