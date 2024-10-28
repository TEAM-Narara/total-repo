package com.narara.superboard.member.service;

import com.narara.superboard.common.exception.redis.RedisDataNotFoundException;
import com.narara.superboard.common.infrastructure.redis.RedisService;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.AccountDeletedException;
import com.narara.superboard.member.exception.AlreadyRegisteredLoginException;
import com.narara.superboard.member.exception.InvalidVerificationCodeException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.member.interfaces.dto.VerifyEmailCodeRequestDto;
import com.narara.superboard.member.service.validator.MemberValidator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final MemberValidator memberValidator;
    private final SpringTemplateEngine templateEngine;
    private final RedisService redisService;
    private final JavaMailSender emailSender;
    private final MemberRepository memberRepository;

    private static final long VERIFICATION_CODE_EXPIRY = 5 * 60L; // 인증코드 유효시간 (5분)

    @Override
    public void sendEmailVerificationCode(String email) {
        memberValidator.validateEmail(email);
        checkExistingMember(email);
        manageRedisData(email);
        sendVerificationEmailToUser(email);
    }

    @Override
    public void verifyEmailCode(VerifyEmailCodeRequestDto verifyEmailCodeRequestDto) {
        // 1. 이메일 코드 검증
        memberValidator.verifyEmailCodeValidate(verifyEmailCodeRequestDto);
        String email = verifyEmailCodeRequestDto.email();
        String code = verifyEmailCodeRequestDto.code();

        // 2. Redis에서 인증 코드 가져오기
        String storedCode = redisService.getData(email);

        // 3. 저장된 코드와 제공된 코드 비교
        if (!storedCode.equals(code)) {
            throw new InvalidVerificationCodeException(email, code);
        }

        // 4. 인증이 성공하면 Redis에서 코드를 삭제
        redisService.deleteData(email);
    }

    private void checkExistingMember(String email) {
        Optional<Member> existingMemberOptional = memberRepository.findByEmail(email);

        if (existingMemberOptional.isPresent()) {
            Member existingMember = existingMemberOptional.get();

            if (!existingMember.getIsDeleted()) {
                String loginType = existingMember.getLoginType().name();
                throw new AlreadyRegisteredLoginException(email, loginType);
            }
            throw new AccountDeletedException();
        }
    }

    private void manageRedisData(String email) {
        if (redisService.existData(email)) {
            redisService.deleteData(email); // 기존 인증 코드 삭제
        }
    }

    private void sendVerificationEmailToUser(String email) {
        String verificationCode = generateVerificationCode();

        try {
            redisService.setDataExpire(email, verificationCode, VERIFICATION_CODE_EXPIRY); // Redis에 인증 코드 저장
            sendVerificationEmail(email, verificationCode); // 이메일 전송
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패하였습니다.", e);
        }
    }

    private void sendVerificationEmail(String email, String code) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setTo(email);
        helper.setSubject("[인증 코드] " + code);
        helper.setText(setEmailContent(code), true); // HTML 템플릿 적용 및 이메일 내용 설정

        emailSender.send(message);
    }

    private String setEmailContent(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("email_authcode_form", context); // email_authcode_form.html을 사용
    }

    private String generateVerificationCode() {
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();

        for (int i = 0; i < 7; i++) {
            int rIndex = rnd.nextInt(3);
            switch (rIndex) {
                case 0 -> code.append((char) (rnd.nextInt(26) + 'a')); // 소문자 a-z
                case 1 -> code.append((char) (rnd.nextInt(26) + 'A')); // 대문자 A-Z
                case 2 -> code.append(rnd.nextInt(10)); // 숫자 0-9
            }
        }
        return code.toString();
    }
}
