package io.caboose.protobuf.models;

import java.util.Optional;

import static io.caboose.protobuf.models.RPCType.*;

/**
 * Model to describe functions / RPCs
 */
public class RPCModel {
    String functionName;
    String input;
    String output;
    String inputShortName;
    String outputShortName;
    Optional<String> javaPackage = Optional.empty();
    RPCType inputType = UNARY;
    RPCType outputType = UNARY;
    boolean isInputStreaming = inputType == STREAMING;
    boolean isOutputStreaming = outputType == STREAMING;

    public RPCModel(){

    }

    /**
     * RPC Constructor defaulting to UNARY input and output
     * @param functionName name for function
     * @param input classname for input
     * @param output classname for output
     */
    public RPCModel(String functionName, String input, String output) {
        this(functionName, input, output, UNARY, UNARY);
    }

    /**
     * RPC Constructor
     * @param functionName name for function
     * @param input classname for input
     * @param output classname for output
     * @param inputType input is UNARY or STREAMING (only UNARY supported)
     * @param outputType output is UNARY or STREAMING (only UNARY supported)
     */
    public RPCModel(String functionName, String input, String output, RPCType inputType, RPCType outputType) {
        this.functionName = functionName;
        this.input = input;
        this.output = output;
        this.inputType = inputType;
        this.outputType = outputType;
        this.isInputStreaming = inputType == STREAMING;
        this.isOutputStreaming = outputType == STREAMING;
        setInputShortNameFromName(input);
        setOutputShortNameFromName(output);
    }

    protected void setInputShortNameFromName(String name){
        this.inputShortName = name.substring(name.lastIndexOf('.') + 1).trim();
    }

    protected void setOutputShortNameFromName(String name){
        this.outputShortName = name.substring(name.lastIndexOf('.') + 1).trim();
    }



    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
        setInputShortNameFromName(input);
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
        setOutputShortNameFromName(output);
    }

    public RPCType getInputType() {
        return inputType;
    }

    public void setInputType(RPCType inputType) {
        this.inputType = inputType;
        this.isInputStreaming = inputType == STREAMING;
    }

    public RPCType getOutputType() {
        return outputType;
    }

    public void setOutputType(RPCType outputType) {
        this.outputType = outputType;
        this.isOutputStreaming = outputType == STREAMING;
    }

    public boolean isInputStreaming() {
        return isInputStreaming;
    }

    public boolean isOutputStreaming() {
        return isOutputStreaming;
    }


    public String getInputShortName() {
        return inputShortName;
    }

    public String getOutputShortName() {
        return outputShortName;
    }

    public Optional<String> getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(Optional<String> javaPackage) {
        this.javaPackage = javaPackage;
    }
}
