package com.learnreactivespring.controller;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@WebFluxTest
/*
* @WebFluxTest 어노테이션을 사용하면 전체 자동 구성이 비활성화되고 대신 WebFlux 테스트와 관련된 구성만 적용됩니다
* (예 : @Controller, @ControllerAdvice, @JsonComponent, Converter / GenericConverter 및 WebFluxConfigurer 빈)
* (제외 예: @Component, @Service 또는 @Repository 빈)
* JUnit4에서는 @RunWith(SpringRunner.class) 와 함께 사용해야한다.
* */
public class FluxAndMonoControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  public void flux_approach1() {

    Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(integerFlux)
        .expectSubscription()
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        .verifyComplete();
  }

  @Test
  public void flux_approach2() {

    webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
        .expectBodyList(Integer.class)
        .hasSize(4);

  }

  @Test
  public void flux_approach3() {

    List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

    EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Integer.class)
        .returnResult();

    assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());

  }

  @Test
  public void flux_approach4() {

    List<Integer> expectedIntegerList = Arrays.asList(1, 2, 3, 4);

    webTestClient.get().uri("/flux")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Integer.class)
        .consumeWith((response) -> {
          assertEquals(expectedIntegerList, response.getResponseBody());
        });
  }


  @Test
  public void fluxStream() {

    Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstream")
        .accept(MediaType.APPLICATION_STREAM_JSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(longStreamFlux)
        .expectNext(0l)
        .expectNext(1l)
        .expectNext(2l)
        .thenCancel()
        .verify();
  }

  @Test
  public void mono() {

    Integer expectedValue = new Integer(1);

    webTestClient.get().uri("/mono")
        .accept(MediaType.APPLICATION_JSON_UTF8)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Integer.class)
        .consumeWith((response) -> {
          assertEquals(expectedValue, response.getResponseBody());
        });
  }

}
