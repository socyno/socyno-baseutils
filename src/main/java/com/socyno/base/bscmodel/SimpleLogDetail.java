package com.socyno.base.bscmodel;

import lombok.Data;

@Data
public class SimpleLogDetail {
    
    private Long id;
    /**
     * 操作前对象
     */
    private String operateBefore;
    /**
     * 操作后对象
     */
    private String operateAfter;
}
