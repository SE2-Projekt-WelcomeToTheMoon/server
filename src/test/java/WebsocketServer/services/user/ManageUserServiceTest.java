package WebsocketServer.services.user;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;


import java.util.List;

class ManageUserServiceTest {

    private ManageUserService manageUserService;

    @BeforeEach
    void setUp() {
        manageUserService = new ManageUserService();
    }

    @AfterEach
    void tearDown() {
        manageUserService.cleanUpUserList();
    }

    @Test
    void testAddUser() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getSessionID()).thenReturn("testUser");

        manageUserService.addUser(user);

        assertEquals(user, manageUserService.getUserBySessionID("testUser"));
    }

    @Test
    void testAddExistingUser() {
        CreateUserService user1 = Mockito.mock(CreateUserService.class);
        Mockito.when(user1.getSessionID()).thenReturn("testUser");

        CreateUserService user2 = Mockito.mock(CreateUserService.class);
        Mockito.when(user2.getSessionID()).thenReturn("testUser");

        manageUserService.addUser(user1);
        manageUserService.addUser(user2);

        assertNotNull(manageUserService.getUserBySessionID("testUser"));
    }

    @Test
    void testDeleteUser() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getSessionID()).thenReturn("testUser");

        manageUserService.addUser(user);
        manageUserService.deleteUser("testUser");

        assertNull(manageUserService.getUserBySessionID("testUser"));
    }

    @Test
    void testDeleteNonExistingUser() {
        manageUserService.deleteUser("nonExistingUser");
        assertNull(manageUserService.getUserBySessionID("nonExistingUser"));
    }

    @Test
    void testGetUserBySessionID() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getSessionID()).thenReturn("testUser");

        manageUserService.addUser(user);

        assertEquals(user, manageUserService.getUserBySessionID("testUser"));
    }

    @Test
    void testGetNonExistingUser() {
        assertNull(manageUserService.getUserBySessionID("nonExistingUser"));
    }

    @Test
    void testGetAllUsers() {
        CreateUserService user1 = Mockito.mock(CreateUserService.class);
        Mockito.when(user1.getSessionID()).thenReturn("user1");

        CreateUserService user2 = Mockito.mock(CreateUserService.class);
        Mockito.when(user2.getSessionID()).thenReturn("user2");

        manageUserService.addUser(user1);
        manageUserService.addUser(user2);

        List<CreateUserService> userList = manageUserService.getAllUsers();

        assertEquals(2, userList.size());
        assertTrue(userList.contains(user1));
        assertTrue(userList.contains(user2));
    }

    @Test
    void testCleanUpUserList() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getSessionID()).thenReturn("testUser");

        manageUserService.addUser(user);
        manageUserService.cleanUpUserList();
        assertEquals(0, manageUserService.getAllUsers().size());

    }

}
