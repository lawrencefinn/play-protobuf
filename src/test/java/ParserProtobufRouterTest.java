import akka.util.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.caboose.protobuf.modules.GuiceAppLoader;
import org.junit.*;

import play.Application;
import play.mvc.Result;
import play.test.*;
import protos.com.example.tutorial.AddressBookProtos;

import static play.test.Helpers.*;
import static org.junit.Assert.*;


public class ParserProtobufRouterTest extends WithApplication {



    @Override
    protected Application provideApplication() {
        return new GuiceAppLoader().builder(GuiceAppLoader.Context.create(play.Environment.simple())).build();
    }

    @Test
    public void testTextOutput() {
        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("555-1212").build();
        Result result = route(fakeRequest().bodyRaw(phoneNumber.toByteArray()).path("/protos.com.example.tutorial.HomeController/index").method("POST"));
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("555-1212"));
    }

    @Test
    public void testTextOutputPseudoPackage() {
        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("555-1212").build();
        Result result = route(fakeRequest().bodyRaw(phoneNumber.toByteArray()).path("/com.butt.HomeController/index").method("POST"));
        assertEquals(OK, result.status());
        assertTrue(contentAsString(result).contains("555-1212"));
    }

    @Test
    public void testObjOutput() throws InvalidProtocolBufferException {
        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("555-1212").build();
        Result result = route(fakeRequest().bodyRaw(phoneNumber.toByteArray()).path("/protos.com.example.tutorial.HomeController/obj").method("POST"));
        assertEquals(OK, result.status());
        ByteString byteString = contentAsBytes(result);
        AddressBookProtos.Person.PhoneNumber output = AddressBookProtos.Person.PhoneNumber.parseFrom(byteString.toArray());
        assertTrue(output.getNumber().equals("555-1212wee"));
    }

}
