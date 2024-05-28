package org.hse.mylaundryapplication;

public class ScheduleItem {


    private String time_1;
    private String time_2;
    private String time_3;
    private String time_4;
    private String time_5;
    private String time_6;
    private Integer floor;

    public void setTime1(String start) {
        this.time_1=start;
    }

    public void setTime2(String start) {
        this.time_2=start;
    }

    public void setTime3(String start) {
        this.time_3=start;
    }

    public void setTime4(String start) {
        this.time_4=start;
    }

    public void setTime5(String start) {
        this.time_5=start;
    }

    public void setTime6(String start) {
        this.time_6=start;
    }

    public void setFloor(Integer floor) {this.floor=floor;}

    public Integer getFloor() {
        return floor;
    }

    public String getTime1() {
        return time_1;
    }
    public String getTime2() {
        return time_2;
    }
    public String getTime3() {
        return time_3;
    }
    public String getTime4() {
        return time_4;
    }
    public String getTime5() {
        return time_5;
    }
    public String getTime6() {
        return time_6;
    }


    interface OnItemClick {
        void onClick(ScheduleItem data);
    }


}
