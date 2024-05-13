package websocketserver.services.user;

import websocketserver.game.model.GameBoard;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

class CreateUserServiceTest {
    @Mock
    WebSocketSession sessionMock;

    @Test
    void testCreateUserServiceWithValidUsername() {
        String username = "testUser";
//        String sessionID = "testSessionID";
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        CreateUserService userService = new CreateUserService(session, username);

        assertEquals(username, userService.getUsername());
        assertEquals(session, userService.getSession());
    }

    @Test
    void testCreateUserServiceWithEmptyUsername() {
        CreateUserService userService = new CreateUserService(null, "");
        assertNull(userService.getUsername());
        assertNull(userService.getSessionID());
    }

    @Test
    void testCheckUserExistsWhenUserDoesNotExist() {
        String username = "testUser";
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        ManageUserService userList = Mockito.mock(ManageUserService.class);
        Mockito.when(userList.getUserBySessionID(session.getId())).thenReturn(null);

        CreateUserService userService = new CreateUserService(session, username);
        boolean result = userService.checkUserExists(session.getId(), username);

        assertTrue(result);
    }

    @Test
    void testCheckUserExistsWhenUserExists() {
        String username = "existingUser";

        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Mockito.when(session.getId()).thenReturn("existingSession");

        CreateUserService user = new CreateUserService(session, username);

        ManageUserService userList = Mockito.mock(ManageUserService.class);
        userList.addUser(user);
        Mockito.when(userList.getUserBySessionID(session.getId())).thenReturn(user);

        CreateUserService userService = new CreateUserService(session, username);
        boolean result = userService.checkUserExists(session.getId(), username);

        assertTrue(result);
    }

    @Test
    void testRegisterUserWithValidUsername() {//
        String username = "testUser";
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        CreateUserService userService = new CreateUserService(session, username);
        assertEquals(username, userService.getUsername());
        assertEquals(session.getId(), userService.getSessionID());
    }

    @Test
    void testRegisterUserWithEmptyUsername() {
        CreateUserService userService = new CreateUserService(null, "");
        assertNull(userService.getUsername());
    }

    @Test
    void testGetGameBoard(){
        MockitoAnnotations.openMocks(this);
        GameBoard gameBoard = new GameBoard();
        String testUser = "testUser";

        CreateUserService createUserService = new CreateUserService(sessionMock, testUser);
        createUserService.setGameBoard(gameBoard);

        assertEquals(gameBoard, createUserService.getGameBoard());
    }

    @Test
    void testCreateGameboard(){
        CreateUserService userService = new CreateUserService(null, "");
        userService.createGameBoard();
        assertNotNull(userService.getGameBoard());
    }
}
