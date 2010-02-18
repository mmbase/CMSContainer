/*
 * Copyright 2003,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.finalist.pluto.portalImpl.services.config;

import com.finalist.pluto.portalImpl.services.ServiceManager;
import com.finalist.pluto.portalImpl.util.Parameters;

/**
 * * The static accessor for the {@link ConfigService}. * *
 * 
 * @see ConfigService
 */

public class Config {

	/**
	 * *
	 * 
	 * @see ConfigService#getParameters
	 */

	public static Parameters getParameters() {
		return (cService.getParameters());
	}

	public static ConfigService getService() {
		return cService;
	}

	private final static ConfigService cService = (ConfigService) ServiceManager.getService(ConfigService.class);
}