package io.caboose.protobuf.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import play.inject.Injector;
import play.routing.Router;
import io.caboose.protobuf.ProtobufRouter;

/**
 * Helper to provide routes.  Used by GuiceAppLoader only
 */
@Singleton
public class GuiceRouterProvider implements Provider<play.api.routing.Router> {

    private final Router javaRouter;
    protected final Injector injector;

    @Inject
    public GuiceRouterProvider(ProtobufRouter router, Injector injector) {
        javaRouter = router.getRouter();
        this.injector = injector;
    }

    @Override
    public play.api.routing.Router get() {
        return javaRouter.asScala();
    }
}
