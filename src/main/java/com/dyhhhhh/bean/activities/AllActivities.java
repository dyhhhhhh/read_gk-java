package com.dyhhhhh.bean.activities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AllActivities {
    private List<Exam> examList;
    private List<LearningActivities> learningActivitiesList;
}
