package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.GameBoard;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameBoardManagerTest {
    @Mock
    private Lobby gameLobby;
    @Mock
    private WebSocketSession session;

    private GameBoardManager gameBoardManager;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameBoardManager = new GameBoardManager(session, gameLobby);
    }

    @Test
    void shouldReturnGameBoardStringWhenUserExists() throws Exception {
        String username = "existingUser";
        GameBoard gameBoard = new GameBoard();
        gameBoard.finalizeGameBoard();
        ObjectMapper mapper = new ObjectMapper();
        String gameBoardString = mapper.writeValueAsString(gameBoard);

        when(gameLobby.findUser(username)).thenReturn(true);
        when(gameLobby.getGameBoard(username)).thenReturn(gameBoard);

        String result = gameBoardManager.getGameBoardUser(username);

        assertEquals(gameBoardString, result);
    }


    @Test
    void shouldReturnNullWhenExceptionOccurs() {
        String username = "existingUser";

        when(gameLobby.findUser(username)).thenReturn(false);
        when(gameLobby.getGameBoard(username)).thenReturn(null);
        String result = gameBoardManager.getGameBoardUser(username);

        assertNull(result);
    }

    @Test
    void shouldInitializeAllGameBoardsCorrectly() {
        when(gameLobby.getUserListFromLobby()).thenReturn(new ArrayList<>(Arrays.asList("user1", "user2")));

        assertTrue(gameBoardManager.initGameBoards());
        verify(gameLobby, times(2)).setGameBoardUser(anyString(), any(GameBoard.class));
    }


    @Test
    void shouldFailToUpdateUserWhenJsonIsMalformed() {
        String username = "validUser";
        String malformedJson = "{state:active}";

        when(gameLobby.findUser(username)).thenReturn(true);

        assertFalse(gameBoardManager.updateUser(username, malformedJson));
    }

    @Test
    void shouldReturnFalseWhenUserNotFound() {
        String username = "nonexistentUser";
        String message = "{\"state\":\"active\"}";

        when(gameLobby.findUser(username)).thenReturn(false);

        boolean result = gameBoardManager.updateUser(username, message);

        assertFalse(result);
        verify(gameLobby, never()).setGameBoardUser(anyString(), any(GameBoard.class));
    }

    @Test
    void shouldSuccessfullyUpdateGameBoard() throws Exception {
        String username = "existingUser";
        GameBoard gameBoard = new GameBoard();
        gameBoard.finalizeGameBoard();
        ObjectMapper mapper = new ObjectMapper();
        String message = mapper.writeValueAsString(gameBoard);

        when(gameLobby.findUser(username)).thenReturn(true);
        when(gameLobby.setGameBoardUser(eq(username), any(GameBoard.class))).thenReturn(true);

        boolean result = gameBoardManager.updateUser(username, message);

        assertTrue(result);
        verify(gameLobby, times(1)).setGameBoardUser(eq(username), any(GameBoard.class));
    }

    @Test
    void shouldReturnFalseWhenJsonParsingFails() {
        String username = "existingUser";
        String badJsonMessage = "{bad_json}";

        when(gameLobby.findUser(username)).thenReturn(true);

        boolean result = gameBoardManager.updateUser(username, badJsonMessage);

        assertFalse(result);
        verify(gameLobby, never()).setGameBoardUser(anyString(), any(GameBoard.class));
    }

    @Test
    void shouldHandleExceptionDuringUpdate() {
        String username = "validUser";
        String json = "{\"state\":\"active\"}";

        when(gameLobby.findUser(username)).thenReturn(true);
        doThrow(new RuntimeException("JSON parse error")).when(gameLobby).setGameBoardUser(anyString(), any(GameBoard.class));

        assertFalse(gameBoardManager.updateUser(username, json));
    }

    @Test
    void shouldReturnNullForNullUsernameInGetGameBoardUser() {
        assertNull(gameBoardManager.getGameBoardUser(null));
    }
}