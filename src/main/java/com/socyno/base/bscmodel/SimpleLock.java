package com.socyno.base.bscmodel;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@ToString
@Accessors(chain = true)
public class SimpleLock {
    
    public static enum STATES {
        created,
        started,
        released;
    }
    
    private Long id;
    private String objectType;
    private String objectId;
    private String title;
    private String logfile;
    private Integer locked;
    private String  state;
    private Boolean result;
    private Date createdAt;
    private String createdUserId;
    private String createdUserName;
    private Date runningAt;
    private Date unlockedAt;
    private String unlockedUserId;
    private String unlockedUserName;
    private Integer timeoutSeconds;

    public boolean isLocked() {
        return getLocked() != null;
    }
    
    public boolean isPending() {
        return getLocked() != null && STATES.created.name().equals(getState());
    }
    
    public boolean isRunning() {
        return getLocked() != null && STATES.started.name().equals(getState());
    }
    
    public boolean isFinished() {
        return getLocked() == null;
    }
    
    public boolean alreadyTimeout() {
        return getTimeoutSeconds() != null && getTimeoutSeconds() > 0 && getCreatedAt() != null
                && (new Date().getTime() - getCreatedAt().getTime() > getTimeoutSeconds() * 1000);
    }
}