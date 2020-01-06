package org.jenkinsci.plugins.maven_artifact_choicelistprovider.common;


import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class OutputFilterModel{

    @SerializedName("sort")
    Integer sort;

    @SerializedName("output")
    ArrayList<Integer> output;

    @SerializedName("delimiter")
    String delimiter = ",";

    @SerializedName("split")
    String split = "/";

    public String getSplit() {
        return split;
    }

    public Integer getSort() {
        return sort;
    }

    public ArrayList<Integer> getOutput() {
        return output;
    }

    public String getDelimiter() {
        return delimiter;
    }

}
