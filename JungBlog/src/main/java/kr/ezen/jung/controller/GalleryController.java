package kr.ezen.jung.controller;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungFileBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Controller
@RequestMapping("/gallery")
@Slf4j
public class GalleryController {

	@Autowired
	private JungBoardService jungBoardService;
	
	@Autowired
	private JungMemberService jungMemberService;
	
	@Autowired
	private JungFileBoardService jungFileBoardService;
	
	@Autowired
	private JungCommentService jungCommentService;
	
//	@PostMapping("/galleryboardUpload")
//	public String galleryboard() {
//		return "gallery/galleryboardupload";
//	}
	
	@GetMapping("/galleryboardUpload")
	public String galleryboard() {
		return "gallery/galleryboardupload";
	}
	
	
	
	
	@RequestMapping(value = {"","/"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String gallery(@ModelAttribute(value = "cv") CommonVO cv, Model model, @RequestParam(value = "error", required = false) String error) {
		cv.setCategoryNum(4); //갤러리 번호 5번인데 일단 4번으로 함
		// psb search
		cv.setS(20);
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		model.addAttribute("pv", pv);
		model.addAttribute("cv", cv);
		model.addAttribute("categoryList",jungBoardService.findCategoryList());
		return "gallery/gallery";
	}
	
	@GetMapping(value = "/{idx}")
	public String as(@PathVariable(value = "idx") int idx, Model model, HttpServletRequest request, HttpServletResponse response) {
		JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
		if(boardVO == null) {
			return "redirect:/gallery?error=notFound";
		}
		if(boardVO.getDeleted() == 1) {
			return "redirect:/gallery?error=notFound";
		}
		if(boardVO.getCategoryNum() != 4) {
			return "redirect:/gallery?error=notFound";
		}
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
		return "gallery/galleryblog"; // 임시값 blog.html
	}
	
	@DeleteMapping(value = "/gallery/{boardIdx}")
	@ResponseBody
	public String deleteblog(HttpSession session, @PathVariable(value = "boardIdx") int boardIdx) {
		log.info("deleteblog({}) 실행", boardIdx);
		int result = jungBoardService.hide(boardIdx);
		return result+"";
	}


	
	
	
	@PostMapping(value = "/paged")
	@ResponseBody() //응답을 객체로 받는다. 
	public List<JungBoardVO> paging(@RequestBody Map<String, Object> map){
		CommonVO cv = new CommonVO();
		cv.setP((Integer) map.get("currentPage"));
		cv.setSearch((String) map.get("search"));
		cv.setS(20);
		cv.setCategoryNum(4);
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		return pv.getList();
	}
	
	// 게시글 수정 처리
	@GetMapping(value = "/update/{boardIdx}")
	public String updateBoard(@PathVariable("boardIdx") int boardIdx,Model model) {
		JungBoardVO updatedBoard=jungBoardService.selectByIdx(boardIdx);
		model.addAttribute("board",updatedBoard);
		log.debug("뭐냐고:{}",model);
		return "/gallery/update"; // 수정된 게시글로 리다이렉트
	}
	
	
	@GetMapping("/galleryboardUpdateOk")
	public String galleryboardUpdateOk(Model model) {
		return "redirect:/gallery";
	}
	
	
	
	//MultipartHttpServletRequest 파일을 받을 수 있는.
	@Transactional //한꺼번에 저장하기 위해 하나가 에러가되면 모든게 막히게
	@PostMapping("/galleryboardUpdateOk")
	public String galleryboardUpdateOk(HttpSession session, @ModelAttribute(value = "boardVO") JungBoardVO boardVO, MultipartHttpServletRequest request) {
		// 1.board 저장
		log.debug("왜요:{}", boardVO);
		jungBoardService.update(boardVO); // 업뎃하고
		jungFileBoardService.deleteByRef(boardVO.getIdx()); // 파일지우고
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
        return "redirect:/gallery";
	}
	
	
	
	
	@GetMapping("/galleryboardUploadOk")
	public String galleryboardUploadOk(Model model) {
		return "redirect:/gallery";
	}
	//MultipartHttpServletRequest 파일을 받을 수 있는.
	@Transactional //한꺼번에 저장하기 위해 하나가 에러가되면 모든게 막히게
	@PostMapping("/galleryboardUploadOk")
	public String galleryboardOk(HttpSession session, @ModelAttribute(value = "boardVO") JungBoardVO boardVO, MultipartHttpServletRequest request) {
		// 1.board 저장
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		boardVO.setRef(memberVO.getIdx());
		jungBoardService.insert(boardVO);
		
		String uploadPath = request.getServletContext().getRealPath("./upload/");
		
		 File file2 = new File(uploadPath);
	     if (!file2.exists()) {
	        file2.mkdirs();
	     }
	    log.info("서버 실제 경로 : " + uploadPath); // 확인용
	    
	    int count =1;
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
                    jungFileBoardService.insert(fileBoardVO);
                    log.info("COUNT2:{}" , count++);
                   
                 }
              }
           }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "redirect:/gallery";
	}
}
