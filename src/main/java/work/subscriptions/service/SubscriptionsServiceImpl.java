package work.subscriptions.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.subscriptions.dto.SubscriptionDTO;
import work.subscriptions.exception.UserNotFoundException;
import work.subscriptions.exception.ValidationException;
import work.subscriptions.model.Subscription;
import work.subscriptions.model.User;
import work.subscriptions.repository.SubscriptionsRepository;
import work.subscriptions.repository.UserRepository;
import work.subscriptions.util.SubscriptionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final SubscriptionsRepository subscriptionsRepository;
    private final UserRepository userRepository;

    public SubscriptionsServiceImpl(SubscriptionsRepository subscriptionsRepository, UserRepository userRepository) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Long addSubscription(Long userId, SubscriptionDTO subscriptionDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь ID %d не найден", userId);
                    log.error(errorMessage);
                    return new UserNotFoundException(errorMessage);
                });

        if (subscriptionDTO == null || subscriptionDTO.getName() == null || subscriptionDTO.getName().isEmpty()) {
            String errorMessage = "Пустая подписка";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }


        Optional<Subscription> existingSubscription = subscriptionsRepository.findByNameAndPrice(
                subscriptionDTO.getName(), subscriptionDTO.getPrice());


        if (existingSubscription.isPresent()) {
            Subscription subscription = existingSubscription.get();
            // Если пользователь уже подписан на эту подписку, то выбрасываем исключение
            if (user.getSubscriptions().contains(subscription)) {
                String errorMessage = String.format("Пользователь  %d уже подписан на эту подписку %s", userId, subscription.getName());
                log.error(errorMessage);
                throw new ValidationException(errorMessage);
            }
            // Если пользователь не подписан на эту подписку, то нам ее надо добавить
            user.getSubscriptions().add(subscription);
            userRepository.save(user);
            Long id = subscription.getId();
            log.info("Подписка с ID {} добавлена для пользователя с ID {}", id, userId);
            return id;
        }
        // А этот путь пойдет, если подписки еще нет в базе
        Subscription newSubscription = SubscriptionMapper.toEntity(subscriptionDTO);
        subscriptionsRepository.save(newSubscription);
        user.getSubscriptions().add(newSubscription);
        userRepository.save(user);
        Long id = newSubscription.getId();
        log.info("Подписка с ID {} добавлена для пользователя с ID {}", id, userId);
        return id;
    }

    @Override
    public List<SubscriptionDTO> getUserSubscriptions(Long userId) {
        User user = userRepository.findByIdWithSubscriptions(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь ID %d не найден", userId);
                    log.error(errorMessage);
                    return new UserNotFoundException(errorMessage);
                });

        List<SubscriptionDTO> result = user.getSubscriptions().stream()
                .map(SubscriptionMapper::toDTO)
                .collect(Collectors.toList());

        return result;
    }

    @Transactional
    @Override
    public void deleteSubscription(Long userId, Long subId) {

        User user = userRepository.findByIdWithSubscriptions(userId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь с ID %d не найден", userId);
                    log.error(errorMessage);
                    return new UserNotFoundException(errorMessage);
                });


        Subscription subscription = subscriptionsRepository.findById(subId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Подписка с ID %d не найдена", subId);
                    log.error(errorMessage);
                    return new ValidationException(errorMessage);
                });


        if (!user.getSubscriptions().contains(subscription)) {
            String errorMessage = String.format("Пользователь с ID %d не имеет подписки с ID %d", userId, subId);
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        user.getSubscriptions().remove(subscription);
        userRepository.save(user);
        log.info("Подписка с ID {} удалена у пользователя с ID {}", subId, userId);
    }


    @Override
    public List<SubscriptionDTO> getTopThreeSubscriptions() {
        List<Subscription> topSubscriptions = subscriptionsRepository.findTopThreePopularSubscriptions();
        List<SubscriptionDTO> result = topSubscriptions.stream()
                .map(SubscriptionMapper::toDTO)
                .collect(Collectors.toList());

        log.info("Получено {} популярных подписок", result.size());
        return result;
    }

}
