package com.example.httpstatus.dto.alimtalk;

import lombok.*;

/**
 * @author Lee Taesung
 * @since 2022/08/01
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class RequestSearchMessageDeliveryResultsDto { // 메시지 발송 결과 조회 요청
    private String messageId;
}
