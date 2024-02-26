package kr.ezen.jung.service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;

@Service(value = "mailService")
@Slf4j
public class MailServiceImpl implements MailService{
   
   @Autowired
   private JavaMailSender javaMailSender; // 메일을 보내기 위한 객체
   
   @Override
   public String mailSend(String to) {
      MailHandler mailHandler = null;
      String authCode = null;
      try {
         mailHandler = new MailHandler(javaMailSender);
         
         mailHandler.setFrom("tjdtlr12349@naver.com", "jungBlogCompany");   // 누가
         mailHandler.setTo(to);                                    // 누구에게
         mailHandler.setSubject("jungBlog 회원가입 인증 번호");           // 제목
         
         // 1. 인증번호 만들기
         authCode = createCode(); // 인증 코드 생성
         
         // 2. 메일 전송
         mailHandler.setText("인증번호: " + authCode);                  // 내용
         log.info("메일 전송 중...");
         mailHandler.send();   // 전송
         log.info("메일 전송 성공!!!!!!");
         
         
         log.info("메일 전송 성공!!!!!!");
      } catch (MessagingException e) {
         log.info("메일 전송 실패!!!!!!");
         e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
         log.info("메일 전송 실패!!!!!!");
         e.printStackTrace();
      }
      // 3. 메일번호 리턴
      return authCode;
   }
   
   // 인증 코드 생성 메서드
   private String createCode() {
      // 인증번호에 사용될 문자들
      String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
      
      // 무작위로 생성된 인증번호를 담을 문자열 변수
      StringBuilder code = new StringBuilder();
      
      // Random 객체 생성
      Random random = new Random();
      
      // 6자리의 인증번호 생성
      for (int i = 0; i < 6; i++) {
         // characters 문자열 중 무작위 인덱스 선택
         int randomIndex = random.nextInt(characters.length());
         
         // 선택된 인덱스에 해당하는 문자를 인증번호 문자열에 추가
         code.append(characters.charAt(randomIndex));
      }
      
      // 생성된 인증번호 반환
      return code.toString();
   }
}