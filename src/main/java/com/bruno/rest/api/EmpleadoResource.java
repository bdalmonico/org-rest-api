package com.bruno.rest.api;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.EmpleadoCriteria;
import com.bruno.org.model.EmpleadoDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.EmpleadoService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.EmpleadoServiceImpl;

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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/empleado")
//Singleton
public class EmpleadoResource {

	private EmpleadoService empleadoService = null;

	public EmpleadoResource() {
		try {
			empleadoService = new EmpleadoServiceImpl();
			System.out.println("Servicio instanciado");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	@Path("/{id}")
	@GET
	@Produces
	@Operation(summary = "Busqueda por id de empleado", description = "Recupera todos los datos de un empleado por su id", responses = {
			@ApiResponse(responseCode = "200", description = "empleado encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = EmpleadoDTO.class))),
			@ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
			@ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
	public Response findEmpleadoById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
		EmpleadoDTO p = null;
		try {
			p = empleadoService.findById(id);
		} catch (Throwable e) {

			e.printStackTrace();
		}
		if (p != null) {
			return Response.ok(p).build();
		} else {
			return Response.status(Status.BAD_REQUEST.getStatusCode(), "Empleado " + id + " no encontrado").build();
		}

	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Operation(
	    summary = "Buscar empleado por critérios",
	    description = "Permite buscar empleado aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
	    responses = {
	        @ApiResponse(
	            responseCode = "200",
	            description = "Lista de empleados encontradas",
	            content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
	        ),
	        @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
	        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
	    }
	)
	public Response getEmpleadoByCriteria(@QueryParam("nombre") String nombre, @QueryParam("apellido") String apellido,
			@QueryParam("email") String email, @QueryParam("fechaEstimadaInicio") String fechaAlta2,
			@QueryParam("rolId") Integer rolId) {
		try {
			EmpleadoCriteria criteria = new EmpleadoCriteria();
			criteria.setNombre(nombre);
			criteria.setEmail(email);
			criteria.setApellido(apellido);
			criteria.setRolId(rolId);

			Date fechaAlta = null;
			if (fechaAlta2 != null) {
				// Converter a String para Date
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Defina o formato desejado
				fechaAlta = sdf.parse(fechaAlta2);
			}
			criteria.setFechaAlta(fechaAlta);

			Results<EmpleadoDTO> resultados = empleadoService.findByCriteria(criteria, 1, 30);

			return Response.ok(resultados).build();
		} catch (Exception e) {
			new WebApplicationException();
			return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
		}

	}

	@POST
	@Operation(
	        summary = "Crea un nuevo empleado",
	        description = "Permite crear un empleado enviando datos a través de Query Params.",
	        responses = {
	                @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
	                @ApiResponse(responseCode = "400", description = "Error en la creación del empleado")
	        }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response crearEmpleado(
	        @QueryParam("nombre") String nombre,
	        @QueryParam("apellido") String apellido,
	        @QueryParam("email") String email,
	        @QueryParam("fechaAlta") String fechaAltaStr,
	        @QueryParam("rolId") Integer rolId) {
	    try {
	        EmpleadoDTO empleado = new EmpleadoDTO();
	        empleado.setNombre(nombre);
	        empleado.setApellido(apellido);
	        empleado.setEmail(email);
	        empleado.setRolId(rolId);
	        

	        // Convertendo a string da data para um objeto Date
	        if (fechaAltaStr != null) {
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	            Date fechaAlta = sdf.parse(fechaAltaStr);
	            empleado.setFechaAlta(fechaAlta);
	        }

	        empleadoService.registrar(empleado);

	        return Response.status(Response.Status.CREATED).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
	    }
	}

	@DELETE
	@Operation(
	        summary = "Eliminar un empleado",
	        description = "Elimina un empleado a partir de su ID pasado como Query Param.",
	        responses = {
	                @ApiResponse(responseCode = "200", description = "Empleado eliminado exitosamente"),
	                @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
	                @ApiResponse(responseCode = "400", description = "Error al eliminar el empleado")
	        }
	)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEmpleado(@QueryParam("id") Long id) {
	    try {
	        if (id == null) {
	            return Response.status(Status.BAD_REQUEST)
	                    .entity("Debe proporcionar un ID válido.")
	                    .build();
	        }

	        boolean eliminado = empleadoService.delete(id);

	        if (eliminado) {
	            return Response.ok("Empleado eliminado exitosamente.").build();
	        } else {
	            return Response.status(Status.NOT_FOUND)
	                    .entity("Empleado con ID " + id + " no encontrado.")
	                    .build();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Status.INTERNAL_SERVER_ERROR)
	                .entity("Error al eliminar el empleado: " + e.getMessage())
	                .build();
	    }
	}
	

@PUT
@Operation(
    summary = "Atualizar dados de um empregado",
    description = "Atualiza um empregado existente com base no ID fornecido.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Empregado atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Erro ao atualizar o empregado"),
        @ApiResponse(responseCode = "404", description = "Empregado não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    }
)
@Produces(MediaType.APPLICATION_JSON)
public Response updateEmpleado(
        @QueryParam("id") Long id,
        @QueryParam("nombre") String nombre,
        @QueryParam("apellido") String apellido,
        @QueryParam("email") String email,
        @QueryParam("fechaAlta") String fechaAltaStr,
        @QueryParam("rolId") Integer rolId,
        @QueryParam("contrasena") String contrasena) { // Agora aceita contraseña

    try {
        if (id == null) {
            return Response.status(Status.BAD_REQUEST).entity("ID do empregado é obrigatório").build();
        }

        EmpleadoDTO empleado = new EmpleadoDTO();
        empleado.setId(id);
        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setEmail(email);
        empleado.setRolId(rolId);

        if (fechaAltaStr != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaAlta = sdf.parse(fechaAltaStr);
            empleado.setFechaAlta(fechaAlta);
        }

        if (contrasena != null && !contrasena.isEmpty()) {
            empleado.setContrasena(contrasena); // Atualiza a senha se fornecida
        }

        boolean atualizado = empleadoService.update(empleado);

        if (atualizado) {
            return Response.ok().entity("Empregado atualizado com sucesso").build();
        } else {
            return Response.status(Status.NOT_FOUND).entity("Empregado não encontrado").build();
        }
    } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar o empregado").build();
    }
}

}
