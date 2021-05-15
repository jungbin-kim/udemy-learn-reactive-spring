package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest // MongoDB를 테스트 할 수 있는 Component 들을 로드해준다.
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {
  // Test 내에서는 Embedded MongoDB 가 사용된다.

  @Autowired
  ItemReactiveRepository itemReactiveRepository;

  List<Item> itemList = Arrays.asList(
      new Item(null, "Samsung TV", 400.0),
      new Item(null, "LG TV", 420.0),
      new Item(null, "Apple Watch", 299.99),
      new Item(null, "Beats Headphones", 149.99)
  );

  @Before
  public void setUp() {
    itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(itemList))
        .flatMap(itemReactiveRepository::save)
        .doOnNext((item -> {
          System.out.println("Insert Item is : " + item);
        }))
        .blockLast();
  }

  @Test
  public void getAllItems() {
    StepVerifier.create(itemReactiveRepository.findAll())
        .expectSubscription()
        .expectNextCount(4)
        .verifyComplete();
  }

}