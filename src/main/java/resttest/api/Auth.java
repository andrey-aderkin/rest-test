package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {
	
    @JsonProperty("username")
    private String userName;
    @JsonProperty("password")
    private String password;

    public Auth(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}