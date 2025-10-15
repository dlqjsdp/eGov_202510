package com.lime.user.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lime.user.service.UserService;
import com.lime.user.vo.UserVO;

/**
 * @Class Name  : UserController.java
 * @Description : 회원가입 및 ID 중복검사 컨트롤러
 * @Modification Information
 * 
 *     수정일                       수정자                    수정내용
 *     ----------    ---------    -------------------------------
 *     2025.10.15    노유경                     회원가입 폼, ID 중복검사, 회원가입 처리
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource(name = "userService")
    private UserService userService;

    // 회원가입 폼 페이지 진입
    @RequestMapping(value="/userInsert.do", method=RequestMethod.GET)
    public String userInsertForm() {
        return "/user/userInsert"; // JSP: /WEB-INF/jsp/user/userInsert.jsp
    }

    // ID 중복체크 (AJAX)
    @ResponseBody
    @RequestMapping(value="/checkId.do", method=RequestMethod.GET, produces="application/json; charset=UTF-8")
    public Map<String, Object> checkId(@RequestParam("userId") String userId) {
        boolean exist = userService.existsUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("available", exist);
        result.put("message", exist ? "사용 가능한 ID입니다." : "이미 사용 중인 ID입니다.");
        return result;
    }

    // 회원가입 처리
    @RequestMapping(value="/insertUser.do", method=RequestMethod.POST)
    public String insertUser(
            @RequestParam("userId") String userId,
            @RequestParam("pwd") String pwd,
            @RequestParam("userName") String userName,
            RedirectAttributes ra) {

        UserVO vo = new UserVO();
        vo.setUserId(userId);
        vo.setPwd(pwd);
        vo.setUserName(userName);

        userService.register(vo);
        ra.addFlashAttribute("msg", "회원가입이 완료되었습니다!");
        return "redirect:/login/login.do";
    }
}
