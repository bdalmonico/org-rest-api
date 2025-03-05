package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.Results;
import com.bruno.org.model.TareaCriteria;
import com.bruno.org.model.TareaDTO;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.TareaService;
import com.bruno.org.service.impl.TareaServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

//http://localhost:8080/org-rest-api/v1/openapi.json
@Path("/tarea")
//Singleton
public class TareaResource {

	private TareaService tareaService = null;

	public TareaResource() {
		try {
			tareaService = new TareaServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de tarea", description = "Busca tareas por su id", responses = {
			@ApiResponse(responseCode = "200", description = "Tarea encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findByTareaId(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		TareaDTO p = null;
		try {
			p = tareaService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Tarea " + id + " no encontrado").build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar tareas por critérios",
	    description = "Permite buscar tareas aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de tareas encontradas",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getTareaByCriteria(
			@Parameter(description = "Nome da tarea") @QueryParam("nombre") String nombre,
			@QueryParam("descripcion") String descripcion,
			@QueryParam("estadoId") Long estadoId, 
			@QueryParam("clienteId") Long clienteId,
			@QueryParam("empleadoId") Long empleadoId,
			@QueryParam("fechaEstimadaInicio") String fechaEstimadaInicio2,
			@QueryParam("fechaEstimadaFin") String fechaEstimadaFin2,
			@QueryParam("fechaRealInicio") String fechaRealInicio2, 
			@QueryParam("fechaRealFin") String fechaRealfin2,
			@QueryParam("proyectoId") Long proyectoId) {
		try {
//			// Criteria... 
			TareaCriteria criteria = new TareaCriteria();
			criteria.setNombre(nombre);
			criteria.setDescripcion(descripcion);
			criteria.setEstadoId(estadoId);
			criteria.setClienteId(clienteId);
			criteria.setEmpleadoId(empleadoId);
			criteria.setProyectoId(proyectoId);

			Date fechaEstimadaInicio = null;
			if (fechaEstimadaInicio2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaEstimadaInicio = sdf.parse(fechaEstimadaInicio2);
			}
			criteria.setFechaEstimadaInicio(fechaEstimadaInicio);

			Date fechaEstimadaFin = null;
			if (fechaEstimadaFin2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaEstimadaFin = sdf.parse(fechaEstimadaFin2);
			}
			criteria.setFechaEstimadaFin(fechaEstimadaFin);

			Date fechaRealInicio = null;
			if (fechaEstimadaInicio2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaEstimadaInicio = sdf.parse(fechaEstimadaInicio2);
			}
			criteria.setFechaRealInicio(fechaRealInicio);

			Date fechaRealFin = null;
			if (fechaEstimadaInicio2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaEstimadaInicio = sdf.parse(fechaEstimadaInicio2);
			}
			criteria.setFechaRealFin(fechaRealFin);

			Results<TareaDTO> resultados = tareaService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	@POST
	@Path("/crear")
	@Operation(
	    summary = "Crea una tarea",
	    description = "Crea tareas con los datos proporcionados como parámetros de consulta.",
	    responses = {
	        @ApiResponse(responseCode = "201", description = "Tarea creada exitosamente", 
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TareaDTO.class))),
	        @ApiResponse(responseCode = "400", description = "Error al recuperar los datos")
	    }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearTarea(
	    @QueryParam("nombre") @Parameter(description = "Nombre de la tarea", required = true) String nombre,
	    @QueryParam("descripcion") @Parameter(description = "Descripción de la tarea") String descripcion,
	    @QueryParam("estadoId") @Parameter(description = "ID del estado de la tarea") Long estadoId,
	    @QueryParam("proyectoId") @Parameter(description = "ID del proyecto asociado") Long proyectoId,
	    @QueryParam("fechaEstimadaInicio") @Parameter(description = "Fecha estimada de inicio (Formato: yyyy-MM-dd)") String fechaEstimadaInicio,
	    @QueryParam("fechaEstimadaFin") @Parameter(description = "Fecha estimada de finalización (Formato: yyyy-MM-dd)") String fechaEstimadaFin,
	    @QueryParam("fechaRealInicio") @Parameter(description = "Fecha real de inicio (Formato: yyyy-MM-dd)") String fechaRealInicio,
	    @QueryParam("fechaRealFin") @Parameter(description = "Fecha real de finalización (Formato: yyyy-MM-dd)") String fechaRealFin
	) {
	    try {
	        TareaDTO tarea = new TareaDTO();
	        tarea.setNombre(nombre);
	        tarea.setDescripcion(descripcion);
	        tarea.setEstadoId(estadoId);
	        tarea.setProyectoId(proyectoId);

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	        if (fechaEstimadaInicio != null && !fechaEstimadaInicio.isEmpty()) {
	            tarea.setFechaEstimadaInicio(sdf.parse(fechaEstimadaInicio));
	        }
	        if (fechaEstimadaFin != null && !fechaEstimadaFin.isEmpty()) {
	            tarea.setFechaEstimadaFin(sdf.parse(fechaEstimadaFin));
	        }
	        if (fechaRealInicio != null && !fechaRealInicio.isEmpty()) {
	            tarea.setFechaRealInicio(sdf.parse(fechaRealInicio));
	        }
	        if (fechaRealFin != null && !fechaRealFin.isEmpty()) {
	            tarea.setFechaRealFin(sdf.parse(fechaRealFin));
	        }

	        tareaService.registrar(tarea);

	        return Response.status(Response.Status.CREATED).entity(tarea).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST)
	                .entity("Error al crear la tarea: " + e.getMessage())
	                .build();
	    }
	}

	@DELETE
	@Path("/{id}")
	@Operation(
	    summary = "Eliminar tarea",
	    description = "Elimina una tarea por su ID.",
	    responses = {
	        @ApiResponse(responseCode = "204", description = "Tarea eliminada exitosamente"),
	        @ApiResponse(responseCode = "404", description = "Tarea no encontrada"),
	        @ApiResponse(responseCode = "400", description = "Error al eliminar la tarea")
	    }
	)
	public Response deleteTarea(@PathParam("id") Long id) {
	    try {
	        // Verifica se a tarea existe
	        TareaDTO tarea = tareaService.findById(id);
	        if (tarea == null) {
	            return Response.status(Status.NOT_FOUND)
	                .entity("Tarea con ID " + id + " no encontrada")
	                .build();
	        }
	        
	        // Elimina a tarea
	        tareaService.delete(id);

	        return Response.status(Status.NO_CONTENT).build(); // Status 204: No Content
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST)
	            .entity("Error al eliminar la tarea: " + e.getMessage())
	            .build();
	    }
	}

}
