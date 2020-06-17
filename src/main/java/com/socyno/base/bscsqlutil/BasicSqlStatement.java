package com.socyno.base.bscsqlutil;

import org.apache.commons.lang3.ArrayUtils;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BasicSqlStatement implements AbstractSqlStatement {
    private String sql;
    private Object[] values;
    
    public BasicSqlStatement setSql(String sql) {
        this.sql = sql;
        return this;
    }
    
    public BasicSqlStatement setValues(Object[] values) {
        this.values = ArrayUtils.clone(values);
        return this;
    }
}
