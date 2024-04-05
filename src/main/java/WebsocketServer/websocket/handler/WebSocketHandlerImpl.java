package WebsocketServer.websocket.handler;

import WebsocketServer.services.LobbyService;
import WebsocketServer.services.UserClientService;
import org.json.JSONObject;
import org.springframework.web.socket.*;

public class WebSocketHandlerImpl implements WebSocketHandler {

    private LobbyService lobbyService;
    private final UserClientService userClientService;

    public WebSocketHandlerImpl(){
        this.lobbyService = new LobbyService();
        this.userClientService = new UserClientService();
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Verbindung erfolgreich hergestellt: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // TODO handle the messages here
        System.out.println("Nachricht erhalten: " + message.getPayload());
//        session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));

                            //-------------------//

        if(message.getPayload().equals("Test message")){
            session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
        }

        JSONObject messageJson = new JSONObject(message.getPayload().toString());
        String action = messageJson.optString("action", "");

        switch (action) {
            case "registerUser":
                //TODO: Method to register new User
                /**
                 *         JSONObject messsage = (JSONObject) message.getPayload();
                 *         if (messsage.get("Username") != null && messsage.get("setUserButtonId") == "2") {
                 *             if (UserClientService.setUsername(messsage.get("Username").toString())) {
                 *                 session.sendMessage(new TextMessage("Username set. Please continue."));
                 *             } else session.sendMessage(new TextMessage("Username already in use, please take another one."));
                 *         }
                 */
            case "joinLobby":
                System.out.println("Versuchen zur Lobby hinzufügen : " + session.getId() + " testUser ");
                  lobbyService.handleJoinLobby(session, messageJson);
                System.out.println("Erfolgreich zur Lobby hinzugefügt :  "+ session.getId() + " testUser ");
                  break;
            default:
                JSONObject response = new JSONObject();
                response.put("error", "unbekannte Aktion");
                response.put("action", action);
                session.sendMessage(new TextMessage(response.toString()));
                System.out.println("Unbekannte Aktion erhalten: " + action + " von " + "testUser");
                break;

        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Verbindung getrennt: "+ session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}