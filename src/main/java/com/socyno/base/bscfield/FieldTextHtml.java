package com.socyno.base.bscfield;

import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldTextHtml extends FieldType {
    
    @Getter
    private static final FieldTextHtml instance = new FieldTextHtml();
    
	public String getTypeName() {
		return "TextHtml";
	}
}
