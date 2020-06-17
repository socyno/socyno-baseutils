package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldFormView extends FieldType {
    
    @Getter
    private static final FieldFormView instance = new FieldFormView();
    
    @Override
    public String getTypeName() {
        return "FormView";
    }
}
