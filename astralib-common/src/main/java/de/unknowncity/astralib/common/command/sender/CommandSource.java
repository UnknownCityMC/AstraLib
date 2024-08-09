package de.unknowncity.astralib.common.command.sender;

public abstract class CommandSource<C> {
    protected final C platformCommandSender;

    public CommandSource(C platformCommandSender) {
        this.platformCommandSender = platformCommandSender;
    }

    public C platformCommandSender() {
        return platformCommandSender;
    }
}
