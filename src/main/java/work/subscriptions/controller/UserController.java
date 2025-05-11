package work.subscriptions.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import work.subscriptions.dto.CreateUserDTO;
import work.subscriptions.dto.ErrorResponseDTO;
import work.subscriptions.dto.SubscriptionDTO;
import work.subscriptions.dto.UserDTO;
import work.subscriptions.exception.DatabaseException;
import work.subscriptions.exception.UserNotFoundException;
import work.subscriptions.exception.ValidationException;
import work.subscriptions.service.SubscriptionsService;
import work.subscriptions.service.UsersService;

import java.util.UUID;

/*Примерные эндпоинты:
POST /users - создать пользователя
GET /users/{id} - получить информацию о пользователе
PUT /users/{id} - обновить пользователя
DELETE /users/{id} - удалить пользователя
*/

@RestController
@Slf4j
public class UserController {
     private final UsersService usersService;
     private final  SubscriptionsService subscriptionsService;

     public UserController(UsersService usersService, SubscriptionsService subscriptionsService) {
         this.usersService = usersService;
         this.subscriptionsService = subscriptionsService;
     }

     @PostMapping("/users")
     public ResponseEntity<?> createUser(@RequestBody CreateUserDTO userDTO) {
         try {
             UserDTO createdUser = usersService.createUser(userDTO);
             return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
         } catch (ValidationException e) {
             log.error("Ошибка валидации при создании пользователя: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Ошибка валидации при создании пользователя: {}", e.getMessage()));
         } catch (DatabaseException e) {
             log.error("Ошибка базы данных при создании пользователя: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Ошибка базы данных при создании пользователя: {}", e.getMessage()));
         } catch (Exception e) {
             log.error("Неизвестная ошибка при создании пользователя: {}", e.getMessage(), e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Неизвестная ошибка при создании пользователя: {}", e.getMessage()));
         }
     }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = usersService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Ошибка: {}", e.getMessage()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при получении пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDTO("Неизвестная ошибка при создании пользователя: {}", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = usersService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            log.error("Пользователь не найден при обновлении: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Пользователь не найден при обновлении:", e.getMessage()));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при обновлении пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Неизвестная ошибка при обновлении пользователя:", e.getMessage()));
        }
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            usersService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.error("Пользователь не найден при удалении: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
