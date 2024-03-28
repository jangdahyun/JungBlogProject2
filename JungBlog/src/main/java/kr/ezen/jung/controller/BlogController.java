package kr.ezen.jung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/blog")
public class BlogController {
	
	@Autowired
	private JungBoardService jungBoardService;
	@Autowired
	private PopularService popularService;
	
	/** 블로그 메인페이지 */
	@GetMapping(value = {"","/"})
	public String blogMainPage(Model model, @ModelAttribute(value = "cv") CommonVO cv, @RequestParam(value = "error", required = false) String error) {
		cv.setS(20);
		cv.setCategoryNum(1);
		log.info("blogHomePage 실행 {}", cv);
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		
		if(error != null && error.length() != 0) {
			model.addAttribute("error", error);
		}
		return "blog/blogMainPage";
	}
	
	
	/** 블로그 글 1개 보기 */
	@GetMapping(value = "/view/{idx}")
	public String blogView(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "idx") int idx) {
		log.info("blogView 실행 idx = {}", idx);
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		
		JungMemberVO memberVO = (JungMemberVO) request.getSession().getAttribute("user");
		if(boardVO == null) { // 해당글이 존재하는지 or 해당글이 blog 가 맞는지 확인
			return "redirect:/blog?error=notFound";
		}
		if(memberVO != null && memberVO.getRole().equals("ROLE_ADMIN")) {
			
		} else if(boardVO.getDeleted() == 1) {
			return "redirect:/blog?error=notFound";
		}
		if(boardVO.getCategoryNum() != 1){
			return "redirect:/blog?error=notFound";
		}
		if(session.getAttribute("user") != null) { // 로그인 했으면
			int heart = jungBoardService.select(((JungMemberVO) session.getAttribute("user")).getIdx(), idx); // 좋아요 했으면 1 아니면 0
			memberVO = (JungMemberVO) request.getSession().getAttribute("user");
			model.addAttribute("heart",heart);
			model.addAttribute("currentUser", memberVO.getIdx());
		}
		
		// 조회수 증가여부 체크
		Cookie oldCookie = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("blog")) {
					oldCookie = cookie;
					break;
				}
			}
		}
		if(oldCookie != null) {
			if(!oldCookie.getValue().contains("["+idx+"]")) {
				jungBoardService.updateReadCount(idx);
				oldCookie.setValue(oldCookie.getValue() + "_[" + idx + "]");
				oldCookie.setPath("/");
                oldCookie.setMaxAge(60);
                if(memberVO != null) { // 로그인을 한 상태라면 유저가 조회했음을 저장한다.
                	PopularVO p = new PopularVO();
                	p.setBoardRef(idx);
                	p.setUserRef(memberVO.getIdx());
                	p.setInteraction(1);
                	popularService.insertPopular(p);                	
                }
                response.addCookie(oldCookie);
			}
		} else {
            jungBoardService.updateReadCount(idx);
            Cookie newCookie = new Cookie("blog","[" + idx + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60);
            if(memberVO != null) { // 로그인을 한 상태라면 유저가 조회했음을 저장한다.
            	PopularVO p = new PopularVO();
            	p.setBoardRef(idx);
            	p.setUserRef(memberVO.getIdx());
            	p.setInteraction(1);
            	popularService.insertPopular(p);                	
            }
            response.addCookie(newCookie);
        }
		log.info("값:{}",boardVO);
		model.addAttribute("board", boardVO);
		return "blog/blogView";
	}
	
	@GetMapping(value = "/view/my/{idx}")
	public String myhide(@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/blog?error=notFound";
		}
		if(boardVO.getCategoryNum() != 1) {
			return "redirect:/blog?error=notFound";
		}
		// 좋아요가 되있는지 찾기위해 게시글번호와 회원번호를 보냄.
		JungMemberVO memberVO = null;
		if(request.getSession().getAttribute("user") != null) { // 로그인 했으면
			int heart = jungBoardService.select(((JungMemberVO) request.getSession().getAttribute("user")).getIdx(), idx); // 좋아요 했으면 1 아니면 0
			memberVO = (JungMemberVO) request.getSession().getAttribute("user");
			model.addAttribute("currentUser", memberVO.getIdx());
			model.addAttribute("heart",heart);
		}	
		// 찾은 정보를 heart로 담아서 보냄
		model.addAttribute("board",boardVO);
		return "blog/myblogView"; // 임시값 blog.html
	}
	
	
	/** 글 쓰기 페이지 */
	@PostMapping("/upload")
	public String blogUpload(HttpSession session, @ModelAttribute(value = "cv") CommonVO cv,Model model) {
		if(session.getAttribute("user") == null) { // 로그인 하지 않았으면
			return "redirect:/member/login";
		}
		model.addAttribute("categoryList",jungBoardService.findCategoryList());
		return "blog/blogUpload";
	}
	
	/** 글 수정 페이지 */
	@GetMapping("/update/{idx}")
	public String blogUpdate(Model model,HttpSession session, @PathVariable(value = "idx") int idx) {
		JungMemberVO memberVO = null;
		if(session.getAttribute("user") == null) { // 로그인 하지 않았으면
			return "redirect:/blog";
		} else {
			memberVO = (JungMemberVO) session.getAttribute("user");
		}
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/blog";
		} else if(boardVO.getCategoryNum() != 1) {
			return "redirect:/blog";
		} else if(boardVO.getRef() != memberVO.getIdx()) {
			return "redirect:/blog";
		}
		model.addAttribute("board", boardVO);
		return "blog/blogUpdate";
	}
	
	/** 글 업로드 */
	@PostMapping("/uploadOk")
	public String blogUploadOk(HttpSession session, @ModelAttribute JungBoardVO boardVO) {
		JungMemberVO memberVO = null;
		if(session.getAttribute("user") == null) { // 로그인 하지 않았으면
			return "redirect:/blog";
		} else {
			memberVO = (JungMemberVO) session.getAttribute("user");
		}
		boardVO.setRef(memberVO.getIdx());
		boardVO.setCategoryNum(1);
		jungBoardService.insert(boardVO);
		return "redirect:/blog";
	}
	
	/** 글 업데이트 */
	@PostMapping("/updateOk")
	public String blogUpdateOk(HttpSession session, @ModelAttribute JungBoardVO boardVO) {
		JungMemberVO memberVO = null;
		if(session.getAttribute("user") == null) { // 로그인 하지 않았으면
			return "redirect:/blog";
		} else {
			memberVO = (JungMemberVO) session.getAttribute("user");
		}
		if(memberVO.getIdx() != boardVO.getRef()) {
			return "redirect:/blog";
		}
		jungBoardService.update(boardVO);
		return "redirect:/blog/view/"+boardVO.getIdx();
	}
}
