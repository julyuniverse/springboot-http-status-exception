package com.example.httpstatus.dto.alimtalk;

import lombok.*;

/**
 * @author Lee Taesung
 * @since 2022/08/01
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ResponseSearchMessageDeliveryResultsDto { // 메시지 발송 결과 조회 응답
    private String messageId;
    private String countryCode; // 국가 코드
}
