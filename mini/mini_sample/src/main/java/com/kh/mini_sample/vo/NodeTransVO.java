package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NodeTransVO extends NodeVO implements Comparable<NodeTransVO> {

    public NodeTransVO(int index, long cost,int count) {
        this.setIndex(index);
        this.setCost(cost);
        this.setTrainN("");
        this.setCount(count);
        this.setIsTrans("");
        this.setDptTable(null);
        this.setStartDest("");
    }

    @Override
    public int compareTo(NodeTransVO o) {
        if (this.getCount() == o.getCount()) {
            return Long.compare(this.getCost(), o.getCost());
        }
        return Integer.compare(this.getCount(), o.getCount());
    }
}

