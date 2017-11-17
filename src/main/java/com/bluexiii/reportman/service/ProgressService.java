package com.bluexiii.reportman.service;

import com.bluexiii.reportman.model.Progress;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bluexiii on 07/11/2017.
 */
@Component
public class ProgressService {
    private Map<String, Progress> progressMap = new HashMap<>();

    public synchronized Progress getProgress(String progressId) {
        if (!progressMap.containsKey(progressId)) {
            return new Progress(0, "准备中");
        }
        return progressMap.get(progressId);
    }

    public synchronized void setProgress(String progressId, int percent, String message) {
        if (!progressMap.containsKey(progressId)) {
            Progress progress = new Progress(percent, message);
            progressMap.put(progressId, progress);
        } else {
            Progress progress = progressMap.get(progressId);
            progress.setPercent(percent);
            progress.setMessage(message);
        }
    }
}
