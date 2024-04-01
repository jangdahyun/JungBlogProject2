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
import kr.ezen.jung.service.PopularService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.HeartVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import kr.ezen.jung.vo.PopularVO;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequestMapping("/fileboard")
@Controller
@Slf4j
public class JungFileBoardController {
	
	
	@Autowired
	private JungBoardService jungBoardService;
	
	@Autowired
	private JungFileBoardService jungFileBoardService;
	
	@Autowired
	private JungCommentService jungCommentService;
	
	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private PopularService popularService;
	
	
	@PostMapping("/fileupload")
	public String fileupload(Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		model.addAttribute("cv", cv);
		return "file/fileupload";
	}
	
	@RequestMapping(value = {"","/"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String fileboard(@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
		cv.setCategoryNum(2); //갤러리 번호 5번인데 일단 4번으로 함
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		
		return "file/fileboard";
	}
	
	
	@GetMapping("/fileuploadOk")
	public String fileuploadOk(Model model) {
		return "redirect:/fileboard";
	}

	
	@Transactional
	@PostMapping("/fileuploadOk")
	public String comment(HttpSession session, @ModelAttribute(value = "fileboardVO") JungBoardVO boardVO, MultipartHttpServletRequest request) {
		// 1.board 저장
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		boardVO.setRef(memberVO.getIdx());
		log.debug("넘어온 값 {}",boardVO);
		jungBoardService.insert(boardVO);
		
		
        String uploadPath = request.getServletContext().getRealPath("./upload/");

        // 파일생성
        File file2 = new File(uploadPath);
        if (!file2.exists()) {
           file2.mkdirs();
        }
        log.info("서버 실제 경로 : " + uploadPath); // 확인용
        // --------------------------------------------------------------------------------------
        // 여러개 파일 받기
        List<MultipartFile> list = request.getFiles("file"); // form에 있는 name과 일치
        String url = "";
        String filepath = "";
        try {
           if (list != null && list.size() > 0) { // 받은 파일이 존재한다면
              // 반복해서 받는다.
              for (MultipartFile file : list) {
                 // 파일이 없으면 처리하지 않는다.
                 if (file != null && file.getSize() > 0) {
                    // 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다.
                    String saveFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    // 파일 객체를 만들어 저장해 준다.
                    File saveFile = new File(uploadPath, saveFileName);
                    // 파일 복사
                    FileCopyUtils.copy(file.getBytes(), saveFile);
                    
                    url = file.getOriginalFilename();	// original
                    filepath = saveFileName;			// savefileName
                    JungFileBoardVO fileBoardVO = new JungFileBoardVO();
                    fileBoardVO.setUrl(url);
                    fileBoardVO.setFilepath(filepath);
                    fileBoardVO.setRef(boardVO.getIdx());
                    log.debug("넘어온 값 {}",fileBoardVO);
                    jungFileBoardService.insert(fileBoardVO);
                 }
              }
           }
        } catch (Exception e) {
           e.printStackTrace();
        }
		   
		return "redirect:/fileboard";
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
	
	@GetMapping(value = "/blog/{idx}")
	public String as (@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/fileboard?error=notFound";
		}
		JungMemberVO memberVO = (JungMemberVO) request.getSession().getAttribute("user");
		if(memberVO != null && memberVO.getRole().equals("ROLE_ADMIN")) {
			
		} else if(boardVO.getDeleted() == 1) {
			return "redirect:/fileboard?error=notFound";
		}
		if(boardVO.getCategoryNum() != 2) {
			return "redirect:/fileboard?error=notFound";
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
					p.setBoardRef(idx);
					p.setUserRef(memberVO.getIdx());
					p.setInteraction(1);
					popularService.insertPopular(p);					
				}
				response.addCookie(oldCookie);
			}
		}	else {
				jungBoardService.updateReadCount(idx);
				Cookie newCookie = new Cookie("blog", "[" + idx + "]");
				newCookie.setPath("/");
				newCookie.setMaxAge(60);
				response.addCookie(newCookie);
		}
		
		return "file/file"; // 임시값
	}
	
	@GetMapping(value = "/my/{idx}")
	public String myhide(@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/fileboard?error=notFound";
		}
		if(boardVO.getCategoryNum() != 2) {
			return "redirect:/fileboard?error=notFound";
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
		log.debug("삭제{}",boardVO);
		return "file/myfile"; // 임시값 blog.html
	}
	
	
	// 게시글 수정 처리
	@GetMapping(value = "/fileupdate/{boardIdx}")
	public String updateBoard(@PathVariable("boardIdx") int boardIdx,Model model) {
		JungBoardVO updatedBoard=jungBoardService.selectByIdx(boardIdx);
		model.addAttribute("board",updatedBoard);
		log.debug("뭐냐고:{}",model);
		return "/file/fileupdate"; // 수정된 게시글로 리다이렉트
	}
	
	@GetMapping("/fileupdateOk")
	public String fileupdateOk(Model model) {
		return "redirect:/";
	}
	
	
	
	//MultipartHttpServletRequest 파일을 받을 수 있는.
	@Transactional //한꺼번에 저장하기 위해 하나가 에러가되면 모든게 막히게
	@PostMapping("/fileupdateOk")
	public String fileupdateOk(HttpSession session, @ModelAttribute(value = "boardVO") JungBoardVO boardVO, MultipartHttpServletRequest request) {
		// 1.board 저장
		log.debug("왜요:{}", boardVO);
		jungFileBoardService.deleteByRef(boardVO.getIdx()); // 파일지우고
		log.debug("왜요 2");
		jungBoardService.update(boardVO); // 업뎃하고
		log.debug("왜요 3");
		String uploadPath = request.getServletContext().getRealPath("./upload/");
		
		 File file2 = new File(uploadPath);
	     if (!file2.exists()) {
	        file2.mkdirs();
	     }
	    log.info("서버 실제 경로 : " + uploadPath); // 확인용
	    
	    // 여러개 파일 받기
        List<MultipartFile> list = request.getFiles("file"); // form에 있는 name과 일치
        String url = "";
        String filepath = "";
        try {
           if (list != null && list.size() > 0) { // 받은 파일이 존재한다면
              // 반복해서 받는다.
              for (MultipartFile file : list) {
                 // 파일이 없으면 처리하지 않는다.
                 if (file != null && file.getSize() > 0) {
                    // 저장파일의 이름 중복을 피하기 위해 저장파일이름을 유일하게 만들어 준다.
                    String saveFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                    // 파일 객체를 만들어 저장해 준다.
                    File saveFile = new File(uploadPath, saveFileName);
                    // 파일 복사
                    FileCopyUtils.copy(file.getBytes(), saveFile);
                    
                    url = file.getOriginalFilename();	// original
                    filepath = saveFileName;			// savefileName
                    JungFileBoardVO fileBoardVO = new JungFileBoardVO();
                    
                    fileBoardVO.setUrl(url);
                    fileBoardVO.setFilepath(filepath);
                    fileBoardVO.setRef(boardVO.getIdx());
                    log.debug("뭐냐:{}",fileBoardVO);
                    jungFileBoardService.insert(fileBoardVO);
                   
                 }
              }
           }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "redirect:/fileboard/blog/"+boardVO.getIdx();
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
		return "redirect:/file/" + boardidx;
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
	
}
