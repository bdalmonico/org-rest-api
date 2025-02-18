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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
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
	@Operation(summary = "Crea cliente",
			description = "Crea un cliente", responses = {
	        @ApiResponse(responseCode = "200", description = "cliente creado exitosamente"),
	        @ApiResponse(responseCode = "400", description = "Error al crear el cliente")
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearCliente(
	        @QueryParam("nombre") String nombre,
	        @QueryParam("email") String email,
	        @QueryParam("nifCif") String nifCif,
	        @QueryParam("telefone") String telefone,
	        @QueryParam("estadoId") Long estadoId) {
	    try {
	        ClienteDTO cliente = new ClienteDTO();
	        cliente.setNombre(nombre);
	        cliente.setEmail(email);
	        cliente.setNifCif(nifCif);
	        cliente.setTelefone(telefone);
	        cliente.setEstadoId(estadoId);

	        clienteService.registrar(cliente);

	        return Response.status(Response.Status.CREATED).entity("Cliente creado con éxito").build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}
	
	@PUT
	@Path("/{id}")
	@Operation(summary = "Actualizar cliente", description = "Actualiza los datos de un cliente existente", responses = {
	        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente"),
	        @ApiResponse(responseCode = "400", description = "Error al actualizar el cliente"),
	        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCliente(
	        @PathParam("id") Long id,
	        @QueryParam("nombre") String nombre,
	        @QueryParam("email") String email,
	        @QueryParam("nifCif") String nifCif,
	        @QueryParam("telefone") String telefone,
	        @QueryParam("estadoId") Long estadoId) {
	    try {
	        // Buscar cliente existente
	        ClienteDTO cliente = clienteService.findById(id);
	        if (cliente == null) {
	            return Response.status(Status.NOT_FOUND.getStatusCode(), "Cliente " + id + " no encontrado").build();
	        }

	        // Atualizar os campos se forem fornecidos
	        if (nombre != null) cliente.setNombre(nombre);
	        if (email != null) cliente.setEmail(email);
	        if (nifCif != null) cliente.setNifCif(nifCif);
	        if (telefone != null) cliente.setTelefone(telefone);
	        if (estadoId != null) cliente.setEstadoId(estadoId);

	        // Salvar as alterações
	        clienteService.update(cliente);

	        return Response.ok("Cliente actualizado con éxito").build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}

}