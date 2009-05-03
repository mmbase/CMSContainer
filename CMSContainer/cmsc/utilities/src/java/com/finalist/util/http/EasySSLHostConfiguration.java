package com.finalist.util.http;

import org.apache.commons.httpclient.contrib.ssl.HostConfigurationWithHostFactory;
import org.apache.commons.httpclient.contrib.ssl.HttpHostFactory;

public class EasySSLHostConfiguration extends HostConfigurationWithHostFactory {

	public EasySSLHostConfiguration() {
		super(HttpHostFactory.DEFAULT);
	}

	
}
