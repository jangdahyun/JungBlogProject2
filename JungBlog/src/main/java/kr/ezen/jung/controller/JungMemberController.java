package kr.ezen.jung.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.MailService;
import kr.ezen.jung.vo.CommonVO;
import kr.ezen.jung.vo.JungBoardVO;
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
    PasswordEncoder passwordEncoder;
    
    // 마이페이지
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
         
         log.info("myblog실행 cv: {}",cv);
         model.addAttribute("pv",pv);
         model.addAttribute("cv", cv);
         model.addAttribute("user",memberVO);
         model.addAttribute("categoryList",jungBoardService.findCategoryList());
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
         
         log.info("실행 cv: {}",pv);
         
         model.addAttribute("pv",pv);
         model.addAttribute("cv", cv);
         model.addAttribute("user",memberVO);
         model.addAttribute("categoryList",jungBoardService.findCategoryList());
   	 	return "my/myheartblog";
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
//	
//	@GetMapping(value = { "/logout" })
//	public String logout(HttpSession session) {
//		session.removeAttribute("user") ;
//		return "redirect:/";
//	}
    
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
//    	return mailService.mailSend(mailVO); // boolean 값 리턴!
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
  	
  	
  	@PostMapping(value = "/userIdCheck", produces = "text/plain;charset=UTF-8")
  	@ResponseBody
  	public String userIdCheck(@RequestBody Map<String, String>map ) {
  		String username = map.get("username");
  		return memberService.selectByUsername(username)+"";
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
  		log.debug("vo : {}",vo);
  		return "redirect:/";
  	}

//    // 특정유저 조회 : 모델에 담아둠
//    @GetMapping("/update/{username}")
//    public String showUpdateForm(@PathVariable String username, Model model) {
//        JungMemberVO memberVO = memberService.selectByUsername(username);
//        model.addAttribute("memberName", memberVO);
//        return "showMember"; 
//    }

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
    @PostMapping("/delete")
    public String deleteMember(@ModelAttribute(value = "memberVO") JungMemberVO deletememberVO,HttpSession session, Model model) {
    	JungMemberVO memberVO = (JungMemberVO)session.getAttribute("user");
    	deletememberVO.setIdx(memberVO.getIdx());
    	log.info("delete 성공 {}", deletememberVO);
    	 //자기 정보
    	log.info("나는 누구?{}",memberVO);
        model.addAttribute("user",memberVO);
//        memberService.delete(memberVO);
        
        return "redirect:/";  
    }
}
