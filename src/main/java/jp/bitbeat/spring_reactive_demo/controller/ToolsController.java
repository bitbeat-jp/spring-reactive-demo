package jp.bitbeat.spring_reactive_demo.controller;

import jp.bitbeat.spring_reactive_demo.model.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class ToolsController {

    private record UrlEncodeParam(String urlEncodeValue) {}

    private record UrlEncodeResponse(String status, String urlEncodedValue) {}

    private record UrlDecodeParam(String urlDecodeValue) {}

    private record UrlDecodeResponse(String status, String urlDecodedValue) {}

    private record RegexpCheckParam(String targetText, String pattern, Boolean ignoreCase, Boolean global, Boolean multiLine) {}

    private record Match(int start, String match, String[] groups) {}
    private record RegexpCheckResponse(String status, String resultText, List<Match> matches) {}

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
}
