package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PathVO {
    String name;
    long duration;
    List<String> path;
    Date dptTm;
    Date arvTm;

    public PathVO(String name,long duration, List<String> path, Date dptTm, Date arvTm) {
        this.name = name;
        this.duration = duration;
        this.path = path;
        this.dptTm = dptTm;
        this.arvTm = arvTm;
    }
}
