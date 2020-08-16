package com.socyno.base.bsctmplutil;

import com.jfinal.template.expr.ast.Array;
import com.socyno.base.bscmixutil.Base64Util;
import com.socyno.base.bscservice.HttpUtil;

import java.io.UnsupportedEncodingException;

public class EnjoyStringExpand {
    public String base64Encode(String self) {
        return Base64Util.encode(self.getBytes());
    }
    
    public String urlEncode(String self) throws UnsupportedEncodingException {
        return HttpUtil.urlEncode(self);
    }
    
    public String format(String self, Array.ArrayListExt args) {
        return String.format(self, args.toArray());
    }
}
