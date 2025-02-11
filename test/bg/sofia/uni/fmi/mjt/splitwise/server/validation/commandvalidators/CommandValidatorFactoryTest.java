package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators;

import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.ServiceValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.RegisterCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.LoginCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.authentication.LogOutCommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CommandValidatorFactoryTest {

    private AuthenticationManager authManagerMock;
    private ServiceValidatorFactory validatorFactoryMock;
    private CommandValidatorFactory commandValidatorFactory;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        validatorFactoryMock = mock(ServiceValidatorFactory.class);
        commandValidatorFactory = CommandValidatorFactory.getInstance(authManagerMock, validatorFactoryMock);
    }

    @Test
    void testGetInstance_ReturnsSameInstance() {
        CommandValidatorFactory anotherInstance = CommandValidatorFactory.getInstance(authManagerMock, validatorFactoryMock);
        assertSame(commandValidatorFactory, anotherInstance, "CommandValidatorFactory should follow Singleton pattern.");
    }

    @Test
    void testGetValidator_ReturnsRegisterCommandValidator() {
        CommandValidator validator = commandValidatorFactory.getValidator("register");
        assertNotNull(validator, "RegisterCommandValidator should not be null.");
        assertTrue(validator instanceof RegisterCommandValidator, "Should return an instance of RegisterCommandValidator.");
    }

    @Test
    void testGetValidator_ReturnsLoginCommandValidator() {
        CommandValidator validator = commandValidatorFactory.getValidator("login");
        assertNotNull(validator, "LoginCommandValidator should not be null.");
        assertTrue(validator instanceof LoginCommandValidator, "Should return an instance of LoginCommandValidator.");
    }

    @Test
    void testGetValidator_ReturnsLogOutCommandValidator() {
        CommandValidator validator = commandValidatorFactory.getValidator("logout");
        assertNotNull(validator, "LogOutCommandValidator should not be null.");
        assertTrue(validator instanceof LogOutCommandValidator, "Should return an instance of LogOutCommandValidator.");
    }

    @Test
    void testGetValidator_ReturnsNullForInvalidCommand() {
        CommandValidator validator = commandValidatorFactory.getValidator("invalid-command");
        assertNull(validator, "Should return null for an unknown command.");
    }
}
