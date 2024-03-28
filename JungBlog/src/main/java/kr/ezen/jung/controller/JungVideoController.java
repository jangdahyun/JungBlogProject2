package kr.ezen.jung.controller;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.JungVideoService;
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.JungVideoVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequestMapping("/videoboard")
@Controller
@Slf4j
public class JungVideoController {
	
	
	@Autowired
	private JungBoardService jungBoardService;
	
	@Autowired
	private JungVideoService jungVideoService;
	
	@Autowired
	private JungCommentService jungCommentService;
	
	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private PopularService popularService;
	
	@RequestMapping(value = {"/videoupload"}, method = {RequestMethod.GET, RequestMethod.POST})
	public String video(Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		model.addAttribute("cv", cv);
		return "video/videoupload";
	}
	
	@RequestMapping(value = {"","/"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String fileboard(@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
		cv.setCategoryNum(6); //갤러리 번호 5번인데 일단 4번으로 함
		cv.setS(12);
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		log.info("pv => {}", pv);
		log.info("cv => {}", cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		
		return "video/videoboard";
	}
	
	
	@GetMapping("/videouploadOk")
	public String fileuploadOk(Model model) {
		return "redirect:/videoboard";
	}

	
	
	@GetMapping(value = "/blog/{idx}")
	public String as (@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		
		JungMemberVO memberVO = (JungMemberVO) request.getSession().getAttribute("user");
		if(boardVO == null) { // 해당글이 존재하는지 or 해당글이 blog 가 맞는지 확인
			return "redirect:/videoboard?error=notFound";
		}
		if(memberVO != null && memberVO.getRole().equals("ROLE_ADMIN")) {
			
		} else if(boardVO.getDeleted() == 1) {
			return "redirect:/videoboard?error=notFound";
		}
		if(boardVO.getCategoryNum() != 6){
			return "redirect:/videoboard?error=notFound";
		}
		
		
		if(request.getSession().getAttribute("user")!=null) {
			int heart = jungBoardService.select(((JungMemberVO)request.getSession().getAttribute("user")).getIdx(), idx);
			model.addAttribute("heart", heart);
			model.addAttribute("currentUser", memberVO.getIdx());
		}
		
		model.addAttribute("board", boardVO);
		Cookie oldCookie = null;
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("blog")) {
					oldCookie = cookie;
				}
			}
		}
		if (oldCookie != null) {
			if (!oldCookie.getValue().contains("[" + idx + "]")) {
				jungBoardService.updateReadCount(idx);
				oldCookie.setValue(oldCookie.getValue() + "_[" + idx + "]");
				oldCookie.setPath("/");
				oldCookie.setMaxAge(60);
				
				if(memberVO != null) {
					PopularVO p = new PopularVO();
					p.setUserRef(memberVO.getIdx());
					p.setBoardRef(idx);
					p.setInteraction(1);
					popularService.insertPopular(p);
					log.info("무야:{}", p);
				}
				response.addCookie(oldCookie);
			}
		}	else {
				jungBoardService.updateReadCount(idx);
				Cookie newCookie = new Cookie("blog", "[" + idx + "]");
				newCookie.setPath("/");
				newCookie.setMaxAge(60);
				if(memberVO != null) {
					PopularVO p = new PopularVO();
					p.setUserRef(memberVO.getIdx());
					p.setBoardRef(idx);
					p.setInteraction(1);
					popularService.insertPopular(p);
					log.info("무야:{}", p);
				}
				response.addCookie(newCookie);
		}
		
		return "video/video"; // 임시값
	}
	
	@GetMapping(value = "/my/{idx}")
	public String asd (@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/videoboard?error=notFound";
		}
		if(boardVO.getCategoryNum() != 6) {
			return "redirect:/videoboard?error=notFound";
		}
		
		JungMemberVO memberVO = null;
		if(request.getSession().getAttribute("user") != null) { // 로그인 했으면
			int heart = jungBoardService.select(((JungMemberVO) request.getSession().getAttribute("user")).getIdx(), idx); // 좋아요 했으면 1 아니면 0
			memberVO = (JungMemberVO) request.getSession().getAttribute("user");
			model.addAttribute("currentUser", memberVO.getIdx());
			model.addAttribute("heart",heart);
		}	
		model.addAttribute("board", boardVO);
		return "video/myvideo";
	}
	
	@PostMapping(value = "/heartUpload")
	@ResponseBody
	public String heart(HttpSession session, @RequestBody HashMap<String, Integer>map) {
		HeartVO heartVO =new HeartVO();
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		heartVO.setUserRef(memberVO.getIdx());
		heartVO.setBoardRef(map.get("boardRef"));
		PopularVO p = new PopularVO();
		p.setBoardRef(map.get("boardRef"));
		p.setUserRef(memberVO.getIdx());
		p.setInteraction(3);
		popularService.insertPopular(p);
		int result = jungBoardService.insertHeart(heartVO);
		log.debug("{}번 유저가 {}번 글에 좋아요", memberVO.getIdx(), map.get("boardRef"));
		return result+"";
	}
	
	@PostMapping(value = "/heartDelete")
	@ResponseBody
	public String heartdelet(HttpSession session,@RequestBody HashMap<String, Integer>map) {
		HeartVO heartVO =new HeartVO();
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		heartVO.setUserRef(memberVO.getIdx());
		heartVO.setBoardRef(map.get("boardRef"));
		PopularVO p = new PopularVO();
		p.setBoardRef(map.get("boardRef"));
		p.setUserRef(memberVO.getIdx());
		p.setInteraction(4);
		popularService.insertPopular(p);
		int result = jungBoardService.deleteHeart(heartVO);
		log.debug("{}번 유저가 {}번 글에 좋아요취소", memberVO.getIdx(), map.get("boardRef"));
		return result+"";
	}

	@Transactional
	@PostMapping("/videouploadOk")
	public String comment(HttpSession session, @ModelAttribute(value = "videoVO") JungBoardVO boardVO, MultipartHttpServletRequest request, Model model) {
		// 1.board 저장
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		boardVO.setRef(memberVO.getIdx());
		log.debug("넘어온 값 {}",boardVO);
		jungBoardService.insert(boardVO);
		log.debug("넘어온 값3 {}",boardVO);
	
        // 여러개 파일 받기
        String youtube = request.getParameter("youtube"); // form에 있는 name과 일치
        try {
           if (youtube != null && youtube.length() > 0) { // 받은 파일이 존재한다면
	    	    // 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다.
				String videourl = UUID.randomUUID() + "_" + youtube;
	    	    // 파일 객체를 만들어 저장해 준다.
				JungVideoVO videoVO = new JungVideoVO();
				videoVO.setVideourl(videourl);
				videoVO.setYoutube(youtube);
				videoVO.setRef(boardVO.getIdx());
				log.debug("넘어온 값2 {}",videoVO);
				jungVideoService.insert(videoVO);
                 }
              
        
        } catch (Exception e) {
           e.printStackTrace();
        }
		   
		return "redirect:/videoboard";
	}
	
	
	@Transactional
	@PostMapping("/videoupdateOk")
	public String videoupdateOk(HttpSession session, @ModelAttribute(value = "boardVO") JungBoardVO boardVO, MultipartHttpServletRequest request, Model model) {
		jungVideoService.deleteByRef(boardVO.getIdx());
		jungBoardService.update(boardVO);
		
		// 여러개 파일 받기
		String youtube = request.getParameter("youtube"); // form에 있는 name과 일치
		try {
			if (youtube != null && youtube.length() > 0) { // 받은 파일이 존재한다면
				// 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다.
				String videourl = UUID.randomUUID() + "_" + youtube;
				// 파일 객체를 만들어 저장해 준다.
				JungVideoVO videoVO = new JungVideoVO();
				videoVO.setVideourl(videourl);
				videoVO.setYoutube(youtube);
				videoVO.setRef(boardVO.getIdx());
				log.debug("넘어온 값2 {}",videoVO);
				jungVideoService.insert(videoVO);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/videoboard";
	}
	
	@GetMapping("/videoupdateOk")
	public String videoupdateOk(Model model) {
		return "redirect:/";
	}
	
	// 게시글 수정 처리
	@GetMapping(value = "/videoupdate/{boardIdx}")
	public String updateBoard(@PathVariable("boardIdx") int boardIdx,Model model) {
		JungBoardVO updatedBoard=jungBoardService.selectByIdx(boardIdx);
		model.addAttribute("board",updatedBoard);
		log.debug("뭐냐고:{}",model);
		return "/video/videoupdate"; // 수정된 게시글로 리다이렉트
	}

}
