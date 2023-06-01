package com.kh.mini_sample.vo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MemberVO {
    private String id;
    private String pwd;
    private String name;
    private String email;
    private Date joined;
}
