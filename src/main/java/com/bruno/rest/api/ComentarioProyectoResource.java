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
import com.bruno.org.model.ComentarioProyectoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ComentarioProyectoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ComentarioProyectoServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary="Busqueda por id de proyecto",
	   description="Recupera todos los datos de un proyecto por su id",
	   responses= {
			   @ApiResponse(
					   responseCode="200", 
					   description="Proyecto encontrado",
					   content=@Content(
							   	mediaType=MediaType.APPLICATION_JSON,
							   	schema=@Schema(implementation=ComentarioProyectoDTO.class)
							   )
					   ),
			   @ApiResponse(
					   responseCode="404",
					   description="Libro no encontrado"
					   ),
			   @ApiResponse(
					   responseCode="400",
					   description="Error al recuperar los datos"
					   )
	   }
)	
	public Response findById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		ComentarioProyectoDTO p = null;
		try {
			p = comentarioProyectoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Producto " + id + " no encontrado").build();
		}

	}
	
	@Path("/proyecto/{proyectoId}")
	@GET  
	@Produces
	public Response findByProyecto(@PathParam("proyectoId") Long proyectoId) throws NumberFormatException, DataException, ServiceException {
		
		Results<ComentarioProyectoDTO> p = null;
		try {
			p= comentarioProyectoService.findByProyecto(proyectoId, 1,20);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p!=null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Proyecto " + proyectoId+ " no encontrado").build();
		}

	}
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public Response create(MultivaluedMap<String, String> formParams) {
	    try {
	    	ComentarioProyectoDTO comentarioProyecto = new ComentarioProyectoDTO();
	    	
	    	comentarioProyecto.setComentario(formParams.getFirst("comentario"));
	    	
	    	String empleadoIdStr = formParams.getFirst("empleadoId");
	        if (empleadoIdStr != null) {
	            comentarioProyecto.setEmpleadoId(Long.parseLong(empleadoIdStr));
	        }

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	        String fechaHoraStr = formParams.getFirst("fechaHora");
	        if (fechaHoraStr != null) {
	            comentarioProyecto.setFechaHora(sdf.parse(fechaHoraStr));
	        }
	        
	        String proyectoIdStr = formParams.getFirst("proyectoId");
	        if (proyectoIdStr != null) {
	            comentarioProyecto.setProyectoId(Long.parseLong(proyectoIdStr));
	        }
	        
	        comentarioProyectoService.comentar(comentarioProyecto);

	        return Response.status(Response.Status.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}

}
