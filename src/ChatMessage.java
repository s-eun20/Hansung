import java.text.SimpleDateFormat;
import java.util.Date;

//채팅 메시지를 나타내는 클래스
public class ChatMessage {
    private String sender;
    private String messageText;
    private long timestamp;

    public ChatMessage(String sender, String messageText, long timestamp) {
        this.sender = sender;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public String getMessageText() {
        return messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
}
