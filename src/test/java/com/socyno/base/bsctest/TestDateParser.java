package com.socyno.base.bsctest;

import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;

import com.socyno.base.bscmixutil.StringUtils;

public class TestDateParser {
    
    @Test
    public void testsSvnFormatDate() {
        Date svnDate;
        Assert.assertNotNull(svnDate = StringUtils.parseDate("2020-03-12T07:38:57.557556Z"));
        Assert.assertEquals(DateFormatUtils.format(svnDate, "yyyy-MM-dd HH:mm:ss"),  "2020-03-12 07:38:57");
    }
    
}
