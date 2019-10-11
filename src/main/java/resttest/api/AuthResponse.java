package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    
	@JsonProperty("token")
    private String token;

    public String getToken() {
        return token;
    }
}