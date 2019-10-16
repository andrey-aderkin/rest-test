package resttest.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Auth {

    @JsonProperty("username")
    private String userName;
    @JsonProperty("password")
    private String password;

    public Auth(String admUserName, String admPassword) {
        this.userName = admUserName;
        this.password = admPassword;
    }
}
