package com.omni.support.ble.exception;

import com.omni.support.ble.core.ICommand;

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 14:19
 *
 * @desc 指令异常
 *
 */
public class CommandException extends Exception {

    private ICommand<?> command;

    public CommandException(String commandName, ICommand<?> command, Throwable e) {
        super(commandName + " Failure", e);
        this.command = command;
    }

    public CommandException(ICommand<?> command, Throwable e) {
        this("" + command, command, e);
    }

    public ICommand<?> getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "CommandException{" +
                "command=" + command +
                '}';
    }
}
