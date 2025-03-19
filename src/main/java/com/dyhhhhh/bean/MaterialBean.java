package com.dyhhhhh.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class MaterialBean extends CommonPayloadBean{
    private String sub_type;
    private String sub_id;

    public MaterialBean() {
        super();
    }
}
