package nh.graphql.beeradvisor.graphql;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import nh.graphql.beeradvisor.auth.graphql.LoginDataFetchers;
import nh.graphql.beeradvisor.graphql.fetchers.BeerAdvisorDataFetcher;
import nh.graphql.beeradvisor.graphql.fetchers.BeerDataFetchers;
import nh.graphql.beeradvisor.graphql.fetchers.ShopFieldDataFetchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.util.Map;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 */
@Configuration
public class BeerAdvisorGraphQLConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BeerAdvisorDataFetcher beerAdvisorDataFetcher;
    @Autowired
    private BeerDataFetchers beerDataFetchers;
    @Autowired
    private ShopFieldDataFetchers shopFieldDataFetchers;
    @Autowired
    private LoginDataFetchers loginDataFetchers;

    @Value("classpath:/schema/auth.graphqls")
    private Resource authSchemaResource;
    @Value("classpath:/schema/rating.graphqls")
    private Resource ratingSchemaResource;
    @Value("classpath:/schema/shop.graphqls")
    private Resource shopSchemaResource;

    @Bean
    public GraphQLSchema graphQLSchema() throws Exception {

        SchemaParser schemaParser = new SchemaParser();

        TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();
        typeRegistry.merge(schemaParser.parse(new InputStreamReader(authSchemaResource.getInputStream())));
        typeRegistry.merge(schemaParser.parse(new InputStreamReader(ratingSchemaResource.getInputStream())));
        typeRegistry.merge(schemaParser.parse(new InputStreamReader(shopSchemaResource.getInputStream())));

        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        final RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
            .type(newTypeWiring("Query")
                .dataFetcher("beer", beerAdvisorDataFetcher.beerFetcher())
                .dataFetcher("beers", beerAdvisorDataFetcher.beersFetcher())
                .dataFetcher("shops", beerAdvisorDataFetcher.shopsFetcher())
                .dataFetcher("shop", beerAdvisorDataFetcher.shopFetcher()))
            .type(newTypeWiring("Beer")
                .dataFetcher("averageStars", beerDataFetchers.averageStarsFetcher())
                .dataFetcher("ratingsWithStars", beerDataFetchers.ratingsWithStarsFetcher())
                .dataFetcher("shops", beerDataFetchers.shopsFetcher()))
            .type(newTypeWiring("Shop")
                .dataFetcher("address", shopFieldDataFetchers.addressFetcher())
                .dataFetcher("beers", shopFieldDataFetchers.beersFetcher()))
            .type(newTypeWiring("Mutation")
                .dataFetcher("login", loginDataFetchers.getLoginDataFetcher())
                .dataFetcher("addRating", beerAdvisorDataFetcher.addRatingMutationFetcher()))
            .type(newTypeWiring("Subscription")
                .dataFetcher("newRatings", beerAdvisorDataFetcher.newRatingsSubscriptionFetcher())
                .dataFetcher("onNewRating", beerAdvisorDataFetcher.onNewRatingSubscriptionFetcher())
            )
            .build();

        return runtimeWiring;
    }



//  @Bean
//  public ServletRegistrationBean graphQLServletRegistrationBean(GraphQLSchema schema) {
//      final GraphQLHttpServlet servlet = GraphQLHttpServlet.with(schema);
//      return new ServletRegistrationBean<>(servlet, "/graphql");
//  }
//
//  @Bean
//  public ServerEndpointRegistration serverEndpointRegistration(GraphQLSchema schema) {
//    final DefaultGraphQLSchemaProvider schemaProvider = new DefaultGraphQLSchemaProvider(schema);
//    final GraphQLQueryInvoker queryInvoker = GraphQLQueryInvoker.newBuilder().build();
//
//    final GraphQLWebsocketServlet websocketServlet = new GraphQLWebsocketServlet(queryInvoker, GraphQLInvocationInputFactory.newBuilder(schemaProvider).build(), GraphQLObjectMapper.newBuilder().build());
//    return new GraphQLWsServerEndpointRegistration("/subscriptions", websocketServlet);
//  }
//
//  @Bean
//  @ConditionalOnMissingBean
//  public ServerEndpointExporter serverEndpointExporter() {
//    return new ServerEndpointExporter();
//  }
//
//  class GraphQLWsServerEndpointRegistration extends ServerEndpointRegistration implements Lifecycle {
//
//    private final GraphQLWebsocketServlet servlet;
//
//    public GraphQLWsServerEndpointRegistration(String path, GraphQLWebsocketServlet servlet) {
//      super(path, servlet);
//      this.servlet = servlet;
//    }
//
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
//      super.modifyHandshake(sec, request, response);
//      servlet.modifyHandshake(sec, request, response);
//    }
//
//    @Override
//    public void start() {
//    }
//
//    @Override
//    public void stop() {
//      servlet.beginShutDown();
//    }
//
//    @Override
//    public boolean isRunning() {
//      return !servlet.isShutDown();
//    }
//  }

}
