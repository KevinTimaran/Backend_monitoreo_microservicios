package com.monitoring.microservicesmonitoring.dto;

import java.util.ArrayList;
import java.util.List;

public class ServiceOperationRequestDTO {

    private List<Object> params = new ArrayList<>();

    public ServiceOperationRequestDTO() {
    }

    public ServiceOperationRequestDTO(List<Object> params) {
        this.params = params;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }
}
