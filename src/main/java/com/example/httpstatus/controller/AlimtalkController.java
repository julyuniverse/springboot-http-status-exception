package com.example.httpstatus.controller;

import com.example.httpstatus.dto.alimtalk.RequestSearchMessageDeliveryResultsDto;
import com.example.httpstatus.dto.alimtalk.ResponseSearchMessageDeliveryResultsDto;
import com.example.httpstatus.service.AlimtalkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Lee Taesung
 * @since 2022/08/01
 */

@RestController
@RequiredArgsConstructor // 생성자 자동 생성
public class AlimtalkController {
    private final AlimtalkService alimtalkService;

    // 메시지 발송 결과 조회
    @PostMapping("/alimtalk/search-message-delivery-results")
    public ResponseEntity<ResponseSearchMessageDeliveryResultsDto> searchMessageDeliveryResults(@RequestBody RequestSearchMessageDeliveryResultsDto requestSearchMessageDeliveryResultsDto) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        ResponseSearchMessageDeliveryResultsDto data = alimtalkService.searchMessageDeliveryResults(requestSearchMessageDeliveryResultsDto.getMessageId());

        return ResponseEntity.ok().body(data);
    }
}
