package kr.ezen.jung.controller;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungCommentService;
import kr.ezen.jung.service.JungFileBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.MailService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
import kr.ezen.jung.vo.JungFileBoardVO;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/member")
public class JungMemberController {

    @Autowired
    private JungMemberService memberService;
    
    @Autowired
    private MailService mailService;
    
    @Autowired
    private JungBoardService jungBoardService;
    
    @Autowired
    private JungFileBoardService jungFileBoardService;
    
    @Autowired
    private JungCommentService jungCommentService;
    		
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @GetMapping(value = {"/mypage"})
    public String mypage(HttpSession session, Model model) {
       if(session.getAttribute("user") == null) {
          return "redirect:/";
       }
       //자기 정보
       JungMemberVO sesesionUser = (JungMemberVO) session.getAttribute("user");
       JungMemberVO memberVO = memberService.selectByIdx(sesesionUser.getIdx());
    
       model.addAttribute("user",memberVO);
       return "my/mypage";
    }
    //내가 쓴 글
    @GetMapping(value = {"/myblog"})
    public String myblog(HttpSession session,@ModelAttribute(value = "cv")CommonVO cv, Model model) {
    	 if(session.getAttribute("user") == null) {
             return "redirect:/";
          }
    	 JungMemberVO sesesionUser = (JungMemberVO) session.getAttribute("user");
         JungMemberVO memberVO = memberService.selectByIdx(sesesionUser.getIdx());
         cv.setUserRef(sesesionUser.getIdx());
         cv.setS(10);
         PagingVO<JungBoardVO> pv = jungBoardService.selectByRef(cv);
         List<String> categoryList= jungBoardService.findCategoryList();
         categoryList.remove("QnA");
         categoryList.remove("공지사항");
         
         model.addAttribute("pv",pv);
         log.debug("pv:{}",pv);
         if(cv.getCategoryNum() != null) {
        	 model.addAttribute("categoryNum", cv.getCategoryNum());        	 
         }
         model.addAttribute("user",memberVO);
         model.addAttribute("categoryList",categoryList);
    	 return "my/myblog";
    }
    
    //내가 좋아요 한 글 보기
    @GetMapping(value = {"/myheartblog"} )
    public String myheartblog(HttpSession session,@ModelAttribute(value = "cv")CommonVO cv, Model model) {
    	if(session.getAttribute("user") == null) {
    		return "redirect:/";
         }
    	 JungMemberVO sesesionUser = (JungMemberVO) session.getAttribute("user");
    	 JungMemberVO memberVO = memberService.selectByIdx(sesesionUser.getIdx());
    	 cv.setUserRef(sesesionUser.getIdx());
         cv.setS(10);
         
         PagingVO<JungBoardVO> pv = jungBoardService.selectHeartByUseridx(cv);
         List<String> categoryList= jungBoardService.findCategoryList();
         categoryList.remove("QnA");
         categoryList.remove("공지사항");
         
         model.addAttribute("pv",pv);
         if(cv.getCategoryNum() != null) {
        	 model.addAttribute("categoryNum", cv.getCategoryNum());        	 
         }
         model.addAttribute("user",memberVO);
         model.addAttribute("categoryList",categoryList);
   	 	return "my/myheartblog";
    }
    //내가 쓴 글
    @GetMapping(value = {"/myqna"})
    public String myqna(HttpSession session,@ModelAttribute(value = "cv")CommonVO cv, Model model) {
    	 if(session.getAttribute("user") == null) {
             return "redirect:/";
          }
    	 JungMemberVO sesesionUser = (JungMemberVO) session.getAttribute("user");
         JungMemberVO memberVO = memberService.selectByIdx(sesesionUser.getIdx());
         cv.setUserRef(sesesionUser.getIdx());
         cv.setS(10);
         cv.setCategoryNum(5);
         
         PagingVO<JungBoardVO> pv = jungBoardService.selectByRef(cv);
         model.addAttribute("pv",pv);
         model.addAttribute("user",memberVO);
         model.addAttribute("cv", cv);
    	 return "my/myqna";
    }
    
    @GetMapping(value = {"/myqna/{idx}"})
    public String myqna(HttpSession session,@PathVariable(value = "idx")int idx, Model model) {
    	if(session.getAttribute("user") == null) {
    		return "redirect:/";
    	}
    	JungMemberVO sesesionUser = (JungMemberVO) session.getAttribute("user");
    	JungMemberVO memberVO = memberService.selectByIdx(sesesionUser.getIdx());
    	
    	JungBoardVO boardVO = jungBoardService.selectByIdx(idx);
    	boardVO.setCommentList(jungCommentService.selectByRef(idx, new CommonVO()).getList());
    	if(boardVO.getRef()!=memberVO.getIdx()) {
    		return "redirect:/";
    	}
    	model.addAttribute("board",boardVO);
    	model.addAttribute("user",memberVO);
    	return "my/myqnadetail";
    }
    
    @GetMapping("/myqnaupload")
	public String myqna() {
		return "my/myqnaupload";
	}
    
    @GetMapping("/myqnauploadOk")
	public String myqnauploadOk(Model model) {
		return "redirect:/member/myqna";
	}
	//MultipartHttpServletRequest 파일을 받을 수 있는.
	@Transactional //한꺼번에 저장하기 위해 하나가 에러가되면 모든게 막히게
	@PostMapping("/myqnauploadOk")
	public String myqnauploadOk(HttpSession session, @ModelAttribute(value = "boardVO") JungBoardVO boardVO, MultipartHttpServletRequest request) {
		// 1.board 저장
		JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
		boardVO.setRef(memberVO.getIdx());
		boardVO.setCategoryNum(5);
		jungBoardService.insert(boardVO);
		
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
                    jungFileBoardService.insert(fileBoardVO);
                 }
              }
           }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return "redirect:/member/myqna";
	}
	
    @PostMapping(value = "/userPwCheck")
    @ResponseBody
    public String updatePassword(@RequestBody JungMemberVO jungVO,HttpSession session) {
    	log.info("updatePassword 실행 : {}", jungVO);
    	JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
    	String password = jungVO.getPassword();

        // 사용자가 제출한 현재 비밀번호와 DB에 저장된 암호화된 비밀번호를 비교합니다.
        if (passwordEncoder.matches(password, memberVO.getPassword())) {
        	log.debug("성공", password);
            return "1";
        } else {
            // 비밀번호가 일치하지 않으면 오류 메시지를 반환합니다.
            return "0";
        }
    }
    
	@GetMapping(value = { "/login" })
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model) {
		if (error != null)
			model.addAttribute("error", "error");
		if (logout != null)
			model.addAttribute("logout", "logout");
		return "login";
	}
	
	@GetMapping(value = {"/findPw"})
	public String findPw(@RequestParam(value = "logout", required = false) String logout, Model model) {
		if (logout != null)
			model.addAttribute("logout", "logout");
		return "findPw";
	}
	
	@PostMapping(value = {"/updatePw"})
	public String updatePw(@ModelAttribute JungMemberVO updatememberVO) {
		log.info("updatePassword 실행 : {}", updatememberVO);
		JungMemberVO memberVO = memberService.selectByUsername(updatememberVO.getUsername());
		updatememberVO.setIdx(memberVO.getIdx());
		memberService.update(updatememberVO);
		return "redirect:/";
	}
	
	@GetMapping(value = {"/findUsername"})
	public String findUsername(@RequestParam(value = "logout", required = false) String logout, Model model) {
		if (logout != null)
			model.addAttribute("logout", "logout");
		return "findUsername";
	}
	
    // 회원가입
    @GetMapping("/join")
    public String RegisterForm(HttpSession session) {
    	// 현재 로그인이 되어있는데 회원가입을 하려고 한다. 막아야 한다.
		if(session.getAttribute("user")!=null) {
			session.removeAttribute("user");// 세션에 회원정보만 지운다.
			session.invalidate();// 세션자체를 끊고 다시 연결한다.
			return "redirect:/";
		}
        return "join"; 
    }
    
    // 회원가입중 필요한 이메일 인증을 보내는 주소
    @GetMapping(value = "/send", produces = "text/plain" )
    @ResponseBody
    public String send(@RequestParam(value = "to") String to) {
    	log.info("send : to=>{}",to);
    	String result = mailService.mailSend(to)+""; //
    	log.info("send Success?:{}", result);
    	return result;
    }
    
    //회원가입 완료
  	@GetMapping("/joinok")
  	public String joinOkGet() {
  		return "redirect:/";
  	}
  	
  	@PostMapping(value = "/userIdCheckByEmail", produces = "text/plain;charset=UTF-8")
  	@ResponseBody
  	public String userIdCheckByEmail(@RequestBody JungMemberVO memberVO) {
  		memberVO=memberService.selectByEmail(memberVO.getEmail());
		return memberVO.getUsername();
  	}
  	
  	@PostMapping(value = "/userIdCheck", produces = "text/plain;charset=UTF-8")
  	@ResponseBody
  	public String userIdCheck(@RequestBody JungMemberVO memberVO ) {
  		return memberService.selectCountByUsername(memberVO.getUsername())+"";
  	}
  	
  	@PostMapping(value = "/userNickNameCheck", produces = "text/plain;charset=UTF-8")
  	@ResponseBody
  	public String userNickNameCheck(@RequestBody JungMemberVO memberVO ) {
  		return memberService.selectCountByNickName(memberVO.getNickName())+"";
  	}
  	
  	/**
  	 * 
  	 * @param 이메일로 찾기
  	 * @return
  	 */
  	@PostMapping(value = "/userEmailCheck", produces = "text/plain;charset=UTF-8")
  	@ResponseBody
  	public String userEmailCheck(@RequestBody Map<String, String>map ) {
  		String email = map.get("email");
  		return memberService.selectByEmail(email)+"";
  	}
  	
  	//이메일 즁복체크
  	@PostMapping("/userEmailCheck2")
  	@ResponseBody
  	public String userEmailCheck(@RequestBody JungMemberVO memberVO) {
  		int result = memberService.emailCheck(memberVO.getEmail());
  		
  		return result +"";
  	}
  	
  	@PostMapping("/joinok")
  	public String joinOkPost(@ModelAttribute(value = "vo") JungMemberVO vo, @RequestParam(value = "bd")String bd ) {
  		// 내용 검증을 해줘야 한다.
  		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
  		Date date = null;
  		try {
  			date = formatter.parse(bd);
  		} catch (ParseException e) {
  			e.printStackTrace();
  		}
  		vo.setBirthDate(date);
  		vo.setRole("ROLE_USER");
  		memberService.insert(vo); // 저장
  		return "redirect:/";
  	}
  	
    // 신규 유저 추가
    @PostMapping("/insert")
    public String insertMember(@ModelAttribute JungMemberVO newMember) {
    	memberService.insert(newMember);
    	return "redirect:/"; 
    }
    
    // 유저 정보 수정
    @PostMapping("/update")
    public String updateMember(@ModelAttribute JungMemberVO updateMember,HttpSession session) {
    	JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
    	updateMember.setIdx(memberVO.getIdx());
    	log.info("update 성공 {}", updateMember);
        memberService.update(updateMember);
        session.setAttribute("user", memberService.selectByIdx(memberVO.getIdx()));
        return "redirect:/member/mypage"; 
    }
    

    // 유저 정보 삭제
    @GetMapping("/delete")
    public String deleteMember(@ModelAttribute(value = "memberVO") JungMemberVO deletememberVO,HttpSession session, Model model) {
    	JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
    	deletememberVO.setIdx(memberVO.getIdx());
    	 //자기 정보
        model.addAttribute("user",memberVO);
        return "my/delete";  
    }
    
    @PostMapping("/deleteOk")
    public String deleteOk(HttpSession session) {
    	JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
    	memberService.delete(memberVO.getIdx());
    	session.invalidate(); // 세션 무효화
    	return "redirect:/"; 
    }
}
