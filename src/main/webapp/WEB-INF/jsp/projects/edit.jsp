<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="${sessionScope.language}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Solar BIM</title>

    <jsp:include page="${contextPath}/WEB-INF/jsp/common/css.jsp"/>

</head>
<body>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/navbar.jsp"/>
<div class="container" role="main">
    <div class="row">
        <jsp:include page="${contextPath}/WEB-INF/jsp/common/navigation.jsp"/>
        <c:set var="station" value="${requestScope.power_station}"/>
        <jsp:useBean id="station" class="edu.spbpu.models.PowerStation" scope="page"/>
        Редактирование станции id=<c:out value="${station.id}"/>
        <div class="col-xs-10 col-md-10 col-lg-10">
            <form action="${pageContext.request.contextPath}/projects/edit" method="POST">
                <input type="hidden" name="id" value="${station.id}">
                <div class="control-group">
                    <!-- Название станции -->
                    <label class="control-label" for="station_name">Название:</label>
                    <div class="controls">
                        <input type="text" id="station_name" name="name" placeholder="" value="${station.name}"
                               class="input-xlarge">
                    </div>
                </div>

                <div class="control-group">
                    <!-- Password-->
                    <label class="control-label" for="nominal_power">Номинальная мощность в МВт:</label>
                    <div class="controls">
                        <input type="number" id="nominal_power" name="nominal_power" value="${station.nominalPower}"
                               class="input-xlarge">
                    </div>
                </div>
                <div class="control-group">
                    <!-- Inverter-->
                    <label class="control-label" for="inverter">Инвертор:</label>
                    <div class="controls">
                        <select id="inverter" name="inverter_id">
                            <option value="">Не выбран</option>
                            <c:forEach items="${requestScope.inverter_list}" var="inverter">
                                <jsp:useBean id="inverter" type="edu.spbpu.models.Inverter" scope="page"/>
                                <option value="${inverter.id}"
                                        <c:if test="${inverter.id==station.inverter.id}">selected</c:if>
                                ><c:out value="${inverter.name}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <!-- Solar modulr-->
                    <label class="control-label" for="module">Инвертор:</label>
                    <div class="controls">
                        <select id="module" name="module_id">
                            <option value="">Не выбран</option>
                            <c:forEach items="${requestScope.module_list}" var="module">
                                <jsp:useBean id="module" type="edu.spbpu.models.SolarModule" scope="page"/>
                                <option value="${module.id}"
                                        <c:if test="${module.id==station.solarModule.id}">selected</c:if>
                                ><c:out value="${module.name}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="control-group">
                    <!-- Button -->
                    <div class="controls">
                        <button class="btn btn-success">Изменить</button>
                    </div>
                </div>
                <p><a href="${contextPath}/projects/info?id=${station.id}">На инфо-страницу</a></p>
            </form>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>