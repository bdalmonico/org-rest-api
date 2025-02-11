<html>
<body>
    <h2>Jersey RESTful Web Application!</h2>
    <p><a href="webapi/myresource">Jersey resource</a>
    <p>Visit <a href="http://jersey.java.net">Project Jersey website</a>
    for more information on Jersey!
     
    <h2>CREAR Projeto</h2>
    <form action="${pageContext.request.contextPath}/v1/proyecto" method="post">
          
        <label>Nome:</label> 
        <input type="text" id="nombre" name="nombre" required><br><br>

        <label for="descripcion">Descrição:</label><br>
        <input type="text" id="descripcion" name="descripcion"><br><br>

        <label for="estadoId">Estado ID:</label><br>
        <input type="number" id="estadoId" name="estadoId"><br><br>

        <label for="clienteId">Cliente ID:</label><br>
        <input type="number" id="clienteId" name="clienteId"><br><br>

        <label for="clienteNombre">Nome do Cliente:</label><br>
        <input type="text" id="clienteNombre" name="clienteNombre"><br><br>

        <label for="fechaEstimadaInicio">Data Estimada de Início:</label><br>
        <input type="date" id="fechaEstimadaInicio" name="fechaEstimadaInicio"><br><br>

        <label for="fechaEstimadaFin">Data Estimada de Término:</label><br>
        <input type="date" id="fechaEstimadaFin" name="fechaEstimadaFin"><br><br>

        <label for="fechaRealInicio">Data Real de Início:</label><br>
        <input type="date" id="fechaRealInicio" name="fechaRealInicio"><br><br>

        <label for="fechaRealFin">Data Real de Término:</label><br>
        <input type="date" id="fechaRealFin" name="fechaRealFin"><br><br>

        <label for="importe">Importe:</label><br>
        <input type="number" step="0.01" id="importe" name="importe"><br><br>

        <input type="submit" value="Criar Projeto">
    </form>
  
</body>
</html>
