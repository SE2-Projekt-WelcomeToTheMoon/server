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

        if(message.getPayload().equals("Test message")){
            session.sendMessage(new TextMessage("echo from handler: " + message.getPayload()));
        }
        JSONObject messageJson = new JSONObject(message.getPayload().toString());

        String action = (String) messageJson.getString("action");

        switch (action) {
            case "registerUser":
                UserClientService.registerUser(session, messageJson);
                break;
            case "joinLobby":
                System.out.println("Versuchen zur Lobby hinzufügen : " + session.getId() + " testUser ");
                lobbyService.handleJoinLobby(session, messageJson);
                System.out.println("Erfolgreich zur Lobby hinzugefügt :  "+ session.getId() + " testUser ");
                break;
            default:
                JSONObject response = new JSONObject();
                response.put("error", "Unbekannte Aktion");
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