package protos.com.example.tutorial;

import com.google.inject.Inject;
import io.caboose.protobuf.parsers.ProtobufParser;
import play.api.http.HttpConfiguration;
import play.http.HttpErrorHandler;

public class PhoneNumberParser extends ProtobufParser<AddressBookProtos.Person.PhoneNumber> {

    @Override
    protected Class<AddressBookProtos.Person.PhoneNumber> getClazz() {
        return AddressBookProtos.Person.PhoneNumber.class;
    }

    @Inject
    protected PhoneNumberParser(HttpConfiguration httpConfiguration, HttpErrorHandler errorHandler, String errorMessage
                          ) throws NoSuchMethodException {
        super(httpConfiguration, errorHandler, errorMessage);
    }

}
