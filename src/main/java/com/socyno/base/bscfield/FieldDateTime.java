package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldDateTime extends FieldType {

    @Getter
    private static final FieldDateTime instance = new FieldDateTime();
    
    public String getTypeName() {
        return "DateTime";
    }
}
