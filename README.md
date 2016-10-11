# play protobuf

This library provides a way to easily handle protobuf objects as input and output in play controllers.
There is also functionality to build controllers are routes from gRPC definitions in proto files.

## Internals
Extend ProtobufParser class with your protobuf class as part of the definition.  Use it as a typical play bodyparser
See test/java/protos/com/example/tutorial/PhoneNumberParser.java as an example parser and
test/java/protos/com/example/tutorial/HomeController.java as a sample controller

## Code Generation
 Use the ServiceCodeGeneratorUtil class to generate controllers, parsers, and routes from proto files that have gRPC
 style definitions.
 Example:
 new ServiceCodeGeneratorUtil().generateClassFiles("service.proto", "app", true);
 new ServiceCodeGeneratorUtil().generateRoutesFiles("service.proto", "conf/routes");

## Testing
./sbt test

## Building
./sbt packageLocal


## Other stuff

protoc --encode tutorial.Person.PhoneNumber protos/addressbook.proto < /tmp/in > /tmp/to


protoc --decode tutorial.Person.PhoneNumber protos/addressbook.proto < /tmp/t


protoc -I=protos/ --java_out=app/ protos/addressbook.proto


protoc --plugin=protoc-gen-grpc-java=/usr/local/bin/protoc-gen-grpc-java --grpc-java_out=app --proto_path="protos" protos/service.proto

nghttpx -s -f"0.0.0.0,9001;no-tls" -b127.0.0.1,9000


java -DmodelDocs=false -Dmodels -Dapis=false  -jar swagger-codegen-cli.jar generate -i http://petstore.swagger.io/v2/swagger.json --library=jersey2 -l java -o boops
