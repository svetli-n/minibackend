package backend.model;

import backend.controller.validator.ValidateWith;
import backend.controller.validator.Validator;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Session <T> {

    private final String value = UUID.randomUUID().toString();
    private final Instant creationTime = Instant.now();
    @ValidateWith(validator=Validator.class)
    private final T userId;
    private final long expireInSeconds;

    public Session(T userId, long expireInSeconds) {
        this.userId = userId;
        this.expireInSeconds = expireInSeconds;
    }

    String getValue() {
        return value;
    }

    T getUserId() { return userId; }

    boolean isExpired() {
        Instant now = Instant.now();
        if(now.isAfter(creationTime.plus(Duration.ofSeconds(expireInSeconds)))) {
            return true;
        }
        return false;
    }

}
