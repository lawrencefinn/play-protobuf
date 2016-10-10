package parsers;

import ch.qos.logback.classic.Logger;
import com.google.inject.Inject;
import play.api.http.HttpConfiguration;
import play.http.HttpErrorHandler;
import protos.com.example.tutorial.AddressBookProtos;

/**
 * DESCRIPTION
 */
public class PhoneParser extends ProtobufParser<AddressBookProtos.Person.PhoneNumber> {

    @Override
    protected Class<AddressBookProtos.Person.PhoneNumber> getClazz() {
        return AddressBookProtos.Person.PhoneNumber.class;
    }

    @Inject
    protected PhoneParser(HttpConfiguration httpConfiguration, HttpErrorHandler errorHandler, String errorMessage
                          ) {
        super(httpConfiguration, errorHandler, errorMessage);
    }

}
