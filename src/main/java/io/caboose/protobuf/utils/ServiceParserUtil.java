package io.caboose.protobuf.utils;

import io.caboose.protobuf.models.RPCModel;
import static io.caboose.protobuf.models.RPCType.*;

import io.caboose.protobuf.models.ServiceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Internal class used for parsing proto files
 */
public class ServiceParserUtil {
    private final Stream<String> lines;
    private final Pattern javaPackagePattern = Pattern.compile("^option java_package = \"([^\"]+)\";$");
    private final Pattern pseudoPackagePattern = Pattern.compile("^package ([^;]+);$");
    private final Pattern servicePattern = Pattern.compile("^service ([^ ]+) \\{$");
    private final Pattern rpcPattern = Pattern.compile("^rpc ([^ ]+) \\(([^)]+)\\) returns \\(([^)]+)\\) .*");
    private final Pattern closePattern = Pattern.compile("^(})$");
    public ServiceParserUtil(Stream<String> lines){
        this.lines = lines;
    }

    public List<ServiceModel> getServicesDefintion(){
        List<ServiceModel> services = new ArrayList<>();
        services.add(new ServiceModel());
        String[] javaPackage = {""};
        String[] pseudoPackage = {""};
        lines.forEach(line -> {
            line = line.trim().replaceAll("\\s+", " ");
            int index = services.size() - 1;
            if (javaPackage[0].isEmpty()){
                javaPackage[0] = parseJavaPackage(line).orElse("");
            }
            if (pseudoPackage[0].isEmpty()){
                pseudoPackage[0] = parsePseudoPackage(line).orElse("");
            }
            if (services.get(index).getName() == null){
                services.get(index).setName(parseService(line));
            } else {
                // service defined
                Optional<RPCModel> rpcModel = parseRpc(line);
                if (rpcModel.isPresent()){
                    services.get(index).getFunctions().add(rpcModel.get());
                } else if (parseServiceClose(line)) {
                    services.get(index).setJavaPackage(
                            Optional.ofNullable(javaPackage[0].isEmpty() ? null : javaPackage[0])
                    );
                    services.get(index).setPseudoPackage(
                            Optional.ofNullable(pseudoPackage[0].isEmpty() ? null : pseudoPackage[0])
                    );
                    services.add(new ServiceModel());
                }
            }
        });
        int lastServiceIndex = services.size() - 1;
        services.remove(lastServiceIndex);
        return services;
    }

    public Optional<RPCModel> parseRpc(String line){
        Matcher match = rpcPattern.matcher(line);
        if (match.matches()){
            RPCModel model = new RPCModel();
            model.setFunctionName(match.group(1));
            if (match.group(2).contains(" ")){
                String[] split = match.group(2).split(" ");
                model.setInputType(split[0].equalsIgnoreCase("stream") ? STREAMING : UNARY);
                model.setInput(split[1]);
            } else {
                model.setInput(match.group(2));
            }
            if (match.group(3).contains(" ")){
                String[] split = match.group(3).split(" ");
                model.setOutputType(split[0].equalsIgnoreCase("stream") ? STREAMING : UNARY);
                model.setOutput(split[1]);
            } else {
                model.setOutput(match.group(3));
            }
            return Optional.of(model);
        }
        return Optional.empty();
    }

    public boolean parseServiceClose(String line){
        return genericParse(line, closePattern).isPresent();
    }

    public String parseService(String line){
        return genericParse(line, servicePattern).orElse(null);
    }

    public Optional<String> parseJavaPackage(String line){
        return genericParse(line, javaPackagePattern);
    }

    public Optional<String> parsePseudoPackage(String line){
        return genericParse(line, pseudoPackagePattern);
    }

    protected Optional<String> genericParse(String line, Pattern pattern){
        Matcher match = pattern.matcher(line);
        if (match.matches()){
            return Optional.of(match.group(1));
        }
        return Optional.empty();
    }
}
