package com.socyno.base.bscsqlutil;

public interface AbstractSqlStatement {
    public String getSql();
    public Object[] getValues();
}
