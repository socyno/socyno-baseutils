package com.socyno.base.bsctmplutil;

import java.util.Date;

public class EnjoyDateExpand {
    
    public long compareWithNow(Date self) throws Exception {
        return self.getTime() - System.currentTimeMillis();
    }
}
