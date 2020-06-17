package com.socyno.base.bscexec;

public class NamingFormatInvalidException extends MessageException {
    
    private static final long serialVersionUID = 1L;
    
    public NamingFormatInvalidException(String message) {
        this(message, null);
    }
    
    public NamingFormatInvalidException(String message, Throwable e) {
        super(message, e);
    }
    
}
