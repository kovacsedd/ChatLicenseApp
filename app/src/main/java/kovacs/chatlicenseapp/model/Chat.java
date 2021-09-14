package kovacs.chatlicenseapp.model;

public class Chat {
    private String datetime;
    private String textMessage;
    private String url;
    private String type;
    private String sender;
    private String receiver;

    public Chat() {
    }

    public Chat(String datetime, String textMessage, String url, String type, String sender, String receiver) {
        this.datetime = datetime;
        this.textMessage = textMessage;
        this.url = url;
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
