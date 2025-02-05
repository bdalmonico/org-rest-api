package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.EmpleadoCriteria;
import com.bruno.org.model.EmpleadoDTO;
import com.bruno.org.model.ProyectoCriteria;
import com.bruno.org.model.ProyectoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.EmpleadoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.EmpleadoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/empleado")
//Singleton
public class EmpleadoResource {

	private EmpleadoService empleadoService = null;

	public EmpleadoResource() {
		try {
			empleadoService = new EmpleadoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de empleado", description = "Recupera todos los datos de un empleado por su id", responses = {
			@ApiResponse(responseCode = "200", description = "empleado encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findEmpleadoById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		EmpleadoDTO p = null;
		try {
			p = empleadoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Empleado " + id + " no encontrado").build();
		}

	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar empleado por critérios",
	    description = "Permite buscar empleado aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de empleados encontradas",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getEmpleadoByCriteria(@QueryParam("nombre") String nombre, @QueryParam("apellido") String apellido,
			@QueryParam("email") String email, @QueryParam("fechaEstimadaInicio") String fechaAlta2,
			@QueryParam("rolId") Integer rolId) {
		try {
			EmpleadoCriteria criteria = new EmpleadoCriteria();
			criteria.setNombre(nombre);
			criteria.setEmail(email);
			criteria.setApellido(apellido);
			criteria.setRolId(rolId);

			Date fechaAlta = null;
			if (fechaAlta2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaAlta = sdf.parse(fechaAlta2);
			}
			criteria.setFechaAlta(fechaAlta);

			Results<EmpleadoDTO> resultados = empleadoService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

}
