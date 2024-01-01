package jp.bitbeat.spring_reactive_demo.controller;

import jp.bitbeat.spring_reactive_demo.model.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ToolsController {

    public record UrlEncodeParam(String urlEncodeValue) {}

    public record UrlEncodeResponse(String status, String urlEncodedValue) {}

    public record UrlDecodeParam(String urlDecodeValue) {}

    public record UrlDecodeResponse(String status, String urlDecodedValue) {}

    public record RegexpCheckParam(String targetText, String pattern, Boolean ignoreCase, Boolean global, Boolean multiLine) {}

    public record Match(int start, String match, String[] groups) {}
    public record RegexpCheckResponse(String status, String resultText, List<Match> matches) {}

    public record DateTimeDiffParam(LocalDateTime datetime1, LocalDateTime datetime2) {}

    public record DateTimeDiffResponse(String status, long resultSeconds) {}

    public record DateTimeCalcParam(LocalDateTime baseDateTime, LocalTime addTime, Boolean isSubtraction) {}

    public record DateTimeCalcResponse(String status, LocalDateTime resultDateTime) {}

    @PostMapping("/tools/url-encode")
    public Mono<UrlEncodeResponse> urlEncode(@RequestBody UrlEncodeParam param) {
        String urlEncodedValue = URLEncoder.encode(param.urlEncodeValue(), StandardCharsets.UTF_8);
        return Mono.just(new UrlEncodeResponse(ResponseStatus.SUCCESS.value(), urlEncodedValue));
    }

    @PostMapping("/tools/url-decode")
    public Mono<UrlDecodeResponse> urlDecode(@RequestBody UrlDecodeParam param) {
        String urlDecodedValue = URLDecoder.decode(param.urlDecodeValue(), StandardCharsets.UTF_8);
        return Mono.just(new UrlDecodeResponse(ResponseStatus.SUCCESS.value(), urlDecodedValue));
    }

    @PostMapping("/tools/regexp-check")
    public Mono<RegexpCheckResponse> regexpCheck(@RequestBody RegexpCheckParam param) {
        int flags = 0;
        if (Boolean.TRUE.equals(param.ignoreCase())) {
            flags |= Pattern.CASE_INSENSITIVE;
        }
        if (Boolean.TRUE.equals(param.multiLine())) {
            flags |= Pattern.MULTILINE;
        }
        boolean global = Boolean.TRUE.equals(param.global());

        try {
            List<Match> matches = new ArrayList<>();
            Matcher matcher = Pattern.compile(param.pattern(), flags).matcher(param.targetText());
            while (matcher.find()) {
                int start = matcher.start();
                String match = matcher.group();
                String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < matcher.groupCount(); i++) {
                    groups[i] = matcher.group(i + 1);  // 前方参照部分の取得
                }
                matches.add(new Match(start, match, groups));

                if (!global) {
                    break;  // Globalオプション無効の場合は検索1回で終了
                }
            }

            return Mono.just(new RegexpCheckResponse(ResponseStatus.SUCCESS.value(), param.targetText(), matches));
        }
        catch (IllegalArgumentException e) {
            return Mono.error(e);
        }
    }

    @PostMapping("/tools/datetime-diff")
    public Mono<DateTimeDiffResponse> dateTimeDiff(@RequestBody DateTimeDiffParam param) {
        Duration duration = Duration.between(param.datetime1(), param.datetime2()).abs();
        return Mono.just(new DateTimeDiffResponse(ResponseStatus.SUCCESS.value(), duration.getSeconds()));
    }

    @PostMapping("/tools/datetime-calc")
    public Mono<DateTimeCalcResponse> dateTimeCalc(@RequestBody DateTimeCalcParam param) {
        Duration duration = Duration.ZERO
                .plusHours(param.addTime().getHour())
                .plusMinutes(param.addTime().getMinute())
                .plusSeconds(param.addTime().getSecond());
        LocalDateTime resultDateTime;
        if (Boolean.TRUE.equals(param.isSubtraction())) {
            resultDateTime = param.baseDateTime().minus(duration);
        } else {
            resultDateTime = param.baseDateTime().plus(duration);
        }
        return Mono.just(new DateTimeCalcResponse(ResponseStatus.SUCCESS.value(), resultDateTime));
    }
}
