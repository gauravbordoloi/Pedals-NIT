package com.gmonetix.pedals.model;

/**
 * @author Gmonetix
 */

public class Request {

    public String cycle_id, status, user_id, start_time, end_time,request_time;
    public long bill;

    public Request() {
    }

    public Request(String cycle_id, String status, String user_id, String start_time, String end_time, long bill, String request_time) {
        this.cycle_id = cycle_id;
        this.status = status;
        this.user_id = user_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.bill = bill;
        this.request_time = request_time;
    }
}
