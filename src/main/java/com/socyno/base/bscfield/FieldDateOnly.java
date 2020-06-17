package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldDateOnly extends FieldType {
    
    @Getter
    private static final FieldDateOnly instance = new FieldDateOnly();
    
    public String getTypeName() {
        return "DateOnly";
    }
}
