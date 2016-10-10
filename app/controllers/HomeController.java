package controllers;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import parsers.PhoneParser;
import play.Logger;
import play.api.mvc.Handler;
import play.core.routing.HandlerInvoker;
import play.inject.Injector;
import play.mvc.*;
import play.routing.Router;
import play.routing.RoutingDsl;
import protos.com.example.tutorial.AddressBookProtos;
import scala.Function0;
import utils.ProtobufResult;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok("Your new application is ready.");
    }

    public Result test() {
        AddressBookProtos.Person.PhoneNumber build = AddressBookProtos.Person.PhoneNumber.newBuilder().setNumber("555-1212").build();
        return ok(build.toByteArray());
    }

    public Router getRouter() {
        return new RoutingDsl().POST("/parse").routeTo(() -> {
            AddressBookProtos.Person.PhoneNumber phoneNumber = null;
            try {
                phoneNumber = AddressBookProtos.Person.PhoneNumber.parseFrom(request().body().asBytes().toArray());
                return parse(phoneNumber);
            } catch (InvalidProtocolBufferException e) {
                Logger.error("Some error parsing", e);
                return badRequest();
            }
        }).build();
    }

    @BodyParser.Of(BodyParser.Bytes.class)
    @ProtobufResult.Of(AddressBookProtos.Person.PhoneNumber.class)
    public Result bytes() {
            AddressBookProtos.Person.PhoneNumber phoneNumber = null;
            try {
                Logger.debug("InputParse " + request().body().asBytes());
                phoneNumber = AddressBookProtos.Person.PhoneNumber.parseFrom(request().body().asBytes().toArray());

            } catch (InvalidProtocolBufferException e) {
                Logger.error("Some error parsing", e);
                return badRequest();
            }
        return ProtobufResult.ok(phoneNumber);
    }

    @BodyParser.Of(PhoneParser.class)
    @ProtobufResult.Of(AddressBookProtos.Person.PhoneNumber.class)
    public Result parse() {
        AddressBookProtos.Person.PhoneNumber phone = request().body().as(AddressBookProtos.Person.PhoneNumber.class);
        Logger.debug(phone + "");
        return ProtobufResult.ok(phone);
    }

    public Result parse(AddressBookProtos.Person.PhoneNumber phone) {
        Logger.debug(phone + "");
        return ProtobufResult.ok(phone);
    }

}
