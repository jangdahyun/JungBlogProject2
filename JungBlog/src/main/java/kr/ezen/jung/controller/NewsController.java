package kr.ezen.jung.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.ezen.jung.service.NewsCommentService;
import kr.ezen.jung.service.NewsService;
import kr.ezen.jung.vo.JungMemberVO;
import kr.ezen.jung.vo.RssCommentVO;
import kr.ezen.jung.vo.RssVO.Item;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/news")
public class NewsController {
	
	@Autowired
	private NewsService newsService;
	
	@GetMapping(value = {"","/"})
	public String newsMaing(Model model) {
		return "news/main";
	}
	// 기사 얻는 주소 (String search, String category, int sizeOfPage, int lastItemIdx)
	@PostMapping(value = "/getNews")
	@ResponseBody
	public List<Item> getItems(@RequestBody HashMap<String, String> map){
		List<Item> list = newsService.getItems(map.get("search"), map.get("category"), Integer.parseInt(map.get("sizeOfPage")), Integer.parseInt(map.get("lastItemIdx")));
		return list;
	}
	
	// 전체
	@GetMapping(value = "/all-news")
	public String allNews(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","전체 뉴스");
		model.addAttribute("search", search);
		model.addAttribute("category", null);
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 경제
	@GetMapping(value = "/economy")
	public String economy(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","전체 뉴스");
		model.addAttribute("category", "경제");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 증권
	@GetMapping(value = "/finance")
	public String finance(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","증권 뉴스");
		model.addAttribute("category", "증권");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 부동산
	@GetMapping(value = "/realetate")
	public String realetate(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","부동산 뉴스");
		model.addAttribute("category", "부동산");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 금융
	@GetMapping(value = "/financial-market")
	public String financialMarket(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","금융 뉴스");
		model.addAttribute("category", "금융");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 산업
	@GetMapping(value = "/industry")
	public String industry(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","산업 뉴스");
		model.addAttribute("category", "산업");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 정치
	@GetMapping(value = "/politics")
	public String politics(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","정치 뉴스");
		model.addAttribute("category", "정치");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 사회
	@GetMapping(value = "/society")
	public String society(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","사회 뉴스");
		model.addAttribute("category", "사회");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 국제
	@GetMapping(value = "/international")
	public String international(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","국제 뉴스");
		model.addAttribute("category", "국제");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// IT 과학
	@GetMapping(value = "/it")
	public String it(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","전체 뉴스");
		model.addAttribute("category", "IT·과학");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 라이프
	@GetMapping(value = "/life")
	public String life(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","생활 뉴스");
		model.addAttribute("category", "라이프");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 아르떼
	@GetMapping(value = "/arte")
	public String arte(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","아르떼 뉴스");
		model.addAttribute("category", "아르떼");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 골프
	@GetMapping(value = "/golf")
	public String golf(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","골프 뉴스");
		model.addAttribute("category", "골프");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 스포츠
	@GetMapping(value = "/sports")
	public String sports(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","스포츠 뉴스");
		model.addAttribute("category", "스포츠");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	// 연예
	@GetMapping(value = "/entertainment")
	public String entertainment(Model model, @RequestParam(value = "search", required = false) String search) {
		model.addAttribute("title","연예 뉴스");
		model.addAttribute("category", "연예");
		model.addAttribute("lastItemIdx", newsService.getLastItemIdx());
		return "news/views";
	}
	
	/** 글 하나보기 */
	@GetMapping(value = "/view/{idx}")
	public String view(Model model, @PathVariable(value = "idx") int idx, HttpServletRequest request, HttpServletResponse response) {
		Item item = newsService.selectByIdx(idx);
		log.info("news view 실행 idx => {}, item => {}", idx, item);
		if(item == null) {
			return "redirect:/news/all-news";
		}
		Cookie oldCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("news")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + idx + "]")) {
                newsService.updateReadCount(idx);
                oldCookie.setValue(oldCookie.getValue() + "_[" + idx + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60);
                response.addCookie(oldCookie);
            }
        } else {
            newsService.updateReadCount(idx);
            Cookie newCookie = new Cookie("news","[" + idx + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60);
            response.addCookie(newCookie);
        }
		
        int lastItemIdx = newsCommentService.findLastItemIdx();
		model.addAttribute("commentLastItemIdx", lastItemIdx + 1);
		model.addAttribute("commentCount", newsCommentService.getTotalCountByRssBoardRef(idx));
		model.addAttribute("rssBoardRef", idx);
		model.addAttribute("item", item);
		return "news/view";
	}
	
	@PostMapping(value = "/updateLikeCount")
	@ResponseBody
	public int updateLikeCount(@RequestBody HashMap<String, String> map) {
		return newsService.updateLikeCount(Integer.parseInt(map.get("idx")));
	}
	
	@Autowired
	private NewsCommentService newsCommentService;
	
	@GetMapping(value = "/view/{idx}/comment")
	public String commentView(Model model ,@PathVariable(value = "idx") int idx) {
		int lastItemIdx = newsCommentService.findLastItemIdx();
		model.addAttribute("lastItemIdx", lastItemIdx + 1);
		model.addAttribute("rssBoardRef", idx);
		return "news/commentView";
	}
	
	// 글 얻기
	@GetMapping(value = "/comment")
	@ResponseBody
	public List<RssCommentVO> getComments(@RequestParam(value = "rssBoardRef") int rssBoardRef, @RequestParam(value = "sizeOfPage") int sizeOfPage, @RequestParam(value = "lastItemIdx") int lastItemIdx){
		List<RssCommentVO> list = newsCommentService.getCommentByRssBoardRef(rssBoardRef, sizeOfPage, lastItemIdx);
		return list;
	}
	
	// 댓글 쓰기
	@PostMapping(value = "/comment")
	public String writeComment(@ModelAttribute RssCommentVO rcv, HttpSession session) {
		JungMemberVO memberVO = (JungMemberVO) session.getAttribute("user");
		rcv.setUserRef(memberVO.getIdx());
		log.info("댓글쓰기 => {}", rcv);
		newsCommentService.insertRssComment(rcv);
		return "redirect:/news/view/" + rcv.getRssBoardRef();
	}
	
	// 글 수정
	@PutMapping(value = "/comment")
	@ResponseBody
	public int updateComment(@RequestBody RssCommentVO rcv) {
		int result = newsCommentService.updateRssComment(rcv.getIdx(), rcv.getReply());
		return result;
	}
	
	// 글 삭제
	@DeleteMapping(value = "/comment")
	@ResponseBody
	public int deleteComment(@RequestBody RssCommentVO rcv) {
		int result = newsCommentService.deleteRssCommentByIdx(rcv.getIdx());
		return result;
	}
	
	// 대댓글 얻기
	@GetMapping(value = "/comments/reply")
	@ResponseBody
	public List<RssCommentVO> getReply(@RequestParam(value = "parentCommentRef") int parentCommentRef){
		List<RssCommentVO> list = newsCommentService.findReplyByParentCommentRef(parentCommentRef);
		return list;
	}
	
	
	
}
