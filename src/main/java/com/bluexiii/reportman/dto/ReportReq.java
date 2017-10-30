package com.bluexiii.reportman.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * Created by bluexiii on 25/10/2017.
 */
@Api("生成报表请求")
public class ReportReq {
    @ApiModelProperty(value = "报表名称", example = "demo")
    private String filePrefix;
    @ApiModelProperty(value = "属性配置")
    private Map paramMap;

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public Map getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map paramMap) {
        this.paramMap = paramMap;
    }
}
