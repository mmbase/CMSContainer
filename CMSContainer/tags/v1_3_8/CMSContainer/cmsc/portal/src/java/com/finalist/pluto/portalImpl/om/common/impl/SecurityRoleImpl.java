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
package com.finalist.pluto.portalImpl.om.common.impl;

import java.io.Serializable;

import org.apache.pluto.om.common.SecurityRole;

public class SecurityRoleImpl implements SecurityRole, Serializable {

	private String description;

	private String roleName;

	public SecurityRoleImpl() {
	}

	// SecurityRole implementation.

	public String getDescription() {
		return description;
	}

	public String getRoleName() {
		return roleName;
	}

	// additional methods.

	public void setDescription(String description) {
		this.description = description;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
