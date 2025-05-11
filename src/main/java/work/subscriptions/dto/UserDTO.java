package work.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class UserDTO {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String email;
    private Set<SubscriptionDTO> subscriptions;
}