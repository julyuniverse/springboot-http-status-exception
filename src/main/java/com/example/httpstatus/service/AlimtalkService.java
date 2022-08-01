package com.example.httpstatus.service;

import com.example.httpstatus.RestTemplateResponseErrorHandler;
import com.example.httpstatus.dto.alimtalk.ResponseSearchMessageDeliveryResultsDto;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Lee Taesung
 * @since 2022/08/01
 */

@Service
@Transactional
public class AlimtalkService {
    @Value("${ncloud.accessKey}")
    private String accessKey;

    @Value("${ncloud.secretKey}")
    private String secretKey;

    @Value("${ncloud.alimtalk.serviceId}")
    private String serviceId;

    // 메시지 발송 결과 조회
    public ResponseSearchMessageDeliveryResultsDto searchMessageDeliveryResults(String messageId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", this.accessKey);
        String signature = makeSearchMessageDeliveryResultsSignature(time, messageId);
        headers.set("x-ncp-apigw-signature-v2", signature);

        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler()); // ResponseErrorHandler를 재 구현한 클래스로 설정
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<ResponseSearchMessageDeliveryResultsDto> responseEntity = restTemplate.exchange(
                "https://sens.apigw.ntruss.com/alimtalk/v2/services/" + this.serviceId + "/messages/" + messageId,
                HttpMethod.GET,
                httpEntity,
                ResponseSearchMessageDeliveryResultsDto.class
        );

        int httpStatusCodeValue = responseEntity.getStatusCodeValue();

        if (httpStatusCodeValue != 200) { // 요청 성공이 아니라면
            int count = 0;

            while (count < 10) { // 요청을 최대 10번까지만 설정
                Thread.sleep(1000); // 1초 대기

                headers = new HttpHeaders();
                headers.set("x-ncp-apigw-timestamp", time.toString());
                headers.set("x-ncp-iam-access-key", this.accessKey);
                signature = makeSearchMessageDeliveryResultsSignature(time, "2e54bd4049d349cf9fed3ff1c0cf176c22");
                headers.set("x-ncp-apigw-signature-v2", signature);
                httpEntity = new HttpEntity<>("", headers);
                restTemplate = new RestTemplate();
                restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler()); // ResponseErrorHandler를 재 구현한 클래스로 설정
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
                responseEntity = restTemplate.exchange(
                        "https://sens.apigw.ntruss.com/alimtalk/v2/services/" + this.serviceId + "/messages/" + "2e54bd4049d349cf9fed3ff1c0cf176c22",
                        HttpMethod.GET,
                        httpEntity,
                        ResponseSearchMessageDeliveryResultsDto.class
                );

                if (responseEntity.getStatusCodeValue() == 200) {
                    break;
                }

                count++;
            }
        }

        return responseEntity.getBody();
    }

    // 메시지 발송 결과 조회 시그니처 생성
    public String makeSearchMessageDeliveryResultsSignature(Long time, String messageId) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "GET";
        String url = "/alimtalk/v2/services/" + this.serviceId + "/messages/" + messageId;
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
