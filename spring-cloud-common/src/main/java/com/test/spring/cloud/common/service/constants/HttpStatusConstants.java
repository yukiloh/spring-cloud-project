package com.test.spring.cloud.common.service.constants;

public enum  HttpStatusConstants {
    BAD_GATWAY(502,"从上游服务器接收到无效相应");

    private int status;
    private String constant;

    HttpStatusConstants(int status, String constant) {
        this.status = status;
        this.constant = constant;
    }

    public int getStatus() {
        return status;
    }

    public String getConstant() {
        return constant;
    }
}
