package WebsocketServer.services;

import org.json.JSONObject;

public class GenerateJSONObjectService {

    /**
     * Methode um ein JSONObject zu generieren
     * @param action    Aktion die ausgeführt werden soll
     * @param username  Benutzername
     * @param success   Erfolgreich oder nicht
     * @param message   Nachricht
     * @param error     Fehlermeldung
     * @return         JSONObject
     */
    public static JSONObject generateJSONObject(String action, String username, Boolean success, String message, String error) {
        JSONObject response = new JSONObject();
        if (action != null) {
            response.put("action", action);
        }
        if (username != null) {
            response.put("username", username);
        }
        if (success != null) {
            response.put("success", success);
        }
        //nur wenn String leer ist
        if (message != null && !message.isEmpty()) {
            response.put("message", message);
        }
        if (error != null && !error.isEmpty()) {
            response.put("error", error);
        }

        return response;
    }
}