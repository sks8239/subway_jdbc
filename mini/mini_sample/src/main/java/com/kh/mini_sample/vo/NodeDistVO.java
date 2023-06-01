package com.kh.mini_sample.vo;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NodeDistVO extends NodeVO implements Comparable<NodeDistVO>  {

    public NodeDistVO(int index, long cost,int count) {
        this.setIndex(index);
        this.setCost(cost);
        this.setTrainN("");
        this.setCount(count);
        this.setIsTrans("");
        this.setDptTable(null);
        this.setStartDest("");
    }
    @Override
    public int compareTo(NodeDistVO o) {
        if (this.getCost() == o.getCost()) {
            return Integer.compare(this.getCount(), o.getCount());
        }
        return Long.compare(this.getCost(), o.getCost());
    }
}
