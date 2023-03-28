package com.project.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.member.constant.DictionaryKey;
import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.entity.Member;
import com.project.member.model.entity.SmsAuthentication;
import com.project.member.model.request.MemberSmsRequest;
import com.project.member.model.request.MessagesRequest;
import com.project.member.model.request.SmsRequest;
import com.project.member.model.response.SmsResponse;
import com.project.member.repository.MemberRepository;
import com.project.member.utils.SmsUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * packageName   : com.project.member.service
 * fileName      : SmsService
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Service
@RequiredArgsConstructor
public class SmsService {

    private final RestTemplate restTemplate;

    @Value("${api.naver.sms.accessKey}")
    private String accessKey;

    @Value("${api.naver.sms.secretKey}")
    private String secretKey;

    @Value("${api.naver.sms.serviceId}")
    private String serviceId;

    @Value("${api.naver.sms.senderPhone}")
    private String senderPhone;

    private final MemberService memberService;

    private final MemberRepository memberRepository;

    private final SmsAuthenticationService smsAuthenticationService;

    // 회원 가입 인증 번호
    public SmsResponse sendSmsNumberCodeByMemberJoin(MemberSmsRequest memberSmsRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        String phoneNumber = memberSmsRequest.getPhoneNumber();

        // 회원 가입 전화번호 중복 여부 체크
        Optional<Member> memberByPhoneNumber = memberService.getMemberByPhoneNumber(phoneNumber);
        if(memberByPhoneNumber.isPresent()) {
            throw new SiteException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        phoneNumber = phoneNumber.replaceAll("-", "");
        String numberCode = SmsUtil.createNumberCode();
        MessagesRequest messagesRequest = new MessagesRequest(phoneNumber, numberCode);
        SmsResponse smsResponse = sendSms(messagesRequest);

        // sms 인증 저장
        if(StringUtils.equals(smsResponse.getStatusCode(), String.valueOf(HttpStatus.ACCEPTED.value()))) {
            smsAuthenticationService.createSmsAuthentication(memberSmsRequest.getPhoneNumber(), null, numberCode);
        }
        return smsResponse;
    }

    // 비밀번호 변경 인증 번호
    public SmsResponse sendSmsNumberCodeByUpdatePassword(MemberSmsRequest memberSmsRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        String phoneNumber = memberSmsRequest.getPhoneNumber();

        // 회원 가입 전화번호 존재 여부 체크
        Member member = memberService.getMemberByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SiteException(ErrorCode.MEMBER_NOT_FOUNT));

        phoneNumber = phoneNumber.replaceAll("-", "");
        String numberCode = SmsUtil.createNumberCode();
        MessagesRequest messagesRequest = new MessagesRequest(phoneNumber, numberCode);
        SmsResponse smsResponse = sendSms(messagesRequest);

        // sms 인증 저장
        if(StringUtils.equals(smsResponse.getStatusCode(), String.valueOf(HttpStatus.ACCEPTED.value()))) {
            smsAuthenticationService.createSmsAuthentication(member.getPhoneNumber(), member.getEmail(), numberCode);
        }
        return smsResponse;
    }

    public SmsResponse sendSms(MessagesRequest messagesRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException {

        Long time = System.currentTimeMillis();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        List<MessagesRequest> messages = new ArrayList<>();
        messages.add(messagesRequest);

        SmsRequest request = SmsRequest.builder()
                .type("SMS")
                .contentType("COMM")
                .countryCode("82")
                .from(senderPhone)
                .content("인증 번호 보내기")
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        SmsResponse response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+ serviceId +"/messages"), httpBody, SmsResponse.class);

        return response;
    }

    @Transactional
    public SmsAuthentication updatePasswordSmsAuthenticationYn(MemberSmsRequest memberSmsRequest) {
        if(StringUtils.isBlank(memberSmsRequest.getNumberCode())) {
            throw new SiteException(ErrorCode.INVALID_NUMBER_CODE);
        }

        Optional<Member> byPhoneNumber = memberRepository.findByPhoneNumber(memberSmsRequest.getPhoneNumber());
        if(!byPhoneNumber.isPresent()) {
            throw new SiteException(ErrorCode.MEMBER_NOT_FOUNT);
        }

        Member member = byPhoneNumber.get();
        return smsAuthenticationService.updateSmsAuthentication(member.getPhoneNumber(), member.getEmail(), memberSmsRequest.getNumberCode(), DictionaryKey.PASSWORD.getKey());
    }

    @Transactional
    public SmsAuthentication updateJoinSmsAuthenticationYn(MemberSmsRequest memberSmsRequest) {
        if(StringUtils.isBlank(memberSmsRequest.getNumberCode())) {
            throw new SiteException(ErrorCode.INVALID_NUMBER_CODE);
        }
        return smsAuthenticationService.updateSmsAuthentication(memberSmsRequest.getPhoneNumber(), null, memberSmsRequest.getNumberCode(), DictionaryKey.JOIN.getKey());
    }

    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/"+ this.serviceId+"/messages";
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
