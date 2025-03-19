package com.dyhhhhh.common.apis;

import com.dyhhhhh.config.RequestHttpConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component
public class VocabularyStrategy extends PageStrategy{
    @Override
    public void execute(String activityId, HashMap<String, Object> activityDetails, RequestHttpConfig httpConfig) {
        super.execute(activityId, activityDetails, httpConfig);
    }
}
