package WebsocketServer.services.json;

import lombok.Getter;

/**
 * Enums for action key in JSON Objects sent to server to ensure that no
 * errors occur when routing sent message on server.
 */
@Getter
public enum ActionValues {
    REGISTERUSER("registerUser"),
    JOINLOBBY("joinLobby"),
    LEAVELOBBY("leaveLobby");

    private final String value;
    ActionValues(String action){
        this.value = action;
    }

}
