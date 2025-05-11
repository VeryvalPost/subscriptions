package work.subscriptions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Set;

@Data
public class CreateUserDTO {
    private Long id;
    private String username;
    private String password;
    private String email;
}