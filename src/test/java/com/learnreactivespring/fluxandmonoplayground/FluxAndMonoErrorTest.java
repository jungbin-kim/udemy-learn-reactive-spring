package com.learnreactivespring.fluxandmonoplayground;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

public class FluxAndMonoErrorTest {

  @Test
  public void fluxErrorHandling() {

    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
        .concatWith(Flux.just("D"))
        .onErrorResume((e) -> { // when error is occurred then this block executed
          System.out.println("Exception is : " + e);
          return Flux.just("default", "default1");
        });

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
//        .expectError(RuntimeException.class)
//        .verify()
        .expectNext("default", "default1")
        // error가 발생하면 원본 flux에서 그 뒤에 있는 D가 emit 하지 않은 것을 알 수 있다.
        .verifyComplete();

  }

  @Test
  public void fluxErrorHandling_OnErrorReturn() {

    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
        .concatWith(Flux.just("D"))
        .onErrorReturn("default");

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectNext("default")
        // error가 발생하면 원본 flux에서 그 뒤에 있는 D가 emit 하지 않은 것을 알 수 있다.
        .verifyComplete();

  }

  @Test
  public void fluxErrorHandling_OnErrorMap() {

    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e));

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C")
        .expectError(CustomException.class)
        .verify();

  }

  @Test
  public void fluxErrorHandling_OnErrorMap_withRetry() {

    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e))
        .retry(2);

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C") // original flux
        .expectNext("A", "B", "C") // retry 1
        .expectNext("A", "B", "C") // retry 2
        .expectError(CustomException.class)
        .verify();

  }

  @Test
  public void fluxErrorHandling_OnErrorMap_withRetryBackoff() {

    Flux<String> stringFlux = Flux.just("A", "B", "C")
        .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
        .concatWith(Flux.just("D"))
        .onErrorMap(e -> new CustomException(e))
        // retryBackoff 메서드는 버전 변경으로 deprecated
        // https://projectreactor.io/docs/core/3.3.11.RELEASE/api/deprecated-list.html
        .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));

    StepVerifier.create(stringFlux.log())
        .expectSubscription()
        .expectNext("A", "B", "C") // original flux
        .expectNext("A", "B", "C") // retry 1
        .expectNext("A", "B", "C") // retry 2
        .expectError(IllegalStateException.class)
        .verify();

  }
}
