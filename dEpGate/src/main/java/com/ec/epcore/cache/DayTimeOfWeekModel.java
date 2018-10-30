package com.ec.epcore.cache;

/**
 * @author xyc
 * @date 2018/8/14
 * 一天的时间范围
 */
public class DayTimeOfWeekModel {
    
    private Integer dayOfWeek;//周几 取国外时间方便计算
    
    private Integer startTime;//开始时间 分钟数
    
    private Integer endTime;//结束时间 分钟数

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }
}
