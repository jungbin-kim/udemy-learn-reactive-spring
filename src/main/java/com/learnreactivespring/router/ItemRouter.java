package com.learnreactivespring.router;

import com.learnreactivespring.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ItemRouter {

  @Bean
  public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler) {
    return null;
  }

}
