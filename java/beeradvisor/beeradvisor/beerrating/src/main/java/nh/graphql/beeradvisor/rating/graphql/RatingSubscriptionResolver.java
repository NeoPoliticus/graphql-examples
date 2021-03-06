package nh.graphql.beeradvisor.rating.graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import nh.graphql.beeradvisor.rating.Rating;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Component
public class RatingSubscriptionResolver implements GraphQLSubscriptionResolver {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private RatingPublisher ratingPublisher;

  public Publisher<Rating> onNewRating() {
    return this.ratingPublisher.getPublisher();
  }

  public Publisher<Rating> newRatings(String beerId) {
    logger.info("Subscription for 'newRatings' (" + beerId + ") received");
    return this.ratingPublisher.getPublisher(beerId);
  }
}
