package com.bluexiii.reportman.web;

import com.bluexiii.reportman.model.ApiResponse;
import com.bluexiii.reportman.model.Progress;
import com.bluexiii.reportman.service.ProgressService;
import com.bluexiii.reportman.service.ReportManService;
import com.bluexiii.reportman.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@Api(value = "开放接口", description = "开放接口")
@RequestMapping("/api/")
public class ReportManController {
    @Autowired
    private ReportManService reportManService;
    @Autowired
    private ProgressService progressService;

    @ApiOperation(value = "生成报表(异步)")
    @PostMapping("/reports")
    public ApiResponse reports(@ApiParam(value = "示例: {\"file.prefix\": \"demo\", \"mail.to.list\": \"foo@bar.com\", \"sql.start_date\": \"2017-10-1\",\"sql.end_date\": \"2017-10-31\"}", required = true)
                               @RequestBody Map<String, String> paramMap) throws IOException {
        // 处理参数
        String filePrefix = paramMap.get("file.prefix");
        paramMap.remove("file.prefix");

        // 取报表后缀
        String fileSuffix = StringUtils.getFileSuffix();

        // 生成报表
        reportManService.makeReportAsync(filePrefix, fileSuffix, paramMap, true);

        // HTTP响应
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reportId", fileSuffix);
        return new ApiResponse("0000", "报表生成中，稍后会发送至您的邮箱。请不要重复点击生成报表。", data);
    }

    @ApiOperation(value = "获取报表进度")
    @GetMapping("/reports/{id}")
    public ApiResponse reports(@PathVariable String id) throws IOException {
        Progress progress = progressService.getProgress(id);
        return new ApiResponse("0000", "成功", progress);
    }
}