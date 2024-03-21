package kr.ezen.jung.controller;

import java.io.File;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungFileBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.JungVideoService;
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.JungFileBoardVO;
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
	
	
	@GetMapping("/videoupload")
	public String video(Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		model.addAttribute("cv", cv);
		return "video/videoupload";
	}
	
	@RequestMapping(value = {"","/"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String fileboard(@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
		cv.setCategoryNum(5); //갤러리 번호 5번인데 일단 4번으로 함
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		log.info("pv => {}", pv);
		log.info("cv => {}", cv);
		JungVideoVO videoVO = new JungVideoVO();
		
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		
		return "video/videoboard";
	}
	
	
	@GetMapping("/videouploadOk")
	public String fileuploadOk(Model model) {
		return "redirect:/videoboard";
	}

	
	/*
	 * @Transactional
	 * 
	 * @PostMapping("/videouploadOk") public String comment(HttpSession
	 * session, @ModelAttribute(value = "videoVO") JungBoardVO boardVO,
	 * MultipartHttpServletRequest request) { // 1.board 저장 JungMemberVO memberVO =
	 * (JungMemberVO)session.getAttribute("user");
	 * boardVO.setRef(memberVO.getIdx()); log.debug("넘어온 값 {}",boardVO);
	 * jungBoardService.insert(boardVO);
	 * 
	 * 
	 * String uploadPath = request.getServletContext().getRealPath("./upload/");
	 * 
	 * // 파일생성 File file2 = new File(uploadPath); if (!file2.exists()) {
	 * file2.mkdirs(); } log.info("서버 실제 경로 : " + uploadPath); // 확인용 //
	 * -----------------------------------------------------------------------------
	 * --------- // 여러개 파일 받기 List<MultipartFile> list = request.getFiles("file");
	 * // form에 있는 name과 일치 String url = ""; String filepath = ""; try { if (list !=
	 * null && list.size() > 0) { // 받은 파일이 존재한다면 // 반복해서 받는다. for (MultipartFile
	 * file : list) { // 파일이 없으면 처리하지 않는다. if (file != null && file.getSize() > 0) {
	 * // 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다. String saveFileName =
	 * UUID.randomUUID() + "_" + file.getOriginalFilename(); // 파일 객체를 만들어 저장해 준다.
	 * File saveFile = new File(uploadPath, saveFileName); // 파일 복사
	 * FileCopyUtils.copy(file.getBytes(), saveFile);
	 * 
	 * url = file.getOriginalFilename(); // original filepath = saveFileName; //
	 * savefileName JungFileBoardVO fileBoardVO = new JungFileBoardVO();
	 * fileBoardVO.setUrl(url); fileBoardVO.setFilepath(filepath);
	 * fileBoardVO.setRef(boardVO.getIdx()); log.debug("넘어온 값 {}",fileBoardVO);
	 * jungFileBoardService.insert(fileBoardVO); } } } } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * return "redirect:/fileboard"; }
	 */
	
	
	// 다운로드가 가능하게 하자
	/*
	 * @GetMapping(value = "/download") public ModelAndView download(@RequestParam
	 * HashMap<String, Object> params, ModelAndView mv) {
	 * log.debug("download 호출!!!!!"); String oFileName = (String) params.get("of");
	 * String sFileName = (String) params.get("sf");
	 * mv.setViewName("fileDownloadView"); mv.addObject("of", oFileName);
	 * mv.addObject("sf", sFileName); return mv; }
	 */
	
	@GetMapping(value = "/blog/{idx}")
	public String as (@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		boardVO.setMember(jungMemberService.selectByIdx(boardVO.getRef()));
		
		boardVO.setCommentCount(jungCommentService.selectCountByRef(boardVO.getIdx()));
	
		boardVO.setCountHeart(jungBoardService.countHeart(idx));
		
		boardVO.setVideoVO(jungVideoService.selectvideoByRef(boardVO.getIdx()));
		
		JungMemberVO memberVO = (JungMemberVO) request.getSession().getAttribute("user");
		
		if(request.getSession().getAttribute("user")!=null) {
			int heart = jungBoardService.select(((JungMemberVO)request.getSession().getAttribute("user")).getIdx(), idx);
			model.addAttribute("heart", heart);
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
				PopularVO p = new PopularVO();
				p.setBoardRef(idx);
				p.setUserRef(memberVO.getIdx());
				p.setInteraction(1);
				popularService.insertPopular(p);
				log.info("무야:{}", p);
				response.addCookie(oldCookie);
			}
		}	else {
				jungBoardService.updateReadCount(idx);
				Cookie newCookie = new Cookie("blog", "[" + idx + "]");
				newCookie.setPath("/");
				newCookie.setMaxAge(60);
				response.addCookie(newCookie);
		}
		
		return "video/video"; // 임시값
	}
	
	@PostMapping(value = "/commentupload")
	public String comment(HttpSession session, @ModelAttribute(value = "commentVO") JungCommentVO commentVO, @RequestParam(value = "boardidx") int boardidx) {
		log.debug("값 : {}", commentVO);
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		commentVO.setUserRef(memberVO.getIdx());
		commentVO.setBoardRef(boardidx);
		PopularVO p = new PopularVO();
		p.setBoardRef(boardidx);
		p.setUserRef(memberVO.getIdx());
		p.setInteraction(2);
		popularService.insertPopular(p);
		jungCommentService.insert(commentVO);
		log.info("{} 님이 {}글에 댓글을 남김",memberVO.getNickName(), boardidx);
		return "redirect:/video/" + boardidx;
	}
	
	/**
	 * boardIdx와 currentPage를 넘기면 boardIdx의 댓글중 currentPage의 댓글을 보내주는 api
	 * @param map
	 * @return List<JungCommentVO>
	 */
	@PostMapping(value = "/comments")
	@ResponseBody
	public List<JungCommentVO> getComments(@RequestBody HashMap<String, Integer> map){
		log.info("map : {}", map);
		PagingVO<JungCommentVO> pv = null;
		int boardIdx = map.get("boardIdx");
		CommonVO cv = new CommonVO();
		cv.setP(map.get("currentPage"));
		pv = jungCommentService.selectByRef(boardIdx, cv);
		return pv.getList();
	}
	@PostMapping(value = "/commentsTotalCount")
	@ResponseBody
	public int getCommentsTotalCount(@RequestBody HashMap<String, Integer> map){
		int result = 0;
		int boardIdx = map.get("boardIdx");
		result = jungCommentService.selectCountByRef(boardIdx);
		return result;
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
		
		
        String uploadPath = request.getServletContext().getRealPath("./upload/");

        // 파일생성
        File file2 = new File(uploadPath);
        if (!file2.exists()) {
           file2.mkdirs();
        }
        log.info("서버 실제 경로 : " + uploadPath); // 확인용
        // --------------------------------------------------------------------------------------
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
	
	
	// 다운로드가 가능하게 하자
	@GetMapping(value = "/download")
	public ModelAndView download(@RequestParam HashMap<String, Object> params, ModelAndView mv) {
		log.debug("download 호출!!!!!");
		String oFileName = (String) params.get("of");
		String sFileName = (String) params.get("sf");
		mv.setViewName("fileDownloadView");
		mv.addObject("of", oFileName);
		mv.addObject("sf", sFileName);
		return mv;
	}
	
}
