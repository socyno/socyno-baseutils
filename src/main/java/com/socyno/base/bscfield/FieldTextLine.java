package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldTextLine extends FieldType {
    
    @Getter
    private static final FieldTextLine instance = new FieldTextLine();
    
	public String getTypeName() {
		return "TEXTLINE";
	}
}
