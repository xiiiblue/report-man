package com.bluexiii.reportman.domain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

@Api("公共响应模型")
public class ApiResponse {
    @ApiModelProperty(value = "返回码", example = "0000")
    private String code;
    @ApiModelProperty(value = "返回信息", example = "成功")
    private String message;

    public ApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
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
}


