package de.unknowncity.astralib.common.command;

import de.unknowncity.astralib.common.registry.Registry;
import org.incendo.cloud.CommandManager;

public class CommandRegistry<C, I> extends Registry<I, AbstractCommand<C, I>> {
    private final CommandManager<C> commandManager;

    public CommandRegistry(I plugin, CommandManager<C> commandManager) {
        super(plugin);
        this.commandManager = commandManager;
    }


    @Override
    public <R extends AbstractCommand<C, I>> void register(R registrable) {
        super.register(registrable);
        registrable.apply(commandManager);
    }
}
