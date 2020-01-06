package org.jenkinsci.plugins.maven_artifact_choicelistprovider.common;

import org.jenkinsci.plugins.maven_artifact_choicelistprovider.artifactory.ArtifactoryResultEntryModel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VersionNumberComparator implements Comparator<ArtifactoryResultEntryModel> {

    private OutputFilterModel joOutputFilter;

    public VersionNumberComparator(OutputFilterModel joOutputFilter){
        this.joOutputFilter = joOutputFilter;
    }

    @Override
    public int compare(ArtifactoryResultEntryModel uri1, ArtifactoryResultEntryModel uri2) {


        if (joOutputFilter != null && joOutputFilter.getSort() != null) {

            List<String> partsList1 = Arrays.stream(uri1.getUri().split(Pattern.quote(joOutputFilter.getSplit()))).collect(Collectors.toList());
            List<String> partsList2 = Arrays.stream(uri2.getUri().split(Pattern.quote(joOutputFilter.getSplit()))).collect(Collectors.toList());

            String version1 = partsList1.get( (joOutputFilter.getSort()<0)?partsList1.size()+joOutputFilter.getSort():joOutputFilter.getSort() );
            String version2 = partsList2.get( (joOutputFilter.getSort()<0)?partsList2.size()+joOutputFilter.getSort():joOutputFilter.getSort() );

            return this.compareVersions(version1,version2);

        }

        return uri1.compareTo(uri2);
    }

    Integer compareVersions(String versionBase, String versionCompare) {
        String[] arr1 = versionBase.split("\\.");
        String[] arr2 = versionCompare.split("\\.");

        try {

            int i = 0;
            while (i < arr1.length || i < arr2.length) {
                if (i < arr1.length && i < arr2.length) {
                    if (Integer.parseInt(arr1[i]) < Integer.parseInt(arr2[i])) {
                        return -1;
                    } else if (Integer.parseInt(arr1[i]) > Integer.parseInt(arr2[i])) {
                        return 1;
                    } else if (Integer.parseInt(arr1[i]) == Integer.parseInt(arr2[i])) {
                        int result = specialCompare(versionBase, versionCompare);
                        if (result != 0) {
                            return result;
                        }
                    }
                } else if (i < arr1.length) {
                    if (Integer.parseInt(arr1[i]) != 0) {
                        return 1;
                    }
                } else if (i < arr2.length) {
                    if (Integer.parseInt(arr2[i]) != 0) {
                        return -1;
                    }
                }

                i++;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return 0;
    }

    private static int specialCompare(String str1, String str2) {
        String[] arr1 = str1.split("\\.");
        String[] arr2 = str2.split("\\.");
        for (int i = 1; i < arr1.length; i++) {
            if (Integer.parseInt(arr1[i]) != 0) {
                return 0;
            }
        }
        for (int j = 1; j < arr2.length; j++) {
            if (Integer.parseInt(arr2[j]) != 0) {
                return 0;
            }
        }
        if (arr1.length < arr2.length) {
            return -1;
        } else {
            return 1;
        }
    }
}
