package kr.ezen.jung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungMemberService;
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
    	
		return "adminMainPage"; // 관리자 메인페이지
	}
	
	// 회원관리페이지
	@GetMapping(value = "userManagement")
	public String userManagement(@ModelAttribute(value = "cv") CommonVO cv, HttpSession session, Model model) {
		log.info("cv: {}",cv);
		PagingVO<JungMemberVO> pv = jungMemberService.getUsers(cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		return "adminUserManagement"; // 관리자 메인페이지
	}
	
}
