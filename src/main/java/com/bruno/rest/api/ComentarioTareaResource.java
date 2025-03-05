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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
	
	@PUT
	@Operation(
	    summary = "Atualizar comentário da tarefa",
	    description = "Atualiza um comentário existente com base no ID fornecido nos query params.",
	    responses = {
	        @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso"),
	        @ApiResponse(responseCode = "400", description = "Erro ao atualizar o comentário"),
	        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateComentarioTarea(
	        @QueryParam("id") Long id,
	        @QueryParam("comentario") String comentario,
	        @QueryParam("empleadoId") Long empleadoId,
	        @QueryParam("fechaHora") String fechaHoraStr,
	        @QueryParam("tareaId") Long tareaId) {

	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST).entity("ID do comentário é obrigatório").build();
	        }

	        ComentarioTareaDTO comentarioTarea = new ComentarioTareaDTO();
	        comentarioTarea.setId(id);
	        comentarioTarea.setComentario(comentario);
	        comentarioTarea.setEmpleadoId(empleadoId);
	        comentarioTarea.setTareaId(tareaId);

	        if (fechaHoraStr != null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            comentarioTarea.setFechaHora(sdf.parse(fechaHoraStr));
	        }

	        boolean atualizado = comentarioTareaService.update(comentarioTarea);

	        if (atualizado) {
	            return Response.ok().entity("Comentário atualizado com sucesso").build();
	        } else {
	            return Response.status(Status.NOT_FOUND).entity("Comentário não encontrado").build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar o comentário").build();
	    }
	}
	
	@DELETE
	@Path("/{id}")
	@Operation(
	    summary = "Deletar comentário da tarefa",
	    description = "Remove um comentário do sistema com base no ID fornecido.",
	    responses = {
	        @ApiResponse(responseCode = "200", description = "Comentário deletado com sucesso"),
	        @ApiResponse(responseCode = "400", description = "Erro ao deletar o comentário"),
	        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComentarioTarea(@PathParam("id") Long id) {
	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST).entity("ID do comentário é obrigatório").build();
	        }

	        boolean deletado = comentarioTareaService.delete(id);

	        if (deletado) {
	            return Response.ok().entity("Comentário deletado com sucesso").build();
	        } else {
	            return Response.status(Status.NOT_FOUND).entity("Comentário não encontrado").build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar o comentário").build();
	    }
	}

}
