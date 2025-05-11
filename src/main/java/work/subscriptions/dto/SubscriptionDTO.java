package work.subscriptions.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubscriptionDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private LocalDateTime expirationDate;

}