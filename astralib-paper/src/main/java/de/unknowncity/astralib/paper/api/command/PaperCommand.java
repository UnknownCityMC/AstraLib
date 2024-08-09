package de.unknowncity.astralib.paper.api.command;

import de.unknowncity.astralib.paper.api.command.sender.PaperCommandSource;
import de.unknowncity.astralib.paper.api.plugin.PaperAstraPlugin;
import de.unknowncity.astralib.common.command.AbstractCommand;

public abstract class PaperCommand<I extends PaperAstraPlugin> extends AbstractCommand<PaperCommandSource, I> {
    public PaperCommand(I plugin) {
        super(plugin);
    }
}
