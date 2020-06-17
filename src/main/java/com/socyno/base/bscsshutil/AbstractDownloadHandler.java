package com.socyno.base.bscsshutil;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractDownloadHandler {
	public abstract void process(InputStream stream) throws IOException;
}
