package kr.ezen.jung.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import kr.ezen.jung.service.JungMemberService;
import kr.ezen.jung.service.MailService;
import kr.ezen.jung.vo.JungMemberVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/member")
public class JungMemberController {

    @Autowired
    private JungMemberService memberService;
    
    @Autowired
    private MailService mailService;
    
    
    // 미완
    @GetMapping(value = {"/mypage"})
    public String mypage(HttpSession session) {
    	if(session.getAttribute("user") == null) {
    		return "redirect:/";
    	}
    	return "mypage";
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
			session.setMaxInactiveInterval(60*30);
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
    @PostMapping("/update/{username}")
    public String updateMember(@PathVariable String username, @ModelAttribute JungMemberVO updateMember) {
        memberService.update(updateMember);
        return "redirect:/"; 
    }

    // 유저 정보 삭제
    @PostMapping("/delete")
    public String deleteMember(@ModelAttribute(value = "memberVO") JungMemberVO memberVO,@PathVariable String username) {
        memberService.delete(memberVO);
        
        return "redirect:/";  
    }
}
