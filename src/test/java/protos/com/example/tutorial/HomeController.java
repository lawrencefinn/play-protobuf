package protos.com.example.tutorial;

import io.caboose.protobuf.ProtobufService;
import io.caboose.protobuf.utils.ServiceCodeGeneratorUtil;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@ProtobufService.Service(packageName = "com.butt")
public class HomeController extends Controller {


    @BodyParser.Of(PhoneNumberParser.class)
    @ProtobufService.Output(AddressBookProtos.Person.PhoneNumber.class)
    public Result index() throws IOException {
        //ServiceCodeGeneratorUtil.generateClassFiles("service.proto", "app", true);
        //ServiceCodeGeneratorUtil.generateClassFiles("Greeter.proto", "app", true);
        //ServiceCodeGeneratorUtil.generateProtoFiles(Greeter.class, "protos");

        //ServiceCodeGeneratorUtil.generateRoutesFiles("service.proto", "conf/routes");
        System.out.println(request().body().as(AddressBookProtos.Person.PhoneNumber.class));
        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.parseFrom(request().body().asBytes().toArray());

        System.out.println("Phone " + phoneNumber);

        return index(request().body().as(AddressBookProtos.Person.PhoneNumber.class));
    }

    @ProtobufService.Output(AddressBookProtos.Person.PhoneNumber.class)
    @ProtobufService.Input(AddressBookProtos.Person.PhoneNumber.class)
    public Result index(AddressBookProtos.Person.PhoneNumber input) throws IOException {
        return ok(input.getNumber());
    }

    @BodyParser.Of(PhoneNumberParser.class)
    @ProtobufService.Output(AddressBookProtos.Person.PhoneNumber.class)
    public Result obj() throws IOException {

        return obj(request().body().as(AddressBookProtos.Person.PhoneNumber.class));
    }

    @ProtobufService.Output(AddressBookProtos.Person.PhoneNumber.class)
    @ProtobufService.Input(AddressBookProtos.Person.PhoneNumber.class)
    public Result obj(AddressBookProtos.Person.PhoneNumber input) throws IOException {
        AddressBookProtos.Person.PhoneNumber output =
                AddressBookProtos.Person.PhoneNumber.newBuilder(input).setNumber(input.getNumber() + "wee").build();
        return ProtobufService.ok(output);
    }



}
