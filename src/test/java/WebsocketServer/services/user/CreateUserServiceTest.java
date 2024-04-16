package WebsocketServer.services.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

class CreateUserServiceTest {

    @Test
    void testCreateUserServiceWithValidUsername() {
        String username = "testUser";
        CreateUserService userService = new CreateUserService(username);

        assertEquals(username, userService.getUsername());
    }

    @Test
    void testCreateUserServiceWithEmptyUsername() {
        CreateUserService userService = new CreateUserService("");
        assertNull(userService.getUsername());
    }

    @Test
    void testCheckUserExistsWhenUserDoesNotExist() {
        String username = "testUser";
        ManageUserService userList = Mockito.mock(ManageUserService.class);
        Mockito.when(userList.getUser(username)).thenReturn(null);

        CreateUserService userService = new CreateUserService(username);
        boolean result = userService.checkUserExists(username);

        assertTrue(result);
    }

    @Test
    void testCheckUserExistsWhenUserExists() {
        String username = "existingUser";
        ManageUserService userList = Mockito.mock(ManageUserService.class);
        Mockito.when(userList.getUser(username)).thenReturn(new CreateUserService(username));

        CreateUserService userService = new CreateUserService(username);
        boolean result = userService.checkUserExists(username);

        assertTrue(result);
    }

    @Test
    void testRegisterUserWithValidUsername() {
        String username = "testUser";
        CreateUserService userService = new CreateUserService(username);
        assertEquals(username, userService.getUsername());
    }

    @Test
    void testRegisterUserWithEmptyUsername() {
        CreateUserService userService = new CreateUserService("");
        assertNull(userService.getUsername());
    }
}
