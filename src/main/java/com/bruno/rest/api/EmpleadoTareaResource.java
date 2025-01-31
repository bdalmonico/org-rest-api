package com.bruno.rest.api;

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
import com.bruno.org.model.EmpleadoTareaDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.EmpleadoTareaService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.EmpleadoTareaServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/empleadotarea")
//Singleton
public class EmpleadoTareaResource {

	private EmpleadoTareaService empleadoTareaService = null;

	public EmpleadoTareaResource() {
		try {
			empleadoTareaService = new EmpleadoTareaServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{idTarea}/{idEmpleado}")
	@GET
	@Produces
	@Operation(summary = "Busqueda de ralacion entre empleado y tarea", description = "devuelve una relacion entre empleado y tarea", responses = {
			@ApiResponse(responseCode = "200", description = "Empleado tarea encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "empleado tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })

	public Response findById(@PathParam("idTarea") Long idEmpleado, @PathParam("idEmpleado") Long idTarea)
			throws NumberFormatException, DataException, ServiceException {
		EmpleadoTareaDTO p = null;
		try {
			p = empleadoTareaService.findById(idTarea, idEmpleado);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response
					.status(Status.BAD_REQUEST.getStatusCode(), "IdEmpleado:" + idEmpleado + " idTarea:" + idTarea)
					.build();
		}

	}
	
	
	@Path("tarea/{idTarea}")
	@GET@Operation(summary = "Busqueda de empleados de una tarea", description = "Recupera todos los empleados de una tarea", responses = {
			@ApiResponse(responseCode = "200", description = "empleados de tarea encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Empleado tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Produces
	public Response findByTarea(@PathParam("idTarea") Long idTarea) throws NumberFormatException, DataException, ServiceException {
		Results<EmpleadoTareaDTO> p = null;
		try {
			p = empleadoTareaService.findByTarea(idTarea, 1, 20);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(),  " idTarea:" + idTarea).build();
		}

	}
	
	@Path("/empleado/{empleadoId}")
	@GET
	@Operation(summary = "Busqueda de tareas de un empleado", description = "Recupera todas las tareas de un empleado", responses = {
			@ApiResponse(responseCode = "200", description = "tareasd e empleado encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Empleado Tarea no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Produces
	public Response findByEmpleado(@PathParam("empleadoId") Long empleadoId)
			throws NumberFormatException, DataException, ServiceException {

		Results<EmpleadoTareaDTO> p = null;
		try {
			p = empleadoTareaService.findByEmpleado(empleadoId, 1, 20);

		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "tarea " + empleadoId + " no encontrado").build();
		}

	}

	@POST
	@Operation(summary = "Crea relacion empleadoTarea", description = "Crea una relacione entre empleado y una tarea", responses = {
			@ApiResponse(responseCode = "200", description = "empleado tarea creado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoTareaDTO.class))),
			@ApiResponse(responseCode = "404", description = "Empleado tarea no creado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	@Consumes("application/x-www-form-urlencoded")
	public Response create(MultivaluedMap<String, String> formParams) {
	    try {
	    	
	    	EmpleadoTareaDTO empleadoTarea = new EmpleadoTareaDTO();
	    	
	    	String empleadoIdStr = formParams.getFirst("empleadoId");
	        if (empleadoIdStr != null) {
	            empleadoTarea.setEmpleadoId(Long.parseLong(empleadoIdStr));
	        }
	        String tareaIdStr = formParams.getFirst("tareaId");
	        if (tareaIdStr != null) {
	            empleadoTarea.setTareaId(Long.parseLong(tareaIdStr));
	        }
	    	
	        empleadoTareaService.create(empleadoTarea);

	        return Response.status(Response.Status.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}

}
