package com.kh.mini_sample.controller;


import com.kh.mini_sample.vo.MemberVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://subwaymap.ddns.net:40")
@RestController
public class MemberController {

    // POST : 로그인
    @PostMapping("/login")
    public ResponseEntity<Boolean> memberLogin(@RequestBody Map<String,String> loginData){
       return null;
    }
    // GET : 회원 조회
    @GetMapping("/member/")
    public ResponseEntity<List<MemberVO>> memberList(@RequestParam String id) {
        return null;
    }
    // GET : 가입여부 확인
    @GetMapping("/check")
    public ResponseEntity<Boolean> memberCheck(@RequestParam String id){
        return null;
    }
    // POST : 회원가입
    // POST : 회원 가입
    @PostMapping("/new")
    public ResponseEntity<Boolean> memberRegister(@RequestBody Map<String, String> regData) {
        return null;
    }

}
