package com.accenture.hr.responses;

import com.accenture.hr.Statuses;

public class StatusResponse {
    private Statuses status;
    private int queuePosition;

    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
    }

    public Statuses getStatus(){
        return status;
    }

    public void setStatus(Statuses status) {
        this.status = status;
    }
}
