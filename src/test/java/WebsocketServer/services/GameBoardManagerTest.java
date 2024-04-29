package WebsocketServer.services;

import WebsocketServer.game.lobby.Lobby;
import WebsocketServer.game.model.GameBoard;
import WebsocketServer.services.user.CreateUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.logging.slf4j.*;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class GameBoardManagerTest {
    @Mock
    private Lobby gameLobby;

    private GameBoardManager gameBoardManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameBoardManager = new GameBoardManager(gameLobby);
    }

    @Test
    void shouldReturnGameBoardStringWhenUserExists() throws Exception {
        String username = "existingUser";
        GameBoard gameBoard = new GameBoard();
        gameBoard.finalizeGameBoard();
        ObjectMapper mapper = new ObjectMapper();
        String gameBoardString = mapper.writeValueAsString(gameBoard);

        when(gameLobby.getUser(username)).thenReturn(true);
        when(gameLobby.getGameBoard(username)).thenReturn(gameBoard);

        String result = gameBoardManager.getGameBoardUser(username);

        assertEquals(gameBoardString, result);
    }


    @Test
    void shouldReturnNullWhenUserDoesNotExist() {
        String username = "nonExistingUser";

        when(gameLobby.getUser(username)).thenReturn(true);

        String result = gameBoardManager.getGameBoardUser(username);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenExceptionOccurs() {
        String username = "existingUser";

        when(gameLobby.getUser(username)).thenReturn(false);
        // returns a STRING null
        when(gameLobby.getGameBoard(username)).thenReturn(null);
        String result = gameBoardManager.getGameBoardUser(username);

        assertEquals("null", result);
    }
}