package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.Main;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SetRadiusLimitCommandTest {

    @Test
    public void testOnCommand() {
        // Arrange
        Main mockMain = Mockito.mock(Main.class);
        Player mockSender = Mockito.mock(Player.class);
        Command mockCommand = Mockito.mock(Command.class);
        String[] args = new String[]{"tnt", "10"};

        SetRadiusLimitCommand command = new SetRadiusLimitCommand(mockMain);

        // Act
        command.onCommand(mockSender, mockCommand,"", args);

        // Assert
        verify(mockMain).setTntRadiusLimit(10);
        verify(mockSender).sendMessage("TNT radius set to 10");
    }

    @Test
    public void testOnCommandWithNonPlayerSender() {
        // Arrange
        Main mockMain = Mockito.mock(Main.class);
        CommandSender mockSender = Mockito.mock(CommandSender.class);
        Command mockCommand = Mockito.mock(Command.class);
        String[] args = new String[]{"tnt", "10"};

        SetRadiusLimitCommand command = new SetRadiusLimitCommand(mockMain);

        // Act
        command.onCommand(mockSender, mockCommand,"", args);

        // Assert
        verify(mockSender).sendMessage("Only players can use this command.");
    }

    @Test
    public void testOnCommandWithNonNumericLimit() {
        // Arrange
        Main mockMain = Mockito.mock(Main.class);
        CommandSender mockSender = Mockito.mock(Player.class);
        Command mockCommand = Mockito.mock(Command.class);
        String[] args = new String[]{"tnt", "a"};

        SetRadiusLimitCommand command = new SetRadiusLimitCommand(mockMain);

        // Act
        command.onCommand(mockSender, mockCommand,"", args);

        // Assert
        verify((Player) mockSender).sendMessage("Please specify a valid integer.");
    }

    @Test
    public void testOnCommandWithNegativeLimit() {
        // Arrange
        Main mockMain = Mockito.mock(Main.class);
        CommandSender mockSender = Mockito.mock(Player.class);
        Command mockCommand = Mockito.mock(Command.class);
        String[] args = new String[]{"tnt", "-1"};

        SetRadiusLimitCommand command = new SetRadiusLimitCommand(mockMain);

        // Act
        command.onCommand(mockSender, mockCommand,"", args);

        // Assert
        verify((Player) mockSender).sendMessage("The limit must be a positive value.");
    }

    // Similarly other scenarios can be tested
}