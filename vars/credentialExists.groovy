import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.DomainRequirement

/**
 * Check if credential exists.
 * @param id credentialsId to check
 * @return True if a credential exists and is accessible, false otherwise.
 */
boolean call(String id) {
  def available_credentials = CredentialsProvider.findCredentialById(
    id,
    UsernamePasswordCredentials.class,
    currentBuild.getRawBuild(),
    Collections.<DomainRequirement>emptyList()
  )
  return available_credentials != null
}