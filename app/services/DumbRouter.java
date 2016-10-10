package services;

import com.google.inject.Inject;
import com.google.protobuf.InvalidProtocolBufferException;
import controllers.HomeController;
import play.Logger;
import play.mvc.Controller;
import play.routing.Router;
import play.routing.RoutingDsl;
import protos.com.example.tutorial.AddressBookProtos;

/**
 * DESCRIPTION
 */
public class DumbRouter extends Controller {
    private final HomeController homeController;
    @Inject
    public DumbRouter(HomeController homeController){
        this.homeController = homeController;

    }
    public Router getRouter() {
        return new RoutingDsl().POST("/parse").routeTo(() -> {
            AddressBookProtos.Person.PhoneNumber phoneNumber = null;
            try {
                bodyParse
                phoneNumber = AddressBookProtos.Person.PhoneNumber.parseFrom(request().body().asBytes().toArray());
                return homeController.parse(phoneNumber);
            } catch (InvalidProtocolBufferException e) {
                Logger.error("Some error parsing", e);
                return badRequest();
            }
        }).build();
    }
}
