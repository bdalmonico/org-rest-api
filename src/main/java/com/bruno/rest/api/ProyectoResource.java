package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import com.bruno.org.model.ProyectoCriteria;
import com.bruno.org.model.ProyectoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ProyectoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ProyectoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/proyecto")
//Singleton
public class ProyectoResource {

	private ProyectoService proyectoService = null;

	public ProyectoResource() {
		try {
			proyectoService = new ProyectoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de proyecto", description = "Recupera todos los datos de un proyecto por su id", responses = {
			@ApiResponse(responseCode = "200", description = "Proyecto encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProyectoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findProyectoById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ProyectoDTO p = null;
		try {
			p = proyectoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
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

	@POST
	@Operation(summary = "Create proyecto", description = "Crea nuevos proyectos", responses = {
			@ApiResponse(responseCode = "200", description = "Proyecto creados ¡", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProyectoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Proyecto no creados"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response crearProyecto(MultivaluedMap<String, String> formParams) {
		try {
			ProyectoDTO proyecto = new ProyectoDTO();
			proyecto.setNombre(formParams.getFirst("nombre"));
			proyecto.setDescripcion(formParams.getFirst("descripcion"));

			String estadoIdStr = formParams.getFirst("estadoId");
			if (estadoIdStr != null) {
				proyecto.setEstadoId(Long.parseLong(estadoIdStr));
			}

			String clienteIdStr = formParams.getFirst("clienteId");
			if (clienteIdStr != null) {
				proyecto.setClienteId(Long.parseLong(clienteIdStr));
			}

			proyecto.setClienteNombre(formParams.getFirst("clienteNombre"));
			proyecto.setImporte(Double.parseDouble(formParams.getFirst("importe")));

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String fechaEstimadaInicioStr = formParams.getFirst("fechaEstimadaInicio");
			if (fechaEstimadaInicioStr != null) {
				proyecto.setFechaEstimadaInicio(sdf.parse(fechaEstimadaInicioStr));
			}

			String fechaEstimadaFinStr = formParams.getFirst("fechaEstimadaFin");
			if (fechaEstimadaFinStr != null) {
				proyecto.setFechaEstimadaFin(sdf.parse(fechaEstimadaFinStr));
			}

			String fechaRealInicioStr = formParams.getFirst("fechaRealInicio");
			if (fechaRealInicioStr != null) {
				proyecto.setFechaRealInicio(sdf.parse(fechaRealInicioStr));
			}

			String fechaRealFinStr = formParams.getFirst("fechaRealFin");
			if (fechaRealFinStr != null) {
				proyecto.setFechaRealFin(sdf.parse(fechaRealFinStr));
			}

			proyectoService.registrar(proyecto);

			return Response.status(Response.Status.CREATED).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
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