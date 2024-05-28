package org.hse.mylaundryapplication;

public class WM {

    public Integer dormitory_id, floor, is_working;

    public WM() {
    }

    public WM(Integer dormitory_id, Integer floor, Integer is_working) {
        this.dormitory_id = dormitory_id;
        this.floor = floor;
        this.is_working = is_working;
    }
}
