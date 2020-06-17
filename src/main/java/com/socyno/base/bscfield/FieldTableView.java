package com.socyno.base.bscfield;

import com.github.reinert.jjschema.SchemaIgnore;
import com.github.reinert.jjschema.v1.FieldType;

import lombok.Getter;

public class FieldTableView extends FieldType {
    
    @Getter
    private static final FieldTableView instance = new FieldTableView();

    @SchemaIgnore
	public String getTypeName() {
		return "TableView";
	}
}
