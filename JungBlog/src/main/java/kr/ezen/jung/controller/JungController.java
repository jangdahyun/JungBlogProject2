package kr.ezen.jung.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungFileBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Controller
@Slf4j
public class JungController {

	@Autowired
	private JungBoardService jungBoardService;
	
	@Autowired
	private JungCommentService jungCommentService;
	
	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private JungFileBoardService jungFileBoardService;
	
	@Autowired
	private PopularService popularService;

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String list(@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
		log.info("cv: {}",cv);
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		model.addAttribute("categoryList",jungBoardService.findCategoryList());
		return "index";
	}
	
	/**
	 * 게시글을 삭제하는 주소
	 * @return 게시글 삭제후 가야 될 주소 리턴
	 */
	@DeleteMapping(value = "/delete/{idx}")
	@ResponseBody
	@Transactional
	public String deleteBoard(@PathVariable(value = "idx") int idx, HttpSession session) {
		if(session.getAttribute("user") == null) {			// 로그인 했는지 확인
			return "0";
		}
		JungMemberVO sessionUser = (JungMemberVO) session.getAttribute("user");
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {								// 있는 게시글인지 확인
			return "0";
		}
		if(boardVO.getRef() != sessionUser.getIdx()){		// 맞는 유저가 게시글을 삭제한 것인지 확인
			return "0";
		}
		jungFileBoardService.deleteByRef(boardVO.getIdx());	// 게시글에 해당하는 파일 삭제
		jungBoardService.delete(boardVO.getIdx());			// 게시글 삭제
		String result = "1";
		// 머지하고 만질것
		/*
		String result = "0";
		int result1 = jungFileBoardService.deleteByRef(boardVO.getIdx());
		int result2 = jungBoardService.delete(boardVO.getIdx());
		if(result1 + result2 == 2) {
			result = "1";
		}
		*/
		return result;
	}
	
	/**
	 * 게시글 숨김 취소 페이지
	 * @return 게시글 숨김 취소 후 가야 될 주소 리턴
	 */
	@PutMapping(value = "/show/{idx}")
	@ResponseBody
	@Transactional
	public String show(@PathVariable(value = "idx") int idx, HttpSession session) {
		if(session.getAttribute("user") == null) {			// 로그인 했는지 확인
			return "0";
		}
		JungMemberVO sessionUser = (JungMemberVO) session.getAttribute("user");
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {								// 있는 게시글인지 확인
			return "0";
		}
		if(boardVO.getRef() != sessionUser.getIdx()){		// 맞는 유저가 게시글을 삭제한 것인지 확인
			return "0";
		}
		int result = jungBoardService.show(boardVO.getIdx());
		return result + "";
	}
	
	/**
	 * 게시글 숨김 페이지
	 * @return 게시글 숨김 취소 후 가야 될 주소 리턴
	 */
	@PutMapping(value = "/hide/{idx}")
	@ResponseBody
	@Transactional
	public String hide(@PathVariable(value = "idx") int idx, HttpSession session) {
		if(session.getAttribute("user") == null) {			// 로그인 했는지 확인
			return "0";
		}
		JungMemberVO sessionUser = (JungMemberVO) session.getAttribute("user");
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {								// 있는 게시글인지 확인
			return "0";
		}
		if(boardVO.getRef() != sessionUser.getIdx()){		// 맞는 유저가 게시글을 삭제한 것인지 확인
			return "0";
		}
		int result = jungBoardService.hide(boardVO.getIdx());
		return result + "";
	}
	
	//카테고리 블로그 전체 보기
//	@RequestMapping(value = "/{categoryNum}", method = { RequestMethod.GET, RequestMethod.POST })
//	public String list2(@PathVariable(value = "categoryNum") int categoryNum,@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
//		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
//		List<JungBoardVO> list = pv.getList();
//		list.forEach((board)->{
//			board.setCountHeart(jungBoardService.countHeart(board.getIdx()));
//		});
//		pv.setList(list);
//		log.info("{}",pv.getTotalPage());
//		log.info("{}",pv.getStartPage());
//		log.info("{}",pv.getEndPage());
//		model.addAttribute("pv", jungBoardService.selectList(cv));
//		
//		return "index";
//	}
	
	
	@GetMapping(value = "/blog/{idx}")
	public String as(@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		boardVO.setMember(jungMemberService.selectByIdx(boardVO.getRef()));
		
		boardVO.setCommentCount(jungCommentService.selectCountByRef(boardVO.getIdx()));
		
		boardVO.setCountHeart(jungBoardService.countHeart(idx));
		
		boardVO.setFileboardVO(jungFileBoardService.selectfileByRef(boardVO.getIdx()));
		
		JungMemberVO memberVO = (JungMemberVO) request.getSession().getAttribute("user");
		
		// 좋아요가 되있는지 찾기위해 게시글번호와 회원번호를 보냄.
		if(request.getSession().getAttribute("user")!=null) {
			int heart = jungBoardService.select(((JungMemberVO)request.getSession().getAttribute("user")).getIdx(), idx); 			
			model.addAttribute("heart",heart);		
		}
				
		// 찾은 정보를 heart로 담아서 보냄
		model.addAttribute("board",boardVO);
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
        		log.info("무야:{}",p);
                response.addCookie(oldCookie);
            }
        } else {
            jungBoardService.updateReadCount(idx);
            Cookie newCookie = new Cookie("blog","[" + idx + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60);
            response.addCookie(newCookie);
        }
    
		//jungBoardService.updateReadCount(idx);
		return "blog"; // 임시값 blog.html
	}
	
	@DeleteMapping(value = "/blog/{boardIdx}")
	@ResponseBody
	public String deleteblog(HttpSession session, @PathVariable(value = "boardIdx") int boardIdx) {
		log.info("deleteblog({}) 실행", boardIdx);
		int result = jungBoardService.hide(boardIdx);
		return result+"";
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
		return "redirect:/blog/" + boardidx;
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
	

//	@GetMapping(value = { "/login" })
//	public String login(@RequestParam(value = "error", required = false) String error,
//			@RequestParam(value = "logout", required = false) String logout, Model model) {
//		if (error != null)
//			model.addAttribute("error", "error");
//		if (logout != null)
//			model.addAttribute("logout", "logout");
//		return "login";
//	}
//	//회원가입 폼
//	@GetMapping(value = {"/join"})
//	public String join(HttpSession session) {
//		// 현재 로그인이 되어있는데 회원가입을 하려고 한다. 막아야 한다.
//		if(session.getAttribute("user")!=null) {
//			session.removeAttribute("user");// 세션에 회원정보만 지운다.
//			session.invalidate();// 세션자체를 끊고 다시 연결한다.
//			return "redirect:/";
//		}
//		return "join";
//	}
//	@GetMapping(value = "/test/userIdCheck", produces = "text/plain;charset=UTF-8")
//	@ResponseBody
//	public String userIdCheck(@RequestParam(value = "username")String username) {
//		return jungMemberService.selectByUsername(username)+"";
//	}
//	//회원가입 완료
//	@GetMapping("/joinok")
//	public String joinOkGet() {
//		return "redirect:/";
//	}
//	@PostMapping("/joinok")
//	public String joinOkPost(@ModelAttribute(value = "vo") JungMemberVO vo,Model model,@RequestParam(value = "bd")String bd ) {
//		// 내용 검증을 해줘야 한다.
//		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
//		Date date = null;
//		try {
//			date = formatter.parse(bd);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		vo.setBirthDate(date);
//		vo.setRole("ROLE_USER");
//		model.addAttribute("vo",vo);
////		memberService.insert(vo); // 저장
//		return "joinOk";
//	}
//	
//	@GetMapping(value = "/myblog/{idx}")
//	public String myblog(@PathVariable(value = "idx") int idx, Model model) {
//		JungMemberVO jungMemberVO=jungMemberService.selectByRef
//		
////		boardVO.setMember();
//		List<JungCommentVO> commentList = jungCommentService.selectByRef(idx);
//		
//		boardVO.setCommentList(commentList);
//		
//		model.addAttribute("board",boardVO);
//		return "myblog"; // 임시값 blog.html
//	}
	
	

	// 파일 업로드 처리 함
	@PostMapping("/ckeditor2")
	public String ckeditor2(Model model, @ModelAttribute(value = "cv") CommonVO cv ){
		log.info("cv : {}",cv);
		model.addAttribute("categoryList",jungBoardService.findCategoryList());
		return "ckeditor2";
	}

	@GetMapping("/ckeditorResult2")
	public String ckeditorResult2(Model model) {
		return "redirect:/ckeditor2";
	}

	@PostMapping("/ckeditorResult2")
	public String ckeditorResult2(@RequestParam Map<String, String> param, Model model,@ModelAttribute(value = "vo") JungBoardVO jungBoardVO,HttpServletRequest request) {
		// MultipartFile 이것이 파일을 알아서 받아준다.
		model.addAttribute("map", param);		
		JungMemberVO mvo = (JungMemberVO)request.getSession().getAttribute("user");
		jungBoardVO.setRef(mvo.getIdx());

		model.addAttribute("vo", jungBoardVO);
		jungBoardService.insert(jungBoardVO);
		return "redirect:/";
//		return "ckeditorResult2";
		
	}

	@PostMapping("/fileupload")
	@ResponseBody
	public Map<String, Object> fileUpload(HttpServletRequest request,
			@RequestPart(value = "upload", required = false) MultipartFile upload) {
		// 반드시 받는 이름이 "upload"이어야 한다.
		// json 데이터로 등록
		// {"uploaded" : 1, "fileName" : "test.jpg", "url" : "/img/test.jpg"}
		// 이런 형태로 리턴이 나가야함.
		// --------------------------------------------------------------------------------------
		// 서버의 업로드될 경로 확인
		String uploadPath = request.getServletContext().getRealPath("/ckeditor/");
		// 파일 객체 생성
		File file2 = new File(uploadPath);
		// 폴더가 없다면 폴더를 생성해준다.
		if (!file2.exists()) {
			file2.mkdirs();
		}
		log.info("서버 실제 경로 : " + uploadPath);
		// --------------------------------------------------------------------------------------
		String saveName = "";
		String saveFileName = "";

		if (upload != null && upload.getSize() > 0) { // 파일이 넘어왔다면
			try {
				// 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다.
				saveFileName = UUID.randomUUID() + "_" + upload.getOriginalFilename();
				// 파일 객체를 만들어 저장해 준다.
				File saveFile = new File(uploadPath, saveFileName);
				// 파일 복사
				FileCopyUtils.copy(upload.getBytes(), saveFile);
				saveName = request.getContextPath() + "/ckeditor/" + saveFileName;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * VO를 만들었다면 CkeditorResultVO vo = new CkeditorResultVO();
		 * vo.setFileName(saveFileName); vo.setUploaded(1); vo.setUrl(saveName);
		 * log.info("리턴값 : " + vo); return vo;
		 */
		// VO가 없다면 맵으로 받아 처리
		Map<String, Object> map = new HashMap<>();
		map.put("fileName", saveFileName);
		map.put("uploaded", 1);
		map.put("url", saveName);
		return map;
	}
	
	@GetMapping("/getScrollItem")
	@ResponseBody
	public List<JungBoardVO> getScrollItem(@RequestParam(value = "lastItemIdx") int lastItemIdx
											,@RequestParam(value = "sizeOfPage") int sizeOfPage
											,@RequestParam(value = "categoryNum") int categoryNum
											,@RequestParam(value = "search") String search){
		List<JungBoardVO> result = jungBoardService.selectScrollBoard(lastItemIdx, sizeOfPage, categoryNum, search);
		return result;
	}
	@GetMapping("/getScrollItem1")
	@ResponseBody
	public List<JungBoardVO> getScrollItem1(@RequestParam(value = "lastItemIdx") int lastItemIdx, @RequestBody CommonVO cv){
		List<JungBoardVO> result = jungBoardService.selectScrollBoard(lastItemIdx, cv.getSizeOfPage(), cv.getCategoryNum(), cv.getSearch());
		return result;
	}
	@GetMapping("/getScrollItem2")
	public String getScrollItem2(Model model,@RequestParam(value = "lastItemIdx") int lastItemIdx, @ModelAttribute CommonVO cv){
		List<JungBoardVO> result = jungBoardService.selectScrollBoard(lastItemIdx, cv.getSizeOfPage(), cv.getCategoryNum(), cv.getSearch());
		model.addAttribute("boards", result);
		return "test2";
	}
	
	
	


	//딱! 한번만 실행해야한다!

	@Autowired	
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	//@GetMapping("/dbinit") // 기존에 등록된 비번을 암호화 해서 변경한다. 1번만 실행하고 지워줘라~~~
	public String dbInit() {
		jdbcTemplate.update("update jung_member set password = ? where username = ?", passwordEncoder.encode("123456"),"admin");
		jdbcTemplate.update("update jung_member set password = ? where username = ?", passwordEncoder.encode("123456"),"master");
		jdbcTemplate.update("update jung_member set password = ? where username = ?", passwordEncoder.encode("123456"),"webmaster");
		jdbcTemplate.update("update jung_member set password = ? where username = ?", passwordEncoder.encode("123456"),"root");
		jdbcTemplate.update("update jung_member set password = ? where username = ?", passwordEncoder.encode("123456"),"dba");
		return "redirect:/";
	}
}
