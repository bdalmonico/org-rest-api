package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bruno.org.model.ImputacionCriteria;
import com.bruno.org.model.ImputacionDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ImputacionService;
import com.bruno.org.service.impl.ImputacionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/imputacion")
//Singleton
public class ImputacionResource {

	private ImputacionService imputacionService = null;

	public ImputacionResource() {
		try {
			imputacionService = new ImputacionServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

//	@Path("/{id}")
//	@GET
//	@Produces
//	@Operation(summary = "Busca imputacion por su id", description = "Recupera imputacion por su id", responses = {
//			@ApiResponse(responseCode = "200", description = "Imputacion encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ImputacionDTO.class))),
//			@ApiResponse(responseCode = "404", description = "Imputacion no encontrado"),
//			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
//	public Response findHorasImputadasById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
//		ImputacionDTO p = null;
//		try {
//			p = imputacionService.findById(id);
//		} catch (Throwable e) {
//
//			e.printStackTrace();
//		}
//		if (p != null) {
//			return Response.ok(p).build();
//		} else {
//			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Imputacion " + id + " no encontrado").build();
//		}
//
//	}

	@Path("/search/criteria")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar horas imputadas por critérios",
	    description = "Permite buscar horas imputadas aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de hroas imputadas encontradas",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getHorasImputadasByCriteria(@QueryParam("comentario") String comentario,
			@QueryParam("empleadoId") Long empleadoId, @QueryParam("fechaHora2") String fechaHora2,
			@QueryParam("horasImputadas") Double horasImputadas, @QueryParam("id") Long id,
			@QueryParam("proyectoId") Long proyectoId, @QueryParam("tareaId") Long tareaId) {
		try {
			ImputacionCriteria criteria = new ImputacionCriteria();

			criteria.setComentario(comentario);
			criteria.setEmpleadoId(empleadoId);
			criteria.setId(id);
			criteria.setProyectoId(proyectoId);
			criteria.setTareaId(tareaId);

			Date fechaHora = null;
			if (fechaHora2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaHora = sdf.parse(fechaHora2);
			}
			criteria.setFechaHora(fechaHora);

			Results<ImputacionDTO> resultados = imputacionService.findByCriteria(criteria, 1, 20);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	@Path("/search/total")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTotalHorasImputadasByCriteria(@QueryParam("comentario") String comentario,
			@QueryParam("empleadoId") Long empleadoId, @QueryParam("fechaHora2") String fechaHora2,
			@QueryParam("horasImputadas") Double horasImputadas, @QueryParam("id") Long id,
			@QueryParam("proyectoId") Long proyectoId, @QueryParam("tareaId") Long tareaId) {
		try {
			ImputacionCriteria criteria = new ImputacionCriteria();

			criteria.setComentario(comentario);
			criteria.setEmpleadoId(empleadoId);
			criteria.setId(id);
			criteria.setProyectoId(proyectoId);
			criteria.setTareaId(tareaId);

			Date fechaHora = null;
			if (fechaHora2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaHora = sdf.parse(fechaHora2);
			}
			criteria.setFechaHora(fechaHora);

			Double resultados = imputacionService.findByTotalByCriteria(criteria);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	 @POST
	    @Operation(summary = "Crea una imputacion", description = "Crea una imputacion", responses = {
	        @ApiResponse(responseCode = "200", description = "Imputacion creada", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ImputacionDTO.class))),
	        @ApiResponse(responseCode = "404", description = "Imputacion no creada"),
	        @ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	    public Response crearImputacionDeHoras(@QueryParam("comentario") String comentario,
	        @QueryParam("empleadoId") Long empleadoId, @QueryParam("fechaHora") String fechaHoraStr,
	        @QueryParam("horasImputadas") Double horasImputadas, @QueryParam("proyectoId") Long proyectoId,
	        @QueryParam("tareaId") Long tareaId) {
	        try {
	            ImputacionDTO imputacion = new ImputacionDTO();

	            imputacion.setComentario(comentario);
	            imputacion.setEmpleadoId(empleadoId);

	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            if (fechaHoraStr != null) {
	                imputacion.setFechaHora(sdf.parse(fechaHoraStr));
	            }

	            imputacion.setHorasImputadas(horasImputadas);
	            imputacion.setProyectoId(proyectoId);
	            imputacion.setTareaId(tareaId);

	            imputacionService.imputar(imputacion);

	            return Response.status(Response.Status.CREATED).build();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	        }
	    }
	
	@DELETE
	@Operation(
	        summary = "Eliminar una imputación",
	        description = "Elimina una imputación a partir de su ID pasado como Query Param.",
	        responses = {
	                @ApiResponse(responseCode = "200", description = "Imputación eliminada exitosamente"),
	                @ApiResponse(responseCode = "404", description = "Imputación no encontrada"),
	                @ApiResponse(responseCode = "400", description = "Error al eliminar la imputación")
	        }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteImputacion(@QueryParam("id") Long id) {
	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST)
	                    .entity("Debe proporcionar un ID válido.")
	                    .build();
	        }

	        boolean eliminado = imputacionService.delete(id);

	        if (eliminado) {
	            return Response.ok("Imputación eliminada exitosamente.").build();
	        } else {
	            return Response.status(Status.NOT_FOUND)
	                    .entity("Imputación con ID " + id + " no encontrada.")
	                    .build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("Error al eliminar la imputación: " + e.getMessage())
	                .build();
	    }
	}

}
