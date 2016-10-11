package protos.com.example.tutorial;
import io.caboose.protobuf.ProtobufService;
import io.caboose.protobuf.models.RPCType;
import play.mvc.*;

@ProtobufService.Service(packageName = "helloworld")
public class Greeter extends Controller {

    @ProtobufService.Output(value = AddressBookProtos.Person.PhoneNumber.class, rpcType = RPCType.UNARY)
    @ProtobufService.Input(value = AddressBookProtos.Person.PhoneNumber.class, rpcType = RPCType.UNARY)
    @BodyParser.Of(PhoneNumberParser.class)
    public Result SayHello(AddressBookProtos.Person.PhoneNumber input) {
        AddressBookProtos.Person.PhoneNumber dummy = AddressBookProtos.Person.PhoneNumber.newBuilder().build();
        return ProtobufService.ok(dummy);
    }

    //This function is used by play's internal router
    @ProtobufService.Output(value = AddressBookProtos.Person.PhoneNumber.class, rpcType = RPCType.UNARY)
    @BodyParser.Of(PhoneNumberParser.class)
    public Result SayHello() {
        AddressBookProtos.Person.PhoneNumber input = request().body().as(AddressBookProtos.Person.PhoneNumber.class);
        return SayHello(input);
    }


}


