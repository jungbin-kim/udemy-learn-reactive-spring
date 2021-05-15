package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest // MongoDB를 테스트 할 수 있는 Component 들을 로드해준다.
@RunWith(SpringRunner.class)
/*
@DirtiesContext: 테스트를 수행하기 전, 후, 각 테스트 케이스 수행 전, 후 context를 다시 생성하도록 함
강의에서는 @DirtiesContext 어노테이션을 해야 통과되는데, 버전이 달라서 그런지 안해도 된다.
 */
//@DirtiesContext
public class ItemReactiveRepositoryTest {
  // Test 내에서는 Embedded MongoDB 가 사용된다.

  @Autowired
  ItemReactiveRepository itemReactiveRepository;

  List<Item> itemList = Arrays.asList(
      new Item(null, "Samsung TV", 400.0),
      new Item(null, "LG TV", 420.0),
      new Item(null, "Apple Watch", 299.99),
      new Item(null, "Beats Headphones", 149.99),
      new Item("ABC", "Bose Headphones", 149.99)

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
        .expectNextCount(itemList.size())
        .verifyComplete();
  }

  @Test
  public void getItemById() {
    StepVerifier.create(itemReactiveRepository.findById("ABC"))
        .expectSubscription()
        .expectNextMatches((item -> item.getDescription().equals("Bose Headphones")))
        .verifyComplete();
  }

  @Test
  public void findItemByDescription() {
    // exactly match description
    StepVerifier
        .create(itemReactiveRepository
            .findByDescription("Bose Headphones")
            .log("findItemByDescription : "))
        .expectSubscription()
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  public void saveItem() {
    Item item = new Item(null, "Google Home Mini", 30.00);
    Mono<Item> saveItem = itemReactiveRepository.save(item);

    StepVerifier.create(saveItem.log("saveItem : "))
        .expectSubscription()
        .expectNextMatches(
            item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
        .verifyComplete();

  }

  @Test
  public void updateItem() {
    double newPrice = 520.00;
    Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
        .map(item -> {
          item.setPrice(newPrice); // Setting the new price
          return item;
        })
        .flatMap(itemReactiveRepository::save); // saving the item with the new price

    StepVerifier.create(updatedItem)
        .expectSubscription()
        .expectNextMatches(item -> item.getPrice() == 520.00)
        .verifyComplete();
  }

  @Test
  public void deleteItemById() {
    Mono<Void> deletedItem = itemReactiveRepository.findById("ABC") // Mono<Item>
        .map(Item::getId) // get Id -> map을 사용해서 type 변경
        .flatMap(id -> {
          return itemReactiveRepository.deleteById(id);
        });

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("The enw Item List : "))
        .expectNextCount(itemList.size() - 1)
        .verifyComplete();
  }

  @Test
  public void deleteItem() {
    Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV") // Mono<Item>
        .flatMap(item -> {
          return itemReactiveRepository.delete(item);
        });

    StepVerifier.create(deletedItem.log())
        .expectSubscription()
        .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("The enw Item List : "))
        .expectNextCount(itemList.size() - 1)
        .verifyComplete();
  }
}
