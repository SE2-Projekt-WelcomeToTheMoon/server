package WebsocketServer.services.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class CreateUserServiceTest {

    @Test
    void testCreateUserServiceWithValidUsername() {
        String username = "testUser";
        String sessionID = "testSessionID";
        CreateUserService userService = new CreateUserService(sessionID, username);

        assertEquals(username, userService.getUsername());
        assertEquals(sessionID, userService.getSessionID());
    }

    @Test
    void testCreateUserServiceWithEmptyUsername() {
        CreateUserService userService = new CreateUserService("", "");
        assertNull(userService.getUsername());
        assertNull(userService.getSessionID());
    }

    @Test
    void testCheckUserExistsWhenUserDoesNotExist() {
        String username = "testUser";
        String sessionID = "testSessionID";
        ManageUserService userList = Mockito.mock(ManageUserService.class);
        Mockito.when(userList.getUser(sessionID)).thenReturn(null);

        CreateUserService userService = new CreateUserService(sessionID, username);
        boolean result = userService.checkUserExists(sessionID);

        assertTrue(result);
    }

    @Test
    void testCheckUserExistsWhenUserExists() {
        String username = "existingUser";
        String sessionID = "testSessionID";
        ManageUserService userList = Mockito.mock(ManageUserService.class);
        Mockito.when(userList.getUser(sessionID)).thenReturn(new CreateUserService(sessionID, username));

        CreateUserService userService = new CreateUserService(sessionID, username);
        boolean result = userService.checkUserExists(sessionID);

        assertTrue(result);
    }

    @Test
    void testRegisterUserWithValidUsername() {//
        String username = "testUser";
        String sessionID = "testSessionID";
        CreateUserService userService = new CreateUserService(sessionID, username);
        assertEquals(username, userService.getUsername());
        assertEquals(sessionID, userService.getSessionID());
    }

    @Test
    void testRegisterUserWithEmptyUsername() {
        CreateUserService userService = new CreateUserService("", "");
        assertNull(userService.getUsername());
    }
}
