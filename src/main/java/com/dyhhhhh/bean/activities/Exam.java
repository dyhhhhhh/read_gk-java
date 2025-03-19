package com.dyhhhhh.bean.activities;


import com.dyhhhhh.common.CommonClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 考试领域对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exam extends CommonClass<Exam> {

    //最终考试分数
    private Double activity_final_score;
    @Override
    public Exam fromMap(Map<String, Object> data) {
        Exam exam = new Exam();
        if (data == null){
            return exam;
        }
        exam.setId(String.valueOf(data.getOrDefault("id","")));
        exam.setType(String.valueOf(data.getOrDefault("type","")));
        exam.setTitle(String.valueOf(data.getOrDefault("title","")));
        Object activityFinalScore = data.get("activity_final_score");
        if (activityFinalScore != null){
            try {
                exam.setActivity_final_score(Double.parseDouble(activityFinalScore.toString()));
            }catch (NumberFormatException e){
                exam.setActivity_final_score(0.0);
            }
        }else {
            exam.setActivity_final_score(0.0);
        }
        return exam;
    }

}
