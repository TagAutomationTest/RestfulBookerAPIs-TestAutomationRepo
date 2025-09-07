package Utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;

import java.util.Map;

public class ResponseUtils {
    public static Map<String, Object> toMap(Response response) {
        try {
            return new ObjectMapper().readValue(
                    response.asString(),
                    new TypeReference<Map<String, Object>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert response to Map", e);
        }
    }
}
