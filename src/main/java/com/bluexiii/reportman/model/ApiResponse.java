package com.bluexiii.reportman.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;
import java.util.Objects;

@Api("公共响应模型")
public class ApiResponse {
    @ApiModelProperty(value = "返回码", example = "0000")
    private String code;
    @ApiModelProperty(value = "返回信息", example = "成功")
    private String message;
    @ApiModelProperty(value = "数据", example = "")
    private Object data;

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}


