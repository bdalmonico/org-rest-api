package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bruno.OrganizateException;
import com.bruno.org.dao.DataException;
import com.bruno.org.model.ProyectoCriteria;
import com.bruno.org.model.ProyectoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ProyectoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ProyectoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
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

@Path("/proyecto")
//Singleton
public class ProyectoResource {

	private ProyectoService proyectoService = null;
	private static Logger logger = LogManager.getLogger(ProyectoResource.class);
	
	public ProyectoResource() {
		try {
			proyectoService = new ProyectoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(summary = "Busqueda por id de proyecto", description = "Recupera todos los datos de un proyecto por su id", responses = {
			@ApiResponse(responseCode = "200", description = "Proyecto encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProyectoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findProyectoById(@Parameter(description = "ID del producto", required = true) @PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ProyectoDTO p = null;
//		try {
//			p = proyectoService.findById(id);
//		} catch (Throwable e) {
//
//			e.printStackTrace();
//		}
//		if (p != null) {
//			return Response.ok(p).build();
//		} else {
//			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
//		}
		
		try {
			logger.info("Buscando proyecto con ID: " + id);

			// Llamar al servicio para obtener la materia prima por ID
			ProyectoDTO proyecto = proyectoService.findById(id);

			if (proyecto == null) {
				logger.warn("proyecto con ID " + id + " no encontrada.");
				return Response.status(Status.NOT_FOUND).entity("proyecto con ID " + id + " no encontrada.")
						.build();
			}

			logger.info("proyecto con ID " + id + " encontrada.");
			return Response.status(Status.OK).entity(proyecto).build();
		} catch (OrganizateException pe) {
			logger.error("Error al buscar proyecto con ID: " + id, pe);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Ha ocurrido un error interno al buscar proyecto: " + pe.getMessage()).build();
		} catch (Exception e) {
			logger.error("Error inesperado al buscar proyecto con ID: " + id, e);
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("Ha ocurrido un error inesperado al buscar proyecto: " + e.getMessage()).build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar proyectos por critérios",
	    description = "Permite buscar proyectos aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de proyectos encontrados",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getProyectoByCriteria(@QueryParam("nombre") String nombre, @QueryParam("descripcion") String descripcion,
			@QueryParam("estadoId") Long estadoId, @QueryParam("clienteId") Long clienteId,
			@QueryParam("clienteNombre") String clienteNombre,
			@QueryParam("fechaEstimadaInicio") String fechaEstimadaInicio2,
			@QueryParam("fechaEstimadaFin") String fechaEstimadaFin2,
			@QueryParam("fechaRealInicio") String fechaRealInicio2, @QueryParam("fechaRealFin") String fechaRealfin2,
			@QueryParam("importe") Double importe) {
		try {
			logger.info("descripcion:" + descripcion, "estadoId: "+estadoId, "clienteId: " + clienteId, "clienteNombre: " + clienteNombre, "fechaEstimadaInicio2: " + fechaEstimadaInicio2, "fechaEstimadaFin2: " 
						+ fechaEstimadaFin2, "fechaRealInicio2: " + fechaRealInicio2,  "fechaRealfin2: "+  fechaRealfin2, "importe:" + importe);
			
			ProyectoCriteria criteria = new ProyectoCriteria();
			criteria.setNombre(nombre);
			criteria.setDescripcion(descripcion);
			criteria.setEstadoId(estadoId);
			criteria.setClienteId(clienteId);
			criteria.setClienteNombre(clienteNombre);
			criteria.setImporte(importe);

			Date fechaEstimadaInicio = null;
			if (fechaEstimadaInicio2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				fechaEstimadaInicio = sdf.parse(fechaEstimadaInicio2);
			}
			criteria.setFechaRealInicio(fechaRealInicio);

			Date fechaRealFin = null;
			if (fechaEstimadaInicio2 != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				fechaEstimadaInicio = sdf.parse(fechaEstimadaInicio2);
			}
			criteria.setFechaRealFin(fechaRealFin);

			Results<ProyectoDTO> resultados = proyectoService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

//	@POST
//	@Path("/crear")
//	@Operation(
//	    summary = "Create proyecto",
//	    description = "Crea nuevos proyectos",
//	    responses = {
//	        @ApiResponse(responseCode = "201", description = "Proyecto creado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProyectoDTO.class))),
//	        @ApiResponse(responseCode = "400", description = "Error al recuperar los datos")
//	    }
//	)
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response crearProyecto(
//	    @QueryParam("nombre") String nombre,
//	    @QueryParam("descripcion") String descripcion,
//	    @QueryParam("estadoId") Long estadoId,
//	    @QueryParam("clienteId") Long clienteId,
//	    @QueryParam("clienteNombre") String clienteNombre,
//	    @QueryParam("importe") Double importe,
//	    @QueryParam("fechaEstimadaInicio") String fechaEstimadaInicio,
//	    @QueryParam("fechaEstimadaFin") String fechaEstimadaFin,
//	    @QueryParam("fechaRealInicio") String fechaRealInicio,
//	    @QueryParam("fechaRealFin") String fechaRealFin
//	) {
//	    try {
//	        ProyectoDTO proyecto = new ProyectoDTO();
//	        proyecto.setNombre(nombre);
//	        proyecto.setDescripcion(descripcion);
//	        proyecto.setEstadoId(estadoId);
//	        proyecto.setClienteId(clienteId);
//	        proyecto.setClienteNombre(clienteNombre);
//	        proyecto.setImporte(importe);
//
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//	        if (fechaEstimadaInicio != null) {
//	            proyecto.setFechaEstimadaInicio(sdf.parse(fechaEstimadaInicio));
//	        }
//	        if (fechaEstimadaFin != null) {
//	            proyecto.setFechaEstimadaFin(sdf.parse(fechaEstimadaFin));
//	        }
//	        if (fechaRealInicio != null) {
//	            proyecto.setFechaRealInicio(sdf.parse(fechaRealInicio));
//	        }
//	        if (fechaRealFin != null) {
//	            proyecto.setFechaRealFin(sdf.parse(fechaRealFin));
//	        }
//
//	        proyectoService.registrar(proyecto);
//
//	        return Response.status(Response.Status.CREATED).entity(proyecto).build();
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return Response.status(Status.BAD_REQUEST).entity("Error al crear el proyecto: " + e.getMessage()).build();
//	    }
//	}
	@POST
	@Path("/crear")
	@Operation(
	    summary = "Create proyecto",
	    description = "Crea nuevos proyectos",
	    responses = {
	        @ApiResponse(responseCode = "201", description = "Proyecto creado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProyectoDTO.class))),
	        @ApiResponse(responseCode = "400", description = "Error al recuperar los datos")
	    }
	)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearProyecto(
	    @FormParam("nombre") String nombre,
	    @FormParam("descripcion") String descripcion,
	    @FormParam("estadoId") String estadoIdStr,
	    @FormParam("clienteId") String clienteIdStr,
	    @FormParam("clienteNombre") String clienteNombre,
	    @FormParam("importe") String importeStr,
	    @FormParam("fechaEstimadaInicio") String fechaEstimadaInicio,
	    @FormParam("fechaEstimadaFin") String fechaEstimadaFin,
	    @FormParam("fechaRealInicio") String fechaRealInicio,
	    @FormParam("fechaRealFin") String fechaRealFin
	) {
	    try {
	        ProyectoDTO proyecto = new ProyectoDTO();
	        proyecto.setNombre(nombre);
	        proyecto.setDescripcion(descripcion);
	        proyecto.setClienteNombre(clienteNombre);

	        // Validar y convertir valores numéricos
	        Long estadoId = estadoIdStr != null ? Long.parseLong(estadoIdStr) : null;
	        Long clienteId = clienteIdStr != null ? Long.parseLong(clienteIdStr) : null;
	        Double importe = importeStr != null ? Double.parseDouble(importeStr) : null;

	        proyecto.setEstadoId(estadoId);
	        proyecto.setClienteId(clienteId);
	        proyecto.setImporte(importe);

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        if (fechaEstimadaInicio != null) {
	            proyecto.setFechaEstimadaInicio(sdf.parse(fechaEstimadaInicio));
	        }
	        if (fechaEstimadaFin != null) {
	            proyecto.setFechaEstimadaFin(sdf.parse(fechaEstimadaFin));
	        }
	        if (fechaRealInicio != null) {
	            proyecto.setFechaRealInicio(sdf.parse(fechaRealInicio));
	        }
	        if (fechaRealFin != null) {
	            proyecto.setFechaRealFin(sdf.parse(fechaRealFin));
	        }

	        proyectoService.registrar(proyecto);

	        return Response.status(Response.Status.CREATED).entity(proyecto).build();
	    } catch (NumberFormatException e) {
	        return Response.status(Status.BAD_REQUEST).entity("Formato numérico inválido en los datos de entrada.").build();
	    } catch (Exception e) {
	        return Response.status(Status.BAD_REQUEST).entity("Error al crear el proyecto: " + e.getMessage()).build();
	    }
	}



	@DELETE
	@Path("/del/{id}")
	public Response DeleteById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		try {
			proyectoService.delete(id);
			return Response.ok().build();
		} catch (Throwable e) {

			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
		}

	}

}