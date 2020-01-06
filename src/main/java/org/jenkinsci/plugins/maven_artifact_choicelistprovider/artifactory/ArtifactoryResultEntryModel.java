package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import com.google.gson.annotations.SerializedName;

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactoryResultEntryModel  implements Comparable {

    @SerializedName("uri")
    String uri;

    public ArtifactoryResultEntryModel() {
        // Important to do nothing
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public int compareTo(Object o) {
        ArtifactoryResultEntryModel obj = (ArtifactoryResultEntryModel) o;
        return this.getUri().compareTo(obj.getUri());
    }

}



