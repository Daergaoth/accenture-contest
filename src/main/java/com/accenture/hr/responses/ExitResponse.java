package com.accenture.hr.responses;

import com.accenture.hr.Statuses;

public class ExitResponse {
    private Statuses status;



    public Statuses getStatus(){
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }
}
