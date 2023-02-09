package si.example.rabbitmqcustom.processing.annotation.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TypeExchange {
    DIRECT("direct"),
    FANOUT("fanout"),
    HEADERS("headers"),
    TOPIC("topic"),
    X_DELAYED_MESSAGE("x-delayed-message");

    private final String value;
}
