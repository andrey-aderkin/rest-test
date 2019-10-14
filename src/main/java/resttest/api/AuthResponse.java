package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

public class AuthResponse {
    
	@JsonProperty("token")
    private String token;

    public static String getToken(String url, String userName, String password) {
        Auth auth = new Auth(userName, password);
        return
            given().
            when().
                contentType(ContentType.JSON).
                body(auth).
                post(url + "/auth").
                getBody().
                jsonPath().
                get("token").
                toString();
    }
}