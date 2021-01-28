#!groovy

import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.StandardCredentials
import com.cloudbees.plugins.credentials.domains.DomainRequirement

Boolean credentialExists(String id) {
	def cred = CredentialsProvider.findCredentialById(
		id,
		StandardCredentials.class,
		currentBuild.getRawBuild(),
		Collections.<DomainRequirement>emptyList()
	)

	return cred != null
}

/**
 * Check if credential exists.
 * @param id credentialsId to check
 * @return True if a credential exists and is accessible, false otherwise.
 */
String call(String id) {
	globalId = id.toUpperCase().toString()

	if (env.CHANGE_TARGET != null) {
		branchId = "${globalId}_${env.CHANGE_TARGET}".toUpperCase().toString()
	} else {
		branchId = "${globalId}_${env.BRANCH_NAME}".toUpperCase().toString()
	}

	if (credentialExists(branchId)) {
		return branchId
	}

	if (credentialExists(globalId)) {
		return globalId
	}

	echo "No credential for ${globalId} or ${branchId}"

	return null
}
