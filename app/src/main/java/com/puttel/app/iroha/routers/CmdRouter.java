package com.puttel.app.iroha.routers;

import iroha.protocol.Commands;

public class CmdRouter extends Router<Commands.Command, Commands.Command.CommandCase> {

    public CmdRouter() {
        super(Commands.Command::getCommandCase);
    }
}
