package de.unknowncity.astralib.velocity.api.command;

import com.velocitypowered.api.command.CommandSource;
import de.unknowncity.astralib.common.command.AbstractCommand;
import de.unknowncity.astralib.velocity.api.plugin.VelocityAstraPlugin;

public abstract class VelocityCommand<I extends VelocityAstraPlugin> extends AbstractCommand<CommandSource, I> {
    public VelocityCommand(I plugin) {
        super(plugin);
    }
}
