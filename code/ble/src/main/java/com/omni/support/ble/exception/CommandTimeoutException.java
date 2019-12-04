
package com.omni.support.ble.exception;


import com.omni.support.ble.core.ICommand;
import java.util.concurrent.TimeoutException;

/**
 * @author 邱永恒
 *
 * @time 2019/8/8 14:19
 *
 * @desc 指令超时
 *
 */
public class CommandTimeoutException extends TimeoutException {

    private ICommand<?> command;

    public CommandTimeoutException(String commandName, ICommand<?> command) {
        super(commandName + " Timeout");
        this.command = command;
    }

    public CommandTimeoutException(ICommand<?> command) {
        this("" + command, command);
    }

    public ICommand<?> getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "CommandTimeoutException{" +
                "command=" + command +
                '}';
    }
}
