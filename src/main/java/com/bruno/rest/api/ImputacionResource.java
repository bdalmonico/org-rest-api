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
import com.bruno.org.model.ImputacionCriteria;
import com.bruno.org.model.ImputacionDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ImputacionService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ImputacionServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busca imputacion por su id", description = "Recupera imputacion por su id", responses = {
			@ApiResponse(responseCode = "200", description = "Imputacion encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ImputacionDTO.class))),
			@ApiResponse(responseCode = "404", description = "Imputacion no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ImputacionDTO p = null;
		try {
			p = imputacionService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Imputacion " + id + " no encontrado").build();
		}

	}

	@Path("/search/criteria")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getByCriteria(@QueryParam("comentario") String comentario,
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
	public Response getTotalByCriteria(@QueryParam("comentario") String comentario,
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
			@ApiResponse(responseCode = "200", description = "Imputacion creado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ImputacionDTO.class))),
			@ApiResponse(responseCode = "404", description = "Imputacion no creado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response create(MultivaluedMap<String, String> formParams) {
		try {
			ImputacionDTO imputacion = new ImputacionDTO();

			imputacion.setComentario(formParams.getFirst("comentario"));

			String empleadoIdStr = formParams.getFirst("empleadoId");
			if (empleadoIdStr != null) {
				imputacion.setEmpleadoId(Long.parseLong(empleadoIdStr));
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String fechaHoraStr = formParams.getFirst("fechaHora");
			if (fechaHoraStr != null) {
				imputacion.setFechaHora(sdf.parse(fechaHoraStr));
			}

			String horasImputadasStr = formParams.getFirst("horasImputadas");
			if (horasImputadasStr != null) {
				imputacion.setHorasImputadas(Double.parseDouble(horasImputadasStr));
			}

			String proyectoIdStr = formParams.getFirst("proyectoId");
			if (proyectoIdStr != null) {
				imputacion.setProyectoId(Long.parseLong(proyectoIdStr));
			}

			String tareaIdStr = formParams.getFirst("tareaId");
			if (tareaIdStr != null) {
				imputacion.setTareaId(Long.parseLong(tareaIdStr));
			}

			imputacionService.imputar(imputacion);

			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}
	}

}
