package com.bruno.rest.api;


import com.bruno.org.dao.DataException;
import com.bruno.org.model.ClienteCriteria;
import com.bruno.org.model.ClienteDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ClienteService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ClienteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/cliente")
//Singleton
public class ClienteResource {

	private ClienteService clienteService = null;

	public ClienteResource() {
		try {
			clienteService = new ClienteServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de cliente", description = "Recupera todos los datos de un cliente por su id", responses = {
			@ApiResponse(responseCode = "200", description = "cliente encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ClienteDTO.class))),
			@ApiResponse(responseCode = "404", description = "no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findClienteById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ClienteDTO p = null;
		try {
			p = clienteService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Cliente " + id + " no encontrado").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar cliente por critérios",
	    description = "Permite buscar cliente aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de cliente encontradas",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getClienteByCriteira(@QueryParam("nombre") String nombre, @QueryParam("email") String email,
			@QueryParam("estadoId") Long estadoId, @QueryParam("nifcif") String nifCif,
			@QueryParam("clienteNombre") String clienteNombre, @QueryParam("telefone") String telefone) {
		try {
//			// Criteria... 
			ClienteCriteria criteria = new ClienteCriteria();
			criteria.setNombre(nombre);
			criteria.setEmail(email);
			criteria.setEstadoId(estadoId);
			criteria.setNifCif(nifCif);
			criteria.setTelefone(telefone);

			Results<ClienteDTO> resultados = clienteService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	@POST
	@Operation(summary = "Crea cliente", description = "Crea un cliente", responses = {
			@ApiResponse(responseCode = "200", description = "cliente encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ClienteDTO.class))),
			@ApiResponse(responseCode = "404", description = "cliente no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response crearCliente(MultivaluedMap<String, String> formParams) {
		try {
			ClienteDTO cliente = new ClienteDTO();
			cliente.setNombre(formParams.getFirst("nombre"));
			cliente.setEmail(formParams.getFirst("email"));
			cliente.setNifCif(formParams.getFirst("nifCif"));
			cliente.setTelefone(formParams.getFirst("telefone"));

			String estadoIdStr = formParams.getFirst("estadoId");
			if (estadoIdStr != null) {
				cliente.setEstadoId(Long.parseLong(estadoIdStr));
			}
			clienteService.registrar(cliente);
			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}
	}

}
