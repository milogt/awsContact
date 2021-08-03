package edu.uchicago.tiang.emailer;

public class Message {

    private String subject;
    private String body;
    private String emailFrom;

    public Message(String subject, String body, String emailFrom) {
        this.subject = subject;
        this.body = body;
        this.emailFrom = emailFrom;
    }

    public Message() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        this.emailFrom = emailFrom;
    }

    @Override
    public String toString() {
        return "Message{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", emailFrom='" + emailFrom + '\'' +
                '}';
    }
}
