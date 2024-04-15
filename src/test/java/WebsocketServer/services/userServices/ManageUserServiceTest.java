package WebsocketServer.services.userServices;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;


import java.util.List;

public class ManageUserServiceTest {

    private ManageUserService manageUserService;

    @BeforeEach
    public void setUp() {
        manageUserService = new ManageUserService();
    }

    @AfterEach
    public void tearDown() {
        manageUserService.cleanUpUserList();
    }

    @Test
    public void testAddUser() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getUsername()).thenReturn("testUser");

        manageUserService.addUser(user);

        assertEquals(user, manageUserService.getUser("testUser"));
    }

    @Test
    public void testAddExistingUser() {
        CreateUserService user1 = Mockito.mock(CreateUserService.class);
        Mockito.when(user1.getUsername()).thenReturn("testUser");

        CreateUserService user2 = Mockito.mock(CreateUserService.class);
        Mockito.when(user2.getUsername()).thenReturn("testUser");

        manageUserService.addUser(user1);
        manageUserService.addUser(user2);

        assertNotNull(manageUserService.getUser("testUser"));
    }

    @Test
    public void testDeleteUser() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getUsername()).thenReturn("testUser");

        manageUserService.addUser(user);
        manageUserService.deleteUser("testUser");

        assertNull(manageUserService.getUser("testUser"));
    }

    @Test
    public void testDeleteNonExistingUser() {
        manageUserService.deleteUser("nonExistingUser");
        assertNull(manageUserService.getUser("nonExistingUser"));
    }

    @Test
    public void testGetUser() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getUsername()).thenReturn("testUser");

        manageUserService.addUser(user);

        assertEquals(user, manageUserService.getUser("testUser"));
    }

    @Test
    public void testGetNonExistingUser() {
        assertNull(manageUserService.getUser("nonExistingUser"));
    }

    @Test
    public void testGetAllUsers() {
        CreateUserService user1 = Mockito.mock(CreateUserService.class);
        Mockito.when(user1.getUsername()).thenReturn("user1");

        CreateUserService user2 = Mockito.mock(CreateUserService.class);
        Mockito.when(user2.getUsername()).thenReturn("user2");

        manageUserService.addUser(user1);
        manageUserService.addUser(user2);

        List<CreateUserService> userList = manageUserService.getAllUsers();

        assertEquals(2, userList.size());
        assertTrue(userList.contains(user1));
        assertTrue(userList.contains(user2));
    }

    @Test
    public void testCleanUpUserList() {
        CreateUserService user = Mockito.mock(CreateUserService.class);
        Mockito.when(user.getUsername()).thenReturn("testUser");

        manageUserService.addUser(user);
        manageUserService.cleanUpUserList();

        assertThrows(NullPointerException.class, () -> manageUserService.getUser("testUser"));
    }

}
