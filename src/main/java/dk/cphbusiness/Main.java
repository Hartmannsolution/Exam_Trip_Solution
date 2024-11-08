package dk.cphbusiness;

import dk.cphbusiness.rest.ApplicationConfig;
import dk.cphbusiness.rest.RestRoutes;
import dk.cphbusiness.security.SecurityRoutes;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class Main {
    public static void main(String[] args) {
        ApplicationConfig
                .getInstance()
                .initiateServer()
//                .checkSecurityRoles() // check for role when route is called
//                .setRoute(SecurityRoutes.getSecurityRoutes())
//                .setRoute(SecurityRoutes.getSecuredRoutes())
                .setRoute(new RestRoutes().getTripRoutes())
                .startServer(7007)
                .setCORS()
                .setGeneralExceptionHandling()
                .setApiExceptionHandling();
    }
}