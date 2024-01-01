package jp.bitbeat.spring_reactive_demo;

import jp.bitbeat.spring_reactive_demo.controller.ToolsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringReactiveDemoApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void testUrlEncode() {
		ToolsController.UrlEncodeParam param = new ToolsController.UrlEncodeParam("URLエンコード");

		Flux<ToolsController.UrlEncodeResponse> fluxResponse = webTestClient.post()
				.uri("/tools/url-encode")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.UrlEncodeResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.urlEncodedValue().equals("URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89"))
				.expectComplete()
				.verify();
	}

	@Test
	void testUrlDecode() {
		ToolsController.UrlDecodeParam param = new ToolsController.UrlDecodeParam("URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89");

		Flux<ToolsController.UrlDecodeResponse> fluxResponse = webTestClient.post()
				.uri("/tools/url-decode")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.UrlDecodeResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.urlDecodedValue().equals("URLエンコード"))
				.expectComplete()
				.verify();
	}

	@Test
	void testRegexpCheck() {
		ToolsController.RegexpCheckParam param = new ToolsController.RegexpCheckParam(
				"2023-12-25 13:30:12\n2023-12-27 9:02:03\n2023-12-30 15:44:42",
				"[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}",
				Boolean.FALSE,
				Boolean.TRUE,
				Boolean.TRUE
		);

		Flux<ToolsController.RegexpCheckResponse> fluxResponse = webTestClient.post()
				.uri("/tools/regexp-check")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.RegexpCheckResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.matches().size() == 2)
				.expectComplete()
				.verify();
	}

	@Test
	void testDateTimeDiff() {
		ToolsController.DateTimeDiffParam param = new ToolsController.DateTimeDiffParam(
				LocalDateTime.of(2023, 12, 28, 17, 37, 33),
				LocalDateTime.of(2023, 12, 25, 12, 30, 00)
		);

		Flux<ToolsController.DateTimeDiffResponse> fluxResponse = webTestClient.post()
				.uri("/tools/datetime-diff")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.DateTimeDiffResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.resultSeconds() == 277653)
				.expectComplete()
				.verify();
	}

	@Test
	void testDateTimeAdd() {
		ToolsController.DateTimeCalcParam param = new ToolsController.DateTimeCalcParam(
				LocalDateTime.of(2023, 12, 29, 16, 7, 28),
				LocalTime.of(2, 30, 15),
				false
		);

		Flux<ToolsController.DateTimeCalcResponse> fluxResponse = webTestClient.post()
				.uri("/tools/datetime-calc")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.DateTimeCalcResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.resultDateTime().equals(LocalDateTime.of(2023, 12, 29, 18, 37, 43)))
				.expectComplete()
				.verify();
	}

	@Test
	void testDateTimeSub() {
		ToolsController.DateTimeCalcParam param = new ToolsController.DateTimeCalcParam(
				LocalDateTime.of(2023, 12, 29, 16, 7, 28),
				LocalTime.of(2, 30, 15),
				true
		);

		Flux<ToolsController.DateTimeCalcResponse> fluxResponse = webTestClient.post()
				.uri("/tools/datetime-calc")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(param))
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(ToolsController.DateTimeCalcResponse.class)
				.getResponseBody();

		StepVerifier.create(fluxResponse)
				.expectNextMatches(response -> response.resultDateTime().equals(LocalDateTime.of(2023, 12, 29, 13, 37, 13)))
				.expectComplete()
				.verify();
	}
}
