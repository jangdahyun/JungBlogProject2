package kr.ezen.jung.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.MailService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/adm")
public class JungMemberManController {

	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private JungBoardService jungBoardService;
	
	
	@GetMapping(value = {"","/"})
	public String man(HttpSession session, Model model) {
		if(session.getAttribute("user") == null) {
    		return "redirect:/";
    	}
    	JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
    	if(!memberVO.getRole().equals("ROLE_ADMIN")) {
    		return "redirect:/";
    	}
    	// 1. 회원수 및 회원목록 한 5개 정도만?
    	PagingVO<JungMemberVO> userpv = jungMemberService.getUsers(new CommonVO());
    	List<JungMemberVO> memberList = userpv.getList().stream().limit(10).toList();
    	// 2. 최근 게시물 한 10개 정도만?
    	PagingVO<JungBoardVO> pv = jungBoardService.selectList(new CommonVO()); // 10개 리스트
    	List<JungBoardVO> boardList = pv.getList();
    	
    	model.addAttribute("name",memberVO.getName());
    	model.addAttribute("users", memberList);
    	model.addAttribute("boards", boardList);
    	
		return "admin/index"; // 관리자 메인페이지
	}
	
	// 회원관리페이지
	@GetMapping(value = "/userManagement")
	public String userManagement(@ModelAttribute(value = "cv") CommonVO cv, HttpSession session, Model model) {
		if(session.getAttribute("user") == null) {
    		return "redirect:/";
    	}
    	JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
    	if(!memberVO.getRole().equals("ROLE_ADMIN")) {
    		return "redirect:/";
    	}
		PagingVO<JungMemberVO> pv = jungMemberService.getUsers(cv);
		model.addAttribute("name",memberVO.getName());
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		return "admin/userManagement"; // 관리자 메인페이지
	}
	
	
	// 누구에게 메일을 전송할지 고르는 곳
	@GetMapping(value = "/mailToUser")
	public String mailToUser(HttpSession session, Model model) {
		if(session.getAttribute("user") == null) {
    		return "redirect:/";
    	}
    	JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
    	if(!memberVO.getRole().equals("ROLE_ADMIN")) {
    		return "redirect:/";
    	}
    	
		model.addAttribute("name",memberVO.getName());
		return "admin/mailToUser"; // 관리자 메일선택창
	}
	
	
	@PostMapping(value = "/pagedUsers")
	@ResponseBody()
	public List<JungMemberVO> pagingUser(@RequestBody Map<String, Object> map){
		CommonVO cv = new CommonVO();
		cv.setP((Integer) map.get("currentPage"));
		cv.setSearch((String) map.get("search"));
		cv.setS(20);
		cv.setB(5);
		PagingVO<JungMemberVO> pv = jungMemberService.getUsers(cv);
		return pv.getList();
	}
	
	
	@PostMapping(value = "/getTotalCount")
	@ResponseBody
	public int getTotalCount(@RequestBody Map<String, Object> map){
		CommonVO cv = new CommonVO();
		cv.setSearch((String) map.get("search"));
		cv.setS(20);
		cv.setB(5);
		PagingVO<JungMemberVO> pv = jungMemberService.getUsers(cv);
		return pv.getTotalCount();
	}
	
	
	// 누구에게가 정해진후 메일을 쓰는 곳
	@PostMapping(value = "/sendToUser")
	public String sendToUser(HttpSession session, Model model, @RequestParam("mailList") List<Integer> mailList) {
	    if(session.getAttribute("user") == null) {
	        return "redirect:/";
	    }
	    JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
	    if(!memberVO.getRole().equals("ROLE_ADMIN")) {
	        return "redirect:/";
	    }
	    model.addAttribute("name",memberVO.getName());
	    
	    Map<String, Object> map = new HashMap<>();
	    for(int i : mailList) {
	    	map.put(jungMemberService.selectByIdx(i).getUsername(), i);
	    }
	    model.addAttribute("userMap", map);
	    return "admin/mailSendToUser";
	}
	
	@Autowired
	private MailService mailService;
	
	@PostMapping(value = "/sendToUserOk")
	public String sendToUserOk(HttpSession session, Model model, @RequestParam("userIdx") List<Integer> userList, @RequestParam("title") String title, @RequestParam("subject") String subject) {
		if(session.getAttribute("user") == null) {
	        return "redirect:/";
	    }
	    JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
	    if(!memberVO.getRole().equals("ROLE_ADMIN")) {
	        return "redirect:/";
	    }
	    log.info("title : {}",title);
	    log.info("subject : {}", subject);
	    log.info("userList : {}", userList);
	    model.addAttribute("title", title);
	    model.addAttribute("subject", subject);
	    
	    Map<String, List<String>> mailResultMap = mailService.adminMailSend(userList, title, subject);
	    model.addAttribute("mailResultMap",mailResultMap);
	    return "admin/test";
	}
	
}
