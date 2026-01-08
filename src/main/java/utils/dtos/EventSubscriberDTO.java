package utils.dtos;

import java.time.LocalDateTime;

public class EventSubscriberDTO {
    private final Long userId;
    private final LocalDateTime date;

    public EventSubscriberDTO(Long userId, LocalDateTime date) {
        this.userId = userId;
        this.date = date;
    }

    public Long getUserId() {
        return userId;
    }
    public LocalDateTime getDate() {
        return date;
    }

}
