package com.dyhhhhh.common.apis;

import com.dyhhhhh.common.enums.ActivityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.EnumMap;
import java.util.Map;

@Component
public class StrategyFactory {

    // 使用 EnumMap 管理策
    private final Map<ActivityType, Strategy> strategies;
    // 通过构造器注入所有策略
    @Autowired
    public StrategyFactory(
            PageStrategy pageStrategy,
            ExamStrategy examStrategy,
            ForumStrategy forumStrategy,
            MaterialStrategy materialStrategy,
            OnlineVideoStrategy onlineVideoStrategy,
            WebLinkStrategy webLinkStrategy,
            HomeworkStrategy homeworkStrategy,
            MixTaskStrategy mixTaskStrategy,
            VocabularyStrategy vocabularyStrategy
    ) {
        strategies = new EnumMap<>(ActivityType.class);
        strategies.put(ActivityType.PAGE, pageStrategy);
        strategies.put(ActivityType.EXAM, examStrategy);
        strategies.put(ActivityType.FORUM, forumStrategy);
        strategies.put(ActivityType.MATERIAL, materialStrategy);
        strategies.put(ActivityType.ONLINE_VIDEO, onlineVideoStrategy);
        strategies.put(ActivityType.WEB_LINK, webLinkStrategy);
        strategies.put(ActivityType.HOMEWORK, homeworkStrategy);
        strategies.put(ActivityType.MIX_TASK, mixTaskStrategy);
        strategies.put(ActivityType.VOCABULARY, vocabularyStrategy);
    }

    /**
     * 根据活动类型获取策略
     */
    public Strategy getStrategy(ActivityType type) {
        Strategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("未知活动类型: " + type);
        }
        return strategy;
    }
}
