package com.learnreactivespring.router;

import static com.learnreactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import com.learnreactivespring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemRouter {

  @Bean
  public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler) {
    return RouterFunctions.route(
        GET(ITEM_FUNCTIONAL_END_POINT_V1)
            .and(accept(MediaType.APPLICATION_JSON)),
        itemHandler::getAllItems);
  }

}
