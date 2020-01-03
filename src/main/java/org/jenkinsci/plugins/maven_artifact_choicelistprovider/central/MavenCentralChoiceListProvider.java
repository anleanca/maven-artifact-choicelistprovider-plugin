package org.jenkinsci.plugins.maven_artifact_choicelistprovider.central;

import java.util.Map;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactChoiceListProvider;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractMavenArtifactDescriptorImpl;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.util.FormValidation;
import jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider;

/**
 * 
 * The implementation of the {@link ChoiceListProvider} for MavenCentral repository.
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class MavenCentralChoiceListProvider extends AbstractMavenArtifactChoiceListProvider {

	private static final long serialVersionUID = -4215624253720954168L;

	@DataBoundConstructor
	public MavenCentralChoiceListProvider(String artifactId) {
		super(artifactId);
	}

	@Extension
	public static class MavenDescriptorImpl extends AbstractMavenArtifactDescriptorImpl {

		/**
		 * the display name shown in the dropdown to select a choice provider.
		 * 
		 * @return display name
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "MavenCentral Artifact Choice Parameter";
		}

		public FormValidation doTest(@QueryParameter String url, @QueryParameter String groupId,
				@QueryParameter String artifactId, @QueryParameter String packaging, @QueryParameter String classifier,
				@QueryParameter boolean reverseOrder, @QueryParameter String outputFilter) {
			final IVersionReader service = new MavenCentralSearchService();
			return super.performTest(service, "", groupId, artifactId, packaging, classifier, reverseOrder, outputFilter);
		}

		@Override
		protected Map<String, String> wrapTestConnection(IVersionReader service, String pRepositoryId, String pGroupId, String pArtifactId,
				String pPackaging, String pClassifier, boolean pReverseOrder, final String pOutputFilter) {
			return readURL(new MavenCentralSearchService(), pRepositoryId, pGroupId, pArtifactId, pPackaging, pClassifier,
					pReverseOrder, pOutputFilter);
		}

	}

	public IVersionReader createServiceInstance() {
		return new MavenCentralSearchService();
	}

}
