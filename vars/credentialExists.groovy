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
boolean call(String id) {
	globalId = id.toUpperCase().toString()
	branchId = "${globalId}_${BRANCH_NAME}".toUpperCase().toString()

	if (credentialExists(branchId)) {
		return true
	}

	if (credentialExists(globalId)) {
		return true
	}

	echo "No credential for ${globalId} or ${branchId}"

	return false
}
