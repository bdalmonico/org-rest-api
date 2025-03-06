package com.bruno.rest.api;

import com.bruno.org.dao.DataException;
import com.bruno.org.model.ClienteCriteria;
import com.bruno.org.model.ClienteDTO;
import com.bruno.org.model.Results;
import com.bruno.org.service.ClienteService;
import com.bruno.org.service.ServiceException;
import com.bruno.org.service.impl.ClienteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.ws.rs.Consumes;
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

@Path("/cliente")
@Produces(MediaType.APPLICATION_JSON) // Todas as respostas serão JSON
public class ClienteResource {

    private ClienteService clienteService = null;

    public ClienteResource() {
        try {
            clienteService = new ClienteServiceImpl();
            System.out.println("Servicio instanciado");
        } catch (Throwable t) {
            t.printStackTrace();
            throw new WebApplicationException("Erro ao instanciar ClienteService: " + t.getMessage(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Busqueda por id de cliente", description = "Recupera todos los datos de un cliente por su id", responses = {
            @ApiResponse(responseCode = "200", description = "cliente encontrado", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ClienteDTO.class))),
            @ApiResponse(responseCode = "404", description = "no encontrado"),
            @ApiResponse(responseCode = "400", description = "Error al recuperar los datos") })
    public Response findClienteById(@PathParam("id") Long id) throws NumberFormatException, DataException, ServiceException {
        ClienteDTO p = null;
        try {
            p = clienteService.findById(id);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new WebApplicationException("Erro ao buscar cliente: " + e.getMessage(), Status.BAD_REQUEST);
        }
        if (p != null) {
            return Response.ok(p).build();
        } else {
            return Response.status(Status.NOT_FOUND).entity("Cliente " + id + " no encontrado").build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Buscar cliente por critérios",
        description = "Permite buscar cliente aplicando múltiplos critérios como nombre, estado, fechas, entre outros.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de cliente encontradas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = Results.class))
            ),
            @ApiResponse(responseCode = "400", description = "Erro ao recuperar os dados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    public Response getClienteByCriteira(@QueryParam("nombre") String nombre, @QueryParam("email") String email,
            @QueryParam("estadoId") Long estadoId, @QueryParam("nifcif") String nifCif,
            @QueryParam("clienteNombre") String clienteNombre, @QueryParam("telefone") String telefone) {
        try {
            ClienteCriteria criteria = new ClienteCriteria();
            criteria.setNombre(nombre);
            criteria.setEmail(email);
            criteria.setEstadoId(estadoId);
            criteria.setNifCif(nifCif);
            criteria.setTelefone(telefone);

            Results<ClienteDTO> resultados = clienteService.findByCriteria(criteria, 1, 30);

            return Response.ok(resultados).build();
        } catch (Exception e) {
            throw new WebApplicationException("Erro ao buscar clientes: " + e.getMessage(), Status.BAD_REQUEST);
        }
    }

    @POST
    @Operation(summary = "Crea cliente",
        description = "Crea un cliente", responses = {
            @ApiResponse(responseCode = "201", description = "cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error al crear el cliente")
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Adicionado para aceitar o formato enviado
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearCliente(
        @QueryParam("nombre") String nombre,
        @QueryParam("email") String email,
        @QueryParam("nifCif") String nifCif,
        @QueryParam("telefone") String telefone,
        @QueryParam("estadoId") Long estadoId) {
        try {
            // Validações básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                return Response.status(Status.BAD_REQUEST)
                    .entity("Nome do cliente é obrigatório")
                    .build();
            }
            if (email == null || email.trim().isEmpty()) {
                return Response.status(Status.BAD_REQUEST)
                    .entity("Email do cliente é obrigatório")
                    .build();
            }
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return Response.status(Status.BAD_REQUEST)
                    .entity("Formato de email inválido")
                    .build();
            }

            ClienteDTO cliente = new ClienteDTO();
            cliente.setNombre(nombre);
            cliente.setEmail(email.trim());
            cliente.setNifCif(nifCif != null ? nifCif.trim() : null);
            cliente.setTelefone(telefone != null ? telefone.trim() : null);
            cliente.setEstadoId(estadoId);

            clienteService.registrar(cliente);
            return Response.status(Status.CREATED)
                .entity("Cliente creado con éxito")
                .build();
        } catch (ServiceException e) {
            return Response.status(Status.BAD_REQUEST)
                .entity("Erro ao criar cliente: " + e.getMessage())
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity("Erro inesperado ao criar cliente: " + e.getMessage())
                .build();
        }
    }

    @PUT
    @Operation(
        summary = "Atualizar cliente",
        description = "Atualiza os dados de um cliente com base no ID fornecido nos query params.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao atualizar o cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCliente(
        @QueryParam("id") Long id,
        @QueryParam("nombre") String nombre,
        @QueryParam("email") String email,
        @QueryParam("nifCif") String nifCif,
        @QueryParam("telefone") String telefone,
        @QueryParam("estadoId") Long estadoId) {
        
        try {
            if (id == null) {
                return Response.status(Status.BAD_REQUEST).entity("ID do cliente é obrigatório").build();
            }

            ClienteDTO cliente = new ClienteDTO();
            cliente.setId(id);
            cliente.setNombre(nombre);
            cliente.setEmail(email);
            cliente.setNifCif(nifCif);
            cliente.setTelefone(telefone);
            cliente.setEstadoId(estadoId);

            boolean atualizado = clienteService.update(cliente);

            if (atualizado) {
                return Response.ok().entity("Cliente atualizado com sucesso").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Cliente não encontrado").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao atualizar o cliente").build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(
        summary = "Deletar cliente",
        description = "Remove um cliente do sistema com base no ID fornecido.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao deletar o cliente"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
        }
    )
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCliente(@PathParam("id") Long id) {
        try {
            boolean deletado = clienteService.delete(id);

            if (deletado) {
                return Response.ok().entity("Cliente deletado com sucesso").build();
            } else {
                return Response.status(Status.NOT_FOUND).entity("Cliente não encontrado").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar o cliente").build();
        }
    }
}