/*-
 * ============LICENSE_START=======================================================
 * openECOMP : SDN-C
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
 * 							reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.openecomp.sdnc.uebclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.openecomp.sdc.api.consumer.IConfiguration;
import org.openecomp.sdc.utils.ArtifactTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SdncUebConfiguration implements IConfiguration{

	private static final String SDNC_CONFIG_DIR = "SDNC_CONFIG_DIR";
	private static final Logger LOG = LoggerFactory
			.getLogger(SdncUebConfiguration.class);

	private String asdcAddress = null;
	private String consumerGroup = null;
	private String consumerID = null;
	private String environmentName = null;
	private String password = null;
	private int pollingInterval = 30;
	private int pollingTimeout = 15;
	private List<String> relevantArtifactTypes = null;
	private String user = null;

	private String sdncUser = null;
	private String sdncPasswd = null;
	private String asdcApiBaseUrl = null;
	private String asdcApiNamespace = null;

	private SdncArtifactMap artifactMap = SdncArtifactMap.getInstance();

	public String getAsdcApiNamespace() {
		return asdcApiNamespace;
	}

	private String incomingDir = null;

	private String archiveDir = null;

	private String overrideFile = null;

	private boolean activateServerTLSAuth;
	private String keyStorePassword;
	private String keyStorePath;

	private String xsltPathList;

	public String getXsltPathList() {
		return xsltPathList;
	}

	public String getOverrideFile() {
		return overrideFile;
	}

	public SdncUebConfiguration() {

		try {
			init();
		} catch (Exception e) {
			LOG.error("Cannot initialize SdncUebConfiguration", e);
		}
	}

	public void init() throws IOException {
		String propPath = null;
		String propDir = System.getenv(SDNC_CONFIG_DIR);
		if (propDir == null) {

			propDir = "/opt/sdnc/data/properties";
		}
		propPath = propDir + "/ueb-listener.properties";
		File propFile = new File(propPath);


		if (!propFile.exists()) {

			throw new FileNotFoundException(
					"Missing configuration properties file : "
							+ propFile);
		}

		Properties props = new Properties();
		props.load(new FileInputStream(propFile));

		asdcAddress = props.getProperty("org.openecomp.sdnc.uebclient.asdc-address");
		consumerGroup = props.getProperty("org.openecomp.sdnc.uebclient.consumer-group");
		consumerID = props.getProperty("org.openecomp.sdnc.uebclient.consumer-id");
		environmentName = props.getProperty("org.openecomp.sdnc.uebclient.environment-name");
		password = props.getProperty("org.openecomp.sdnc.uebclient.password");
		user = props.getProperty("org.openecomp.sdnc.uebclient.user");

		sdncUser = props.getProperty("org.openecomp.sdnc.uebclient.sdnc-user");
		sdncPasswd = props.getProperty("org.openecomp.sdnc.uebclient.sdnc-passwd");
		asdcApiBaseUrl = props.getProperty("org.openecomp.sdnc.uebclient.asdc-api-base-url");
		asdcApiNamespace = props.getProperty("org.openecomp.sdnc.uebclient.asdc-api-namespace");

		incomingDir = props.getProperty("org.openecomp.sdnc.uebclient.spool.incoming");
		archiveDir = props.getProperty("org.openecomp.sdnc.uebclient.spool.archive");
		overrideFile = props.getProperty("org.openecomp.sdnc.uebclient.override-file");

		String curval = props.getProperty("org.openecomp.sdnc.uebclient.polling-interval");
		if ((curval != null) && (curval.length() > 0)) {
			try {
				pollingInterval = Integer.parseInt(curval);
			} catch (Exception e) {
				LOG.warn("Illegal value for org.openecomp.sdnc.uebclient.polling-interval ("+curval+")");
			}
		}

		curval = props.getProperty("org.openecomp.sdnc.uebclient.polling-timeout");
		if ((curval != null) && (curval.length() > 0)) {
			try {
				pollingTimeout = Integer.parseInt(curval);
			} catch (Exception e) {
				LOG.warn("Illegal value for org.openecomp.sdnc.uebclient.polling-timeout ("+curval+")");
			}
		}

		curval = props.getProperty("org.openecomp.sdnc.uebclient.relevant-artifact-types");
		if ((curval != null) && (curval.length() > 0)) {
			String[] artifactTypes = curval.split(",");

			relevantArtifactTypes = new LinkedList<String>();

			for (int i = 0 ; i < artifactTypes.length ; i++) {
				try {
					if (ArtifactTypeEnum.valueOf(artifactTypes[i]) != null) {
							relevantArtifactTypes.add(artifactTypes[i]);
					} else {
						LOG.warn("Skipping unrecognized artifact type "+artifactTypes[i]);
					}
				} catch (Exception e) {

					LOG.warn("Caught exception validating artifact type "+artifactTypes[i], e);
				}

			}

		}

		curval = props.getProperty("org.openecomp.sdnc.uebclient.activate-server-tls-auth", "false");
		activateServerTLSAuth = "true".equalsIgnoreCase(curval);
		keyStorePath = props.getProperty("org.openecomp.sdnc.uebclient.keystore-path");
		keyStorePassword = props.getProperty("org.openecomp.sdnc.uebclient.keystore-password");
		xsltPathList = props.getProperty("org.openecomp.sdnc.uebclient.xslt-path-list");


		String artifactMapFile = props.getProperty("org.openecomp.sdnc.uebclient.artifact-map");
		if (artifactMapFile != null) {
			artifactMap.load(artifactMapFile);
		}

	}

	@Override
	public String getAsdcAddress() {
		return asdcAddress;
	}

	@Override
	public String getConsumerGroup() {
		return consumerGroup;
	}

	@Override
	public String getConsumerID() {
		return consumerID;
	}

	@Override
	public String getEnvironmentName() {
		return environmentName;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public int getPollingInterval() {
		return pollingInterval;
	}

	@Override
	public int getPollingTimeout() {
		return pollingTimeout;
	}

	@Override
	public List<String> getRelevantArtifactTypes() {
		return relevantArtifactTypes;
	}

	@Override
	public String getUser() {
		return user;
	}


	public String getSdncUser() {
		return sdncUser;
	}

	public String getSdncPasswd() {
		return sdncPasswd;
	}

	public String getAsdcApiBaseUrl() {
		return asdcApiBaseUrl;
	}

	@Override
	public boolean activateServerTLSAuth() {
		return activateServerTLSAuth;
	}

	@Override
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	@Override
	public String getKeyStorePath() {
		return keyStorePath;
	}

	public String getIncomingDir() {
		return incomingDir;
	}

	public String getArchiveDir() {
		return archiveDir;
	}

	public int getMaxPasses() {
		return(artifactMap.getNumPasses());
	}

	public SdncArtifactMap.SdncArtifactType getMapping(String tag) {
		return(artifactMap.getMapping(tag));
	}

	@Override
	public boolean isFilterInEmptyResources() {
		// TODO Auto-generated method stub
		return false;
	}


}
