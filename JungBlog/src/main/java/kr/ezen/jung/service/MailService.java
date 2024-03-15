package kr.ezen.jung.service;

import java.util.List;
import java.util.Map;

import kr.ezen.jung.vo.JungMemberVO;

public interface MailService {
	public String mailSend(String to);
	
	public Map<String, List<JungMemberVO>> adminMailSend(List<Integer> userIdxList, String title, String subject);
	
}
