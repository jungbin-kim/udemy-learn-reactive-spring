package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

  @Autowired
  ItemReactiveRepository itemReactiveRepository;

  @Override
  public void run(String... args) throws Exception {
    initialDataSetUp();
  }

  public List<Item> data() {
    return Arrays.asList(
        new Item(null, "Samsung TV", 399.99),
        new Item(null, "LG TV", 329.99),
        new Item(null, "Apple Watch", 349.99),
        new Item("ABC", "Beats Headphones", 19.99)
    );
  }

  private void initialDataSetUp() {
    itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(data()))
        .flatMap(itemReactiveRepository::save)
        .thenMany(itemReactiveRepository.findAll())
        .subscribe((item -> {
          System.out.println("Item inserted from CommandLineRunner : " + item);
        }))
    ;

  }


}
