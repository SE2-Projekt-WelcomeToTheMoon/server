package WebsocketServer.services;

import org.json.JSONException;
import org.json.JSONObject;

public class GenerateJSONObjectService {

    public static JSONObject generateJSONObject() throws JSONException {
        return new JSONObject(
                "{\"username\":\"\",\"action\":\"\",\"message\":\"\", \"error\":\"\"}"
        );
    }
}