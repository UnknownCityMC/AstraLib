package de.unknowncity.astralib.common.command;

import de.unknowncity.astralib.common.registry.registrable.Registrable;
import org.incendo.cloud.CommandManager;

public abstract class AbstractCommand<C, I> implements Registrable<I> {
    protected I plugin;

    public AbstractCommand(I plugin) {
        this.plugin = plugin;
    }

    /**
     * Builds a command and adds handlers for command execution logic
     * @param commandManager the command manager to which the command should be registered
     */
    public abstract void apply(CommandManager<C> commandManager);
}
