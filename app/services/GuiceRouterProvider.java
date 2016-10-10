package services;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import controllers.HomeController;
import play.inject.Injector;
import play.routing.Router;
import play.routing.RoutingDsl;
import play.routing.RoutingDsl.*;

/**
 * DESCRIPTION
 */
@Singleton
public class GuiceRouterProvider implements Provider<play.api.routing.Router> {

    private final Router javaRouter;
    protected final Injector injector;

    @Inject
    public GuiceRouterProvider(HomeController router, Injector injector) {
        javaRouter = router.getRouter();
        this.injector = injector;
    }

    @Override
    public play.api.routing.Router get() {
        return javaRouter.asScala();
    }
}
