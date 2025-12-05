package cst8218.chab0109.orbgame.service;
/**
 * JAX-RS configuration class that sets the base path for all REST endpoints.
 * All REST resources will be available under the '/api' path in the application URL.
 * This class enables the RESTful web services in the application.
 */

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("api")
public class ApplicationConfig extends Application {
}