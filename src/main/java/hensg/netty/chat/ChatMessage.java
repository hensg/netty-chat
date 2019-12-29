package hensg.netty.chat;

public class ChatMessage {

    private final String message;
    private final String originUser;

    public ChatMessage(String message, String originUser) {
        this.message = message;
        this.originUser = originUser;
    }

    public String getMessage() { return message; }

    public String getOriginUser() { return originUser; }
}
