package work.subscriptions.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import work.subscriptions.dto.SubscriptionDTO;
import work.subscriptions.model.Subscription;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionsRepository extends JpaRepository<Subscription, Long> {

    @Query(nativeQuery = true, value =
            "SELECT s.* " +
                    "FROM subscriptions s " +
                    "LEFT JOIN user_subscriptions us ON s.id = us.subscription_id " +
                    "GROUP BY s.id " +
                    "ORDER BY COUNT(us.user_id) DESC " +
                    "LIMIT 3")
    List<Subscription> findTopThreePopularSubscriptions();


    Optional<Subscription> findByNameAndPrice(String name, BigDecimal price);
}
