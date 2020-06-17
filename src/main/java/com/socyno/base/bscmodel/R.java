package com.socyno.base.bscmodel;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public class R {
    private int status = 0;
    private String message = "ok";
    private Object data = null;

    public R() {
        this(0, "ok");
    }
    
    public static R ok() {
    	return new R();
    }
    
    public static R error(String errmsg) {
    	return new R(500, errmsg);
    }
    
    public R(String message) {
        this(0, message);
    }
    
    public R(int status, String message) {
        this.status = status;
        this.message = message;
    }
    
    public int getCode() {
        return getStatus();
    }
    
    public String getMsg() {
        return getMessage();
    }
    
    public R setCode(int status) {
        return setStatus(status);
    }
    
    public R setData(Object data) {
        this.data = data;
        return this;
    }
    
    public R setMessage(String message) {
        this.message = message;
        return this;
    }
    
    public R setMsg(String message) {
        return setMessage(message);
    }
    
    public R setStatus(int status) {
        this.status = status;
        return this;
    }
    
    @SuppressWarnings("unchecked")
	public R put(final String key, final Object value) {
        if (!(data instanceof Map)) {
            data = new HashMap<String , Object>();
        }
        ((Map<String , Object>)data).put(key , value);
        return this;
    }
}
