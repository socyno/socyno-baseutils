package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldTimeOnly extends FieldType {
    
    @Getter
    private static final FieldTimeOnly instance = new FieldTimeOnly();
    
	public String getTypeName() {
		return "TimeOnly";
	}
}
