package com.bruno.rest.api;

import java.text.SimpleDateFormat;

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
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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

//	@Path("/{id}")
//	@GET
//	@Produces
//	@Operation(
//			summary = "Busqueda por id de Comentariotarea", 
//			description = "Recupera todos los datos de un Comentario Tarea por su id", 
//			responses = {
//			@ApiResponse(responseCode = "200", description = "Comentario Tarea encontrado", 
//					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
//					schema = @Schema(implementation = ComentarioTareaDTO.class))),
//			@ApiResponse(responseCode = "404", description = "Comentario Tarea no encontrado"),
//			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
//	public Response findComentarioTareaById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
//		ComentarioTareaDTO p = null;
//		try {
//			p = comentarioTareaService.findById(id);
//		} catch (Throwable e) {
//
//			e.printStackTrace();
//		}
//		if (p != null) {
//			return Response.ok(p).build();
//		} else {
//			return Response.status(Status.BAD_REQUEST.getStatusCode(), "tarea " + id + " no encontrado").build();
//		}
//
//	}

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
	public Response findComentarioByTarea(@PathParam("tareaId") Long tareaId)
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
	        description = "Crea un comentario en una tarea usando Query Params",
	        responses = {
	                @ApiResponse(responseCode = "201", description = "Comentario Tarea creado exitosamente"),
	                @ApiResponse(responseCode = "400", description = "Error al crear el comentario")
	        }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearComentarioTarea(
	        @QueryParam("comentario") String comentario,
	        @QueryParam("empleadoId") Long empleadoId,
	        @QueryParam("fechaHora") String fechaHoraStr,
	        @QueryParam("tareaId") Long tareaId) {
	    try {
	        ComentarioTareaDTO comentarioTarea = new ComentarioTareaDTO();
	        comentarioTarea.setComentario(comentario);
	        comentarioTarea.setEmpleadoId(empleadoId);
	        comentarioTarea.setTareaId(tareaId);

	        // Convertendo a string da data para um objeto Date
	        if (fechaHoraStr != null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            comentarioTarea.setFechaHora(sdf.parse(fechaHoraStr));
	        }

	        comentarioTareaService.comentar(comentarioTarea);

	        return Response.status(Response.Status.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}


}
