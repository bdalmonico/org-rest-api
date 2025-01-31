package com.bruno.rest.api;

import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.ComentarioTareaDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ComentarioTareaService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ComentarioTareaServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/comentariotarea")
//Singleton
public class ComentarioTareaResource {

	private ComentarioTareaService comentarioTareaService = null;

	public ComentarioTareaResource() {
		try {
			comentarioTareaService = new ComentarioTareaServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(
			summary = "Busqueda por id de Comentariotarea", 
			description = "Recupera todos los datos de un Comentario Tarea por su id", 
			responses = {
			@ApiResponse(responseCode = "200", description = "Comentario Tarea encontrado", 
					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
					schema = @Schema(implementation = ComentarioTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Comentario Tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ComentarioTareaDTO p = null;
		try {
			p = comentarioTareaService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "tarea " + id + " no encontrado").build();
		}

	}

	@Path("/tarea/{tareaId}")
	@GET
	@Operation(
			summary = "Busqueda de comentario por id de tarea", 
			description = "Recupera todos los datos de un cometnario por id de la Tarea", 
			responses = {
			@ApiResponse(responseCode = "200", description = "Comentario Tarea encontrado", 
					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
					schema = @Schema(implementation = ComentarioTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Comentario tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Produces
	public Response findByTarea(@PathParam("tareaId") Long tareaId)
			throws NumberFormatException, DataException, ServiceException {

		Results<ComentarioTareaDTO> p = null;
		try {
			p = comentarioTareaService.findByTarea(tareaId, 1, 20);

		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "tarea " + tareaId + " no encontrado").build();
		}

	}
	

	@POST
	@Operation(
			summary = "Crea comentario Tarea", 
			description = "Crea comentariotarea", 
			responses = {
			@ApiResponse(responseCode = "200", description = "Comentario Tarea encontrado", 
					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
					schema = @Schema(implementation = ComentarioTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Comentario tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response create(MultivaluedMap<String, String> formParams) {
		try {
			ComentarioTareaDTO comentarioTarea = new ComentarioTareaDTO();

			comentarioTarea.setComentario(formParams.getFirst("comentario"));

			String empleadoIdStr = formParams.getFirst("empleadoId");
			if (empleadoIdStr != null) {
				comentarioTarea.setEmpleadoId(Long.parseLong(empleadoIdStr));
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String fechaHoraStr = formParams.getFirst("fechaHora");
			if (fechaHoraStr != null) {
				comentarioTarea.setFechaHora(sdf.parse(fechaHoraStr));
			}

			String tareaIdStr = formParams.getFirst("tareaId");
			if (tareaIdStr != null) {
				comentarioTarea.setTareaId(Long.parseLong(tareaIdStr));
			}

			comentarioTareaService.comentar(comentarioTarea);

			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}
	}

}
