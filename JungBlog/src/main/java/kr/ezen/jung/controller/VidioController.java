package kr.ezen.jung.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.ezen.jung.service.JungBoardService;
import kr.ezen.jung.vo.CommonVO;

@Controller
@RequestMapping("/vidio")
public class VidioController {
	
	@Autowired
	private JungBoardService jungBoardService;
	
	@RequestMapping(value = {"","/"}, method = {RequestMethod.GET, RequestMethod.POST}) // 214 test
	public String view(Model model, @ModelAttribute CommonVO commonVO) {
		
		return "test";
	}
	@GetMapping("/{idx}") // 214 test
	public String view2(@PathVariable(value = "idx") int idx, Model model) {
		model.addAttribute("board", jungBoardService.selectByIdx(idx));
		return "test";
	}
}
