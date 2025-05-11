package work.subscriptions.service;

import work.subscriptions.dto.SubscriptionDTO;

import java.util.List;
import java.util.UUID;

public interface SubscriptionsService {


    Long addSubscription(Long userId, SubscriptionDTO subscriptionDTO);

    List<SubscriptionDTO> getUserSubscriptions(Long userId);

    void deleteSubscription(Long userId, Long subId);

    List<SubscriptionDTO> getTopThreeSubscriptions();
}
