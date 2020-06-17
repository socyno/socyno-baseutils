package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldPassword extends FieldType {
    
    @Getter
    private static final FieldPassword instance = new FieldPassword();
    
    public String getTypeName() {
        return "PASSWORD";
    }
}
