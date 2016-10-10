package services;

import play.ApplicationLoader;
import play.api.inject.BindingKey;
import play.api.inject.guice.GuiceableModule$;
import play.inject.guice.GuiceApplicationLoader;
import play.api.inject.guice.GuiceableModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DESCRIPTION
 */
public class GuiceAppLoader extends GuiceApplicationLoader {

    @Override
    protected GuiceableModule[] overrides(ApplicationLoader.Context context) {
        GuiceableModule[] modules = super.overrides(context);
        GuiceableModule module = GuiceableModule$.MODULE$.fromPlayBinding(new BindingKey<>(play.api.routing.Router.class).toProvider(GuiceRouterProvider.class).eagerly());

        List<GuiceableModule> copyModules = new ArrayList<>(Arrays.asList(modules));
        copyModules.add(module);

        return copyModules.toArray(new GuiceableModule[copyModules.size()]);
    }

}
