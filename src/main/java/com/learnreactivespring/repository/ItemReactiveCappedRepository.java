package com.learnreactivespring.repository;

import com.learnreactivespring.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {

  // https://dalsacoo-log.tistory.com/entry/Spring-Data-MongoDB-Tailable-Cursors
  @Tailable
  Flux<ItemCapped> findItemsBy();
}
