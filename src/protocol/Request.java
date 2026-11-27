package protocol;
import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;
    private String action;
    private Object payload;
    private String userEmail;

    public Request(String action, Object payload, String userEmail) {
        this.action = action; this.payload = payload; this.userEmail = userEmail;
    }
    public String getAction() { return action; }
    public Object getPayload() { return payload; }
    public String getUserEmail() { return userEmail; }
}