package kr.ezen.jung.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.service.VisitService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/adm")
public class JungMemberManController {

	@Autowired
	private JungMemberService jungMemberService;
	@Autowired
	private JungBoardService jungBoardService;
	@Autowired
	private MailService mailService;
	@Autowired
	private VisitService visitService;
	@Autowired
	private PopularService popularService;
	
	
	//=========================================================================================================================================
	// 관리자 메인페이지
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
    	
		return "admin/admin"; // 관리자 메인페이지
	}
	//=========================================================================================================================================
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
	
	//=========================================================================================================================================
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
	
	
	
	@PostMapping(value = "/sendToUserOk")
	public String sendToUserOk(HttpSession session, Model model, @RequestParam("userIdx") List<Integer> userList, @RequestParam("title") String title, @RequestParam("subject") String subject) {
		if(session.getAttribute("user") == null) {
	        return "redirect:/";
	    }
	    JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
	    if(!memberVO.getRole().equals("ROLE_ADMIN")) {
	        return "redirect:/";
	    }
		model.addAttribute("name",memberVO.getName());
	    log.info("title : {}",title);
	    log.info("subject : {}", subject);
	    log.info("userList : {}", userList);
	    model.addAttribute("title", title);
	    model.addAttribute("subject", subject);
	    
	    Map<String, List<JungMemberVO>> mailResultMap = mailService.adminMailSend(userList, title, subject);
	    model.addAttribute("mailResultMap", mailResultMap);
	    return "admin/mailToUserResult";
	}
	
	//=========================================================================================================================================
	/**
	 * 회원 권한 변경 페이지
	 * @param session
	 * @param model
	 * @param cv
	 * @return
	 */
	@GetMapping(value = "/user-roles")
	public String userRoles(HttpSession session, Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		if(session.getAttribute("user") == null) {
	        return "redirect:/";
	    }
	    JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
	    if(!memberVO.getRole().equals("ROLE_ADMIN")) {
	        return "redirect:/";
	    }
	    model.addAttribute("name", memberVO.getName());
	    cv.setS(20);
	    log.info("userRoles 실행 cv: {}", cv);
	    PagingVO<JungMemberVO> pv = jungMemberService.getUsers(cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		return "admin/userRoles";
	}
	
	
	@PostMapping(value = "/updateUserRole")
	public String updateUserRole(HttpSession session, @ModelAttribute(value = "cv") CommonVO cv, @RequestParam(value = "role") int role) {
		log.info("updateUserRole 실행 => cv:{}, role:{}", cv, role);
		JungMemberVO memberVO = new JungMemberVO();
		memberVO.setIdx(cv.getIdx());
		if(role == 0) {
			memberVO.setRole("ROLE_USER");
		} else {
			memberVO.setRole("ROLE_ADMIN");
		}
		log.info("memberVO: {}", memberVO);
		jungMemberService.updateRole(memberVO);
		return "redirect:/adm/user-roles?p="+cv.getP()+"&search="+cv.getSearch();
	}
	
	@GetMapping(value = "/userTrendAnalysis")
	public String userTrendAnalysis(HttpSession session, Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		log.info("bestPost실행 cv: {}",cv);
		if(session.getAttribute("user") == null) {
	        return "redirect:/";
	    }
	    JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
	    if(!memberVO.getRole().equals("ROLE_ADMIN")) {
	        return "redirect:/";
	    }
	    model.addAttribute("name", memberVO.getName());
		cv.setS(30);
		PagingVO<PopularVO> pv = popularService.getUserTrendAnalysis(cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		return "admin/userTrendAnalysis";
	}
	
	//=========================================================================================================================================
	// 인기게시물
	@GetMapping(value = "/bestPost")
	public String bestPost(HttpSession session, Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		
		return "admin/bestPost";
	}
	
	
	//=========================================================================================================================================
	// 방문객 정보를 넘기는 주소
	@PostMapping(value = "/visitorData")
	@ResponseBody
	public Map<String, Object> visitorData(){
		Map<String, Object> map = new HashMap<>();
		// 1. 현재접속자수
		map.put("activeSession", VisitService.getActiveSessionCount());
		// 2. 지금까지 총 방문자 수
		map.put("totalCount", visitService.getTotalVisitorCount());
		
		// 현재로 부터 4일전 3일전 2일전 1일전 오늘의 방문자수를 가지는 리스트
		List<Integer> countList = new ArrayList<>();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date()); // 오늘날짜 새팅
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.add(Calendar.DATE, -6);
		for (int i = 0; i < 7; i++) {
			String strDate = sdf.format(cal.getTime());
			int visitCount = visitService.getDailyVisitorCount(strDate);
			countList.add(visitCount);
			cal.add(Calendar.DATE, +1);
		}
		map.put("countList", countList);
		return map;
	}
}
