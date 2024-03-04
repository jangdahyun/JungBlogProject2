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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungFileBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungCommentVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
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
	
	
	@GetMapping("/fileupload")
	public String fileupload(Model model, @ModelAttribute(value = "cv") CommonVO cv) {
		model.addAttribute("cv", cv);
		return "file/fileupload";
	}
	
	@RequestMapping(value = {"","/"}, method = { RequestMethod.GET, RequestMethod.POST })
	public String fileboard(@ModelAttribute(value = "cv") CommonVO cv, Model model,JungMemberVO vo) {
		cv.setCategoryNum(2); //갤러리 번호 5번인데 일단 4번으로 함
		PagingVO<JungBoardVO> pv = jungBoardService.selectList(cv);
		
		List<JungBoardVO> list = pv.getList();
		
		list.forEach((board)->{
		// 유저정보 넣어주기
		board.setMember(jungMemberService.selectByIdx(board.getRef()));
		// 좋아요 갯수 넣어주기
		board.setCountHeart(jungBoardService.countHeart(board.getIdx()));
		// 카테고리 이름넣기
		board.setCategoryName(jungBoardService.findCategoryName(board.getCategoryNum()));
		// 파일넣기
		board.setFileboardVO(jungFileBoardService.selectfileByRef(board.getIdx()));
		});
		pv.setList(list);
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
	
}
