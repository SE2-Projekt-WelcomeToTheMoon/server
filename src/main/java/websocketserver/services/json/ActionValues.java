package websocketserver.services.json;

import lombok.Getter;

/**
 * Enums for action key in JSON Objects sent to client to ensure that no
 * errors occur when routing sent message on client.
 */
@Getter
public enum ActionValues {
    REGISTERUSER("registerUser"),
    JOINLOBBY("joinLobby"),
    LEAVELOBBY("leaveLobby"),
    REQUESTLOBBYUSER("requestLobbyUser"),
    REQUESTUSERSFORWINNINGSCREEN("requestUsersForWinningScreen"),
    STARTGAME("startGame"),
    RECONNECT("reconnect"),
    DISCONNECT("disconnect"),
    MAKEMOVE("makeMove");

    private final String value;
    ActionValues(String action){
        this.value = action;
    }

}
