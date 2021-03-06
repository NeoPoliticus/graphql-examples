package nh.graphql.beeradvisor.rating.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import nh.graphql.beeradvisor.rating.Beer;
import nh.graphql.beeradvisor.rating.BeerRepository;
import nh.graphql.beeradvisor.rating.Rating;
import nh.graphql.beeradvisor.user.User;
import nh.graphql.beeradvisor.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Component
public class RatingMutationResolver implements GraphQLMutationResolver {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  private BeerRepository beerRepository;

  @Transactional
  // EXAMPLE:
  // access fields from graphql query in Spring Security: userId must match logged
  // in user
  // (checks makes no sense, just for demo!)
  @PreAuthorize("isAuthenticated() && #addRatingInput.userId == authentication.principal.id")
  public Rating addRating(AddRatingInput addRatingInput) {
    User user = userRepository.getUser(addRatingInput.getUserId());
    Beer beer = beerRepository.getBeer(addRatingInput.getBeerId());

    Rating rating = beer.addRating(user, addRatingInput.getComment(), addRatingInput.getStars());
    beerRepository.saveBeer(beer);

    applicationEventPublisher.publishEvent(new RatingCreatedEvent(rating));

    return rating;
  }
}
