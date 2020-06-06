package example.com.gameapp;

public class LoginInfo {

    private String username;
    private String password;

    private static final LoginInfo ourInstance = new LoginInfo();

    public static LoginInfo getInstance() {
        return ourInstance;
    }

    private LoginInfo() {
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
