package com.bruno.rest.api;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.EstadoDTO;
import com.bruno.org.service.EstadoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.EstadoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/estado")
//Singleton
public class EstadoResource {

	private EstadoService estadoService = null;

	public EstadoResource() {
		try {
			estadoService = new EstadoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busca estados por su id ", description = "Recupera todos os estados por su id", responses = {
			@ApiResponse(responseCode = "200", description = "Estado encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EstadoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Estado no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findEstadoById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		EstadoDTO p = null;
		try {
			p = estadoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
		}

	}

}
