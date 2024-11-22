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
    public void register(AbstractCommand<C, I> registrable) {
        super.register(registrable);
        registrable.apply(commandManager);
    }
}
