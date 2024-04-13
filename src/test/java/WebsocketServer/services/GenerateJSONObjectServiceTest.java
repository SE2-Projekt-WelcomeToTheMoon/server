package WebsocketServer.services;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateJSONObjectServiceTest {
    private JSONObject jsonObject;


    @AfterEach
    public void tearDown(){
        jsonObject = null;
    }

    @Test
    public void testGenerateJSONObject(){
        assertNull(jsonObject);
        jsonObject = GenerateJSONObjectService.generateJSONObject("registerUser", "Dummy",
                false, "test", "test error");
        assertNotNull(jsonObject);
        assertEquals("{\"success\":false,\"action\":\"registerUser\",\"message\":\"test\"," +
                "\"error\":\"test error\",\"username\":\"Dummy\"}", jsonObject.toString());
        jsonObject = null;
        jsonObject = GenerateJSONObjectService.generateJSONObject("", "", null, "",
                "");
        assertNotNull(jsonObject);
        assertEquals("{\"action\":\"\",\"username\":\"\"}", jsonObject.toString());
    }
}
