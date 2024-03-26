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

import jakarta.servlet.http.HttpServletRequest;
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
		cv.setS(3);
		PagingVO<JungBoardVO> pvall = jungBoardService.selectList(cv);
		cv.setCategoryNum(1);
		PagingVO<JungBoardVO> pvblog = jungBoardService.selectList(cv);
		cv.setCategoryNum(2);
		PagingVO<JungBoardVO> pvfileboard = jungBoardService.selectList(cv);
		cv.setCategoryNum(4);
		PagingVO<JungBoardVO> pvgallery = jungBoardService.selectList(cv);
		cv.setCategoryNum(6);
		PagingVO<JungBoardVO> pvvideo = jungBoardService.selectList(cv);
		
		model.addAttribute("pvall", pvall);
		model.addAttribute("pvblog", pvblog);
		model.addAttribute("pvfileboard", pvfileboard);
		model.addAttribute("pvgallery", pvgallery);
		model.addAttribute("pvvideo", pvvideo);
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
		jungBoardService.show(boardVO.getIdx());
		String result = "1";
		return result;
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
	
	@DeleteMapping(value = "/blog/{boardIdx}")
	@ResponseBody
	public String deleteblog(HttpSession session, @PathVariable(value = "boardIdx") int boardIdx) {
		log.info("deleteblog({}) 실행", boardIdx);
		int result = jungBoardService.hide(boardIdx);
		return result+"";
	}
	
	@PutMapping(value = "/show2/{boardIdx}")
	@ResponseBody
	public String show2(HttpSession session, @PathVariable(value = "boardIdx") int boardIdx) {
		log.info("deleteblog({}) 실행", boardIdx);
		int result = jungBoardService.show(boardIdx);
		return result+"";
	}
	
	@PostMapping(value = "/commentupload")
	public String comment(HttpSession session, @ModelAttribute(value = "commentVO") JungCommentVO commentVO, @RequestParam(value = "boardidx") int boardidx,@RequestParam(value = "categoryNum") int categoryNum) {
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
		String path="";
		if (categoryNum == 1) {
			path="/blog/view/";
		}
		else if(categoryNum==2){
			path="/fileboard/blog/";
		}else if(categoryNum==4) {
			path="/gallery/";
		}
		return "redirect:" + path + boardidx;
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
	
	@PostMapping("/getScrollItem")
	@ResponseBody
	public List<JungBoardVO> getScrollItem(@RequestBody Map<String, String> map){
		log.info("getScrollItem : {}", map);
		List<JungBoardVO> result = jungBoardService.selectScrollBoard(Integer.parseInt(map.get("lastItemIdx")), Integer.parseInt(map.get("sizeOfPage")), Integer.parseInt(map.get("categoryNum")), map.get("search"));
		return result;
	}


	@GetMapping(value = "/notice")
	public String notice(Model model ,@ModelAttribute(value = "cv") CommonVO cv, @RequestParam(value = "error", required = false) String error) {
		cv.setS(10);
	    cv.setB(5);
	    cv.setCategoryNum(3);
	    PagingVO<JungBoardVO> noticeList = jungBoardService.selectList(cv);
	    model.addAttribute("pv", noticeList);
		return "notice";
	}
	@GetMapping(value = "/notice/detail/{idx}")
	public String notice(Model model , @PathVariable(value = "idx") int idx) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null || boardVO.getCategoryNum() != 3) {
			return "redirect:/notice?error=notFound";
		}
		model.addAttribute("board", boardVO);
		return "noticeDetail";
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
