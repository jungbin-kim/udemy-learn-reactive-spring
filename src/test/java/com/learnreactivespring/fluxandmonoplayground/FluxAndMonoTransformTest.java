package com.learnreactivespring.fluxandmonoplayground;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {

  List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

  @Test
  public void transformUsingMap() {

    Flux<String> namesFlux = Flux.fromIterable(names)
        .map(s -> s.toUpperCase())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("ADAM", "ANNA", "JACK", "JENNY")
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_Length() {

    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(s -> s.length())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4, 4, 4, 5)
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_Length_repeat() {

    Flux<Integer> namesFlux = Flux.fromIterable(names)
        .map(s -> s.length())
        .repeat(1)
        .log();

    StepVerifier.create(namesFlux)
        .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
        .verifyComplete();
  }

  @Test
  public void transformUsingMap_Filter() {

    Flux<String> namesFlux = Flux.fromIterable(names)
        .filter(s -> s.length() > 4)
        .map(s -> s.toUpperCase())
        .log();

    StepVerifier.create(namesFlux)
        .expectNext("JENNY")
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap() {

    Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .flatMap(s -> {
          return Flux.fromIterable(
              convertToList(s)); // A ->  List(A, newValue), B -> List(B, newValue) ...
        }) // db or external service call that returns a flux
        .log();

    StepVerifier.create(stringFlux)
        .expectNextCount(12)
        .verifyComplete();
  }

  private List<String> convertToList(String s) {
    try {
      Thread.sleep(1000l);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return Arrays.asList(s, "new Value");
  }

  @Test
  public void transformUsingFlatMap_usingParallel() {

    Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .window(2)// Flux<Flux<String>> (A, B), (C, D), (E, F)
        .flatMap(s -> {
          return s
              .map(this::convertToList)
              .subscribeOn(Schedulers.parallel())
              .flatMap(Flux::fromIterable);
        }) // db or external service call that returns a flux
        .log();

    StepVerifier.create(stringFlux)
        .expectNextCount(12)
        .verifyComplete();
  }

  @Test
  public void transformUsingFlatMap_parallel_maintain_order() {

    Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F"))
        .window(2)// Flux<Flux<String>> (A, B), (C, D), (E, F)
//        .flatMapSequential( // flatMapSequential = 일단 오는대로 구독하고, 결과는 순서에 맞게 리턴. concatMap보다는 동시성이 보장되지만, 실행 순서를 보장하지 않는 것 같다.
        .concatMap( // concatMap = 입력 함수에서 리턴하는 Publisher의 스트림이 다 끝난 후 그 다음 값을 처리
            s -> {
              return s
                  .map(this::convertToList)
                  .subscribeOn(Schedulers.parallel())
                  .flatMap(Flux::fromIterable);
            }) // db or external service call that returns a flux
        .log();

    StepVerifier.create(stringFlux)
        .expectNextCount(12)
        .verifyComplete();
  }
}
