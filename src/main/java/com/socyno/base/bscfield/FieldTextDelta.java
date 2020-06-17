package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldTextDelta extends FieldType {
    
    @Getter
    private static final FieldTextDelta instance = new FieldTextDelta();
    
	public String getTypeName() {
		return "TextDelta";
	}
}
