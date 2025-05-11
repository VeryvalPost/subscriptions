package work.subscriptions.util;

import work.subscriptions.dto.SubscriptionDTO;
import work.subscriptions.model.Subscription;

public class SubscriptionMapper {
    public static SubscriptionDTO toDTO(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setId(subscription.getId());
        subscriptionDTO.setName(subscription.getName());
        subscriptionDTO.setPrice(subscription.getPrice());
        subscriptionDTO.setExpirationDate(subscription.getExpirationDate());
        return subscriptionDTO;
    }

    public static Subscription toEntity(SubscriptionDTO subscriptionDTO) {
        if (subscriptionDTO == null) {
            return null;
        }
        Subscription subscription = new Subscription();
        subscription.setId(subscriptionDTO.getId());
        subscription.setName(subscriptionDTO.getName());
        subscription.setPrice(subscriptionDTO.getPrice());
        subscription.setExpirationDate(subscriptionDTO.getExpirationDate());
        return subscription;
    }
}