package io.caboose.protobuf.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Model describing service
 */
public class ServiceModel {
    Optional<String> javaPackage = Optional.empty();
    Optional<String> pseudoPackage;
    String name;
    List<RPCModel> functions;

    /**
     * Default empty constructor
     */
    public ServiceModel() {
        this(Optional.empty(), Optional.empty(), null, new ArrayList<>());
    }

    /**
     * Constructor
     * @param javaPackage optional string with java package name.  used in code generation
     * @param pseudoPackage optional package name exposed to clients.  only affects routes
     * @param name name of service
     * @param functions list of functions / RPCs
     */
    public ServiceModel(Optional<String> javaPackage, Optional<String> pseudoPackage, String name, List<RPCModel> functions) {
        this.javaPackage = javaPackage;
        this.pseudoPackage = pseudoPackage;
        this.name = name;
        this.functions = functions;
    }

    public Optional<String> getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(Optional<String> javaPackage) {
        this.javaPackage = javaPackage;
    }

    public Optional<String> getPseudoPackage() {
        return pseudoPackage;
    }

    public void setPseudoPackage(Optional<String> pseudoPackage) {
        this.pseudoPackage = pseudoPackage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RPCModel> getFunctions() {
        return functions;
    }

    public void setFunctions(List<RPCModel> functions) {
        this.functions = functions;
    }
}
