package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldFormAttachments extends FieldType {
    
    @Getter
    private static final FieldFormAttachments instance = new FieldFormAttachments();
    
    public String getTypeName() {
        return "File";
    }
}
