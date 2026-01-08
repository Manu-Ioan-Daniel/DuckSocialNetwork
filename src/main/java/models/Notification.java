package models;

public class Notification extends Entity<Long> {
    private String message;
    private Long userId;
    public Notification(String message,Long userId) {
        this.message=message;
        this.userId = userId;
    }
    public String getMessage(){
        return message;
    }
    public Long getUserId(){
        return userId;
    }

}
