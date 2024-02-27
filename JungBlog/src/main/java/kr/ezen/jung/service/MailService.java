package kr.ezen.jung.service;

import java.util.List;
import java.util.Map;

public interface MailService {
	public String mailSend(String to);
	
	public Map<String, List<String>> adminMailSend(List<Integer> userIdxList, String title, String subject);
}
