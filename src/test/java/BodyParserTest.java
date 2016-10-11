import akka.NotUsed;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import akka.util.ByteStringBuilder;
import org.junit.*;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.F;
import play.libs.streams.Accumulator;
import play.mvc.Http;
import play.mvc.Result;
import play.test.*;
import protos.com.example.tutorial.AddressBookProtos;
import protos.com.example.tutorial.PhoneNumberParser;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import static play.test.Helpers.*;
import static org.junit.Assert.*;
public class BodyParserTest extends WithApplication {
    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testTextOutput() throws NoSuchMethodException, ExecutionException, InterruptedException {
        AddressBookProtos.Person.PhoneNumber phoneNumber = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("555-1212").build();
        ByteString bytes = new ByteStringBuilder().putBytes(phoneNumber.toByteArray()).result();
        Http.RequestImpl header = fakeRequest().bodyRaw(phoneNumber.toByteArray()).path("/test").method("POST").header("Content-Type", "BLoops").build();
        PhoneNumberParser phoneNumberParser = app.injector().instanceOf(PhoneNumberParser.class);
        Accumulator<ByteString, F.Either<Result, AddressBookProtos.Person.PhoneNumber>> apply = phoneNumberParser.apply(header);
        Source<ByteString, NotUsed> single = Source.single(bytes);
        CompletionStage<F.Either<Result, AddressBookProtos.Person.PhoneNumber>> run = apply.run(single, mat);
        AddressBookProtos.Person.PhoneNumber output = run.toCompletableFuture().get().right.get();
        assertEquals(phoneNumber.getNumber(), output.getNumber());
    }
}
