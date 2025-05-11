package work.subscriptions.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import work.subscriptions.dto.ErrorResponseDTO;
import work.subscriptions.dto.SubscriptionDTO;
import work.subscriptions.exception.UserNotFoundException;
import work.subscriptions.exception.ValidationException;
import work.subscriptions.model.Subscription;
import work.subscriptions.service.SubscriptionsService;

import java.util.List;
import java.util.UUID;

/*Примерные эндпоинты:
POST /users/{id}/subscriptions - добавить подписку
GET /users/{id}/subscriptions - получить подписки пользователя
DELETE /users/{id}/subscriptions/{sub_id} - удалить подписку
GET /subscriptions/top - получить ТОП-3 популярных подписок*/

@RestController
@Slf4j
public class SubscriptionsController {
    private final SubscriptionsService subscriptionsService;

    public SubscriptionsController(SubscriptionsService subscriptionsService) {
        this.subscriptionsService = subscriptionsService;
    }

    @PostMapping("/users/{id}/subscriptions")
    public ResponseEntity<?> addSubscription(@PathVariable Long id, @RequestBody SubscriptionDTO subscriptionDTO) {
        try {
            Long createdId = subscriptionsService.addSubscription(id, subscriptionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdId);
        } catch (ValidationException e) {
            log.error("Ошибка валидации при создании подписки: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Ошибка валидации при создании подписки:", "BAD_REQUEST"));
        } catch (UserNotFoundException e) {
            log.error("Пользователь не найден при создании подписки:: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Пользователь не найден при создании подписки:", "NOT_FOUND"));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при создании подписки: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Неизвестная ошибка при создании подписки:", "INTERNAL_SERVER_ERROR"));
        }
    }


    @GetMapping("/users/{id}/subscriptions")
    public ResponseEntity<?> getUserSubscriptions(@PathVariable Long id) {
        try {
            List<SubscriptionDTO> subscriptions = subscriptionsService.getUserSubscriptions(id);
            return ResponseEntity.ok(subscriptions);
        } catch (UserNotFoundException e) {
            log.error("Пользователь не найден при поиске подписки:: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Пользователь не найден при поиске подписки:", "NOT_FOUND"));
        } catch (Exception e) {
            log.error("Неизвестная ошибка при поиске подписки: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Неизвестная ошибка при поиске подписки:", "INTERNAL_SERVER_ERROR"));
        }
    }

    @DeleteMapping("/users/{id}/subscriptions/{sub_id}")
    public ResponseEntity<?> deleteSubscription(@PathVariable Long id, @PathVariable Long sub_id) {
        try {
            subscriptionsService.deleteSubscription(id, sub_id);
            log.info("Подписка с ID {} удалена для пользователя с ID {}", sub_id, id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.error("Ошибка: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Пользователь не найден", "NOT_FOUND"));
        } catch (ValidationException e) {
            log.error("Ошибка валидации: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Ошибка валидации", e.getMessage()));
        } catch (Exception e) {
            log.error("Непредвиденная ошибка: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Неизвестная ошибка при поиске подписки:", "INTERNAL_SERVER_ERROR"));
        }
    }

    @GetMapping("/subscriptions/top")
    public ResponseEntity<?> getTopThreeSubscription() {
        try {
            List<SubscriptionDTO> topSubscriptions = subscriptionsService.getTopThreeSubscriptions();
            log.info("Получено {} популярных подписок", topSubscriptions.size());
            return ResponseEntity.ok(topSubscriptions);
        } catch (Exception e) {
            log.error("Непредвиденная ошибка при получении популярных подписок: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDTO("Непредвиденная ошибка при получении популярных подписок:", "INTERNAL_SERVER_ERROR"));
        }
    }

}
