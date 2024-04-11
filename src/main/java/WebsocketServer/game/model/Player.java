package WebsocketServer.game.model;

import WebsocketServer.game.services.GameBoardService;

public class Player {
    GameBoard gameBoard;

    public Player(GameBoardService gameBoardService){
        gameBoard = gameBoardService.createGameBoard();
    }
}
