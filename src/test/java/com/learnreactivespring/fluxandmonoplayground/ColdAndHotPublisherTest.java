package com.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdAndHotPublisherTest {

  @Test
  public void coldPublisherTest() throws InterruptedException {

    Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
        .delayElements(Duration.ofSeconds(1));

    // emits the value from beginning
    stringFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

    Thread.sleep(2000);

    // emits the value from beginning
    stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

    Thread.sleep(4000);
  }

  @Test
  public void hotPublisherTest() throws InterruptedException {
    Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
        .delayElements(Duration.ofSeconds(1));

    ConnectableFlux<String> connectableFlux = stringFlux.publish();
    connectableFlux.connect();
    connectableFlux.subscribe(s -> System.out.println("Subscriber 1 : " + s));

    Thread.sleep(3000);

    // Does not emit the values from beginning
    connectableFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));

    Thread.sleep(4000);

  }


}
