package com.socyno.base.bscsshutil;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SshScriptConfig {
    private String host;
    private String user;
    private String script;
    private String password;
}
