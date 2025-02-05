package com.bruno.rest.api;

import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;

@OpenAPIDefinition(
	    info = @Info(
	        title = "Organizate REST API",
	        version = "1.0",
	        description = "API para comprar libros ",
	        contact = @Contact(
	            name = "Soporte API",
	            email = "soporte@organizate.com",
	            url = "https://organizate.com"
	        ),
	        license = @License(
	            name = "MIT",
	            url = "https://opensource.org/licenses/MIT"
	        )
	    ),
	    servers = {
	    		@Server(url = "http://localhost:8080/org-rest-api/", description = "Servidor Local")
	    		// Cuando lo subais al hosting
	    		// @Server(url = "https://api.thegoldenbook.com", description = "Servidor de Producción"),

	    }
)

@ApplicationPath("/v1")
public class OrgRestApplication extends ResourceConfig {
	public OrgRestApplication() {
		packages(OrgRestApplication.class.getPackage().getName());
		
		register(io.swagger.v3.jaxrs2.integration.resources.OpenApiResource.class);
	}
}
