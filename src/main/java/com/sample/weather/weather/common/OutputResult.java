package com.sample.weather.weather.common;

import com.fasterxml.jackson.annotation.JsonInclude;

public class OutputResult<T> {
    private boolean success = true;
    private ErrorCode errorCode = ErrorCode.error;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public OutputResult<T> withSuccess(boolean success){
        setSuccess(success);
        return this;
    }
    public OutputResult<T> withErrorCode(ErrorCode errorCode){
        setErrorCode(errorCode);
        return this;
    }
    public OutputResult<T> withData(T data){
        setData(data);
        return this;
    }






}
