package org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.AbstractRESTfulVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.IVersionReader;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.ValidAndInvalidClassifier;
import org.jenkinsci.plugins.maven_artifact_choicelistprovider.nexus.StandardRESTfulParameterBuilder;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Class utilizes the RESTful Search API from jFrog Artifactory to search for
 * items. <br>
 * <a href="https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API">Documentation</a>
 * 
 * @author stephan.watermeyer, Diebold Nixdorf
 */
public class ArtifactorySearchService extends AbstractRESTfulVersionReader implements IVersionReader {

    private static final Logger LOGGER = Logger.getLogger(ArtifactorySearchService.class.getName());

    public ArtifactorySearchService(String pURL) {
        super(pURL);
    }

    private static final String SEARCH_SERVICE = "api/search/gavc";

    @Override
    public String getRESTfulServiceEndpoint() {
        return SEARCH_SERVICE;
    }

    @Override
    public Set<String> callService(String pRepositoryId, String pGroupId, String pArtifactId, String pPackaging, ValidAndInvalidClassifier pClassifier, final String pOutputFilter) {
        final MultivaluedMap<String, String> requestParams = new StandardRESTfulParameterBuilder().create("", pGroupId, pArtifactId, pPackaging, pClassifier);

        Set<String> retVal = new LinkedHashSet<String>();
        LOGGER.info("call artifactory service");
        final String plainResult = getInstance().queryParams(requestParams).accept(MediaType.APPLICATION_JSON).get(String.class);

        if (plainResult == null) {
            LOGGER.info("response from Artifactory Service is NULL.");
        } else {
            LOGGER.info("parse result from artifactory service to JSON");
            retVal = parseResult(plainResult, pPackaging, pOutputFilter);
        }

        return retVal;
    }

    Set<String> parseResult(final String pContent, final String pPackaging, final String pOutputFilter) {
        final Set<String> retVal = new LinkedHashSet<String>();
        try {
            final ArtifactoryResultModel fromJson = new Gson().fromJson(pContent, ArtifactoryResultModel.class);

            List<ArtifactoryResultEntryModel> results = Arrays.asList(fromJson.getResults());
            Collections.sort(results);

            fromJson.results = (ArtifactoryResultEntryModel[]) results.toArray();

            for (ArtifactoryResultEntryModel current : fromJson.getResults()) {

                // XXX: As the Artifactory Service is not able to filter on
                // packaging level, we do it in the code.
                if (validPackaging(current.getUri(), pPackaging)) {
                    retVal.add(this.filterOutput(current.getUri(), pOutputFilter));
//                    retVal.add(current.getUri());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "failed to parse JSON returned from ArtifactoryService: '" + pContent + "'", e);
        }
        return retVal;
    }

    private String filterOutput(String pUri, String pOutputFilter) {

        StringBuilder outString = new StringBuilder();

        if (pOutputFilter.trim().equals("")) {
            return pUri;
        }

        List<Integer> partsIndexList = Arrays.stream(pOutputFilter.split("\\|"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        String[] parts = pUri.split("/");

        List<String>partsList = Arrays.stream(pUri.split("/")).collect(Collectors.toList());

        return partsIndexList.stream()
                .map( partIndex -> (partIndex < 0) ? (partsList.size()+partIndex) : partIndex )
                .filter(idx -> (idx >= 0 && partsList.size() > idx))
                .map(partsList::get)
                .collect(Collectors.joining(" | "));
    }
    private boolean validPackaging(final String pArtifactURL, String pRequestedPackaging) {
        if (StringUtils.isEmpty(pRequestedPackaging.trim())) {
            return true;
        }

        // in case the packaging is not empty, the equals has to check the given package
        return pArtifactURL.endsWith(pRequestedPackaging);
    }

}

/**
 * Helper Class to parse the JSON
 *
 * @author stephan.watermeyer, Diebold Nixdorf
 */
class ArtifactoryResultEntryModel  implements Comparable{

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

class ArtifactoryResultModel {

    @SerializedName("results")
    ArtifactoryResultEntryModel[] results = new ArtifactoryResultEntryModel[] {};

    public ArtifactoryResultModel() {
        // Important to do nothing
    }

    public ArtifactoryResultEntryModel[] getResults() {
        return results;
    }

}
