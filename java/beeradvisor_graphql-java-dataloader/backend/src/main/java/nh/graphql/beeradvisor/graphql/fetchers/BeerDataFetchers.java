package nh.graphql.beeradvisor.graphql.fetchers;

import graphql.schema.DataFetcher;
import nh.graphql.beeradvisor.Utils;
import nh.graphql.beeradvisor.domain.Beer;
import nh.graphql.beeradvisor.domain.Rating;
import nh.graphql.beeradvisor.domain.Shop;
import nh.graphql.beeradvisor.domain.ShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Component
public class BeerDataFetchers {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ShopService shopService;

    public BeerDataFetchers(ShopService shopService) {
        this.shopService = shopService;
    }

    public DataFetcher<List<Shop>> shopsFetcher() {
        return environment -> {
            Beer beer = environment.getSource();
            final String beerId = beer.getId();
            return shopService.findShopsForBeer(Utils.listOf(beerId));
        };
    }

    public DataFetcher<Integer> averageStarsFetcher() {
        return environment -> {
            Beer beer = environment.getSource();
            return (int) Math.round(beer.getRatings().stream().mapToDouble(Rating::getStars).average().getAsDouble());
        };
    }

    public DataFetcher<List<Rating>> ratingsWithStarsFetcher() {
        return environment -> {
            Beer beer = environment.getSource();
            int starsInput = environment.getArgument("stars");
            return beer.getRatings().stream().filter(r -> r.getStars() == starsInput).collect(Collectors.toList());
        };
    }
}
