package com.bruno.rest.api;

import java.text.SimpleDateFormat;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.ComentarioProyectoDTO;
import com.bruno.org.model.ComentarioTareaDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ComentarioProyectoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ComentarioProyectoServiceImpl;

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

@Path("/comentarioproyecto")
//Singleton
public class ComentarioProyectoResource {

	private ComentarioProyectoService comentarioProyectoService = null;

	public ComentarioProyectoResource() {
		try {
			comentarioProyectoService = new ComentarioProyectoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/proyecto/{proyectoId}")
	@GET
	@Operation(
			summary = "Busqueda por id de proyecto", 
			description = "Recupera todos los datos de un comentario por su id de Proyecto", 
			responses = {
			@ApiResponse(responseCode = "200", description = "comentario proyecto encontrado", 
					content = @Content(mediaType = MediaType.APPLICATION_JSON, 
					schema = @Schema(implementation = ComentarioTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "comentario proyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Produces
	public Response findComentarioByProyecto(@PathParam("proyectoId") Long proyectoId)
			throws NumberFormatException, DataException, ServiceException {

		Results<ComentarioProyectoDTO> p = null;
		try {
			p = comentarioProyectoService.findByProyecto(proyectoId, 1, 20);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Proyecto " + proyectoId + " no encontrado")
					.build();
		}

	}

	@POST
	@Operation(summary = "Crea ComentarioProyecto", description = "Crea un ComentarioProyecto usando Query Params", responses = {
	        @ApiResponse(responseCode = "201", description = "ComentarioProyecto creado exitosamente"),
	        @ApiResponse(responseCode = "400", description = "Error al crear el comentario")
	})
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearComentarioProyecto(
	        @QueryParam("comentario") String comentario,
	        @QueryParam("empleadoId") Long empleadoId,
	        @QueryParam("fechaHora") String fechaHoraStr,
	        @QueryParam("proyectoId") Long proyectoId) {
	    try {
	        ComentarioProyectoDTO comentarioProyecto = new ComentarioProyectoDTO();
	        comentarioProyecto.setComentario(comentario);
	        comentarioProyecto.setEmpleadoId(empleadoId);
	        comentarioProyecto.setProyectoId(proyectoId);

	        // Convertendo a string da data para um objeto Date
	        if (fechaHoraStr != null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            comentarioProyecto.setFechaHora(sdf.parse(fechaHoraStr));
	        }

	        comentarioProyectoService.comentar(comentarioProyecto);

	        return Response.status(Response.Status.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}

	@PUT
	@Operation(
	    summary = "Atualizar comentário do projeto",
	    description = "Atualiza um comentário existente com base no ID fornecido nos query params.",
	    responses = {
	        @ApiResponse(responseCode = "200", description = "Comentário atualizado com sucesso"),
	        @ApiResponse(responseCode = "400", description = "Erro ao atualizar o comentário"),
	        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateComentarioProyecto(
	        @QueryParam("id") Long id,
	        @QueryParam("comentario") String comentario,
	        @QueryParam("empleadoId") Long empleadoId,
	        @QueryParam("fechaHora") String fechaHoraStr,
	        @QueryParam("proyectoId") Long proyectoId) {

	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST).entity("ID do comentário é obrigatório").build();
	        }

	        ComentarioProyectoDTO comentarioProyecto = new ComentarioProyectoDTO();
	        comentarioProyecto.setId(id);
	        comentarioProyecto.setComentario(comentario);
	        comentarioProyecto.setEmpleadoId(empleadoId);
	        comentarioProyecto.setProyectoId(proyectoId);

	        if (fechaHoraStr != null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            comentarioProyecto.setFechaHora(sdf.parse(fechaHoraStr));
	        }

	        boolean atualizado = comentarioProyectoService.update(comentarioProyecto);

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
	    summary = "Deletar comentário do projeto",
	    description = "Remove um comentário do sistema com base no ID fornecido.",
	    responses = {
	        @ApiResponse(responseCode = "200", description = "Comentário deletado com sucesso"),
	        @ApiResponse(responseCode = "400", description = "Erro ao deletar o comentário"),
	        @ApiResponse(responseCode = "404", description = "Comentário não encontrado"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComentarioProyecto(@PathParam("id") Long id) {
	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST).entity("ID do comentário é obrigatório").build();
	        }

	        boolean deletado = comentarioProyectoService.delete(id);

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
