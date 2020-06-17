package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldText extends FieldType {
    
    @Getter
    private static final FieldText instance = new FieldText();
    
	public String getTypeName() {
		return "TEXT";
	}
}
