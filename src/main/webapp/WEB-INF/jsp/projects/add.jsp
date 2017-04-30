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
        <div class="col-xs-10 col-md-10 col-lg-10">
            Добавление нового проекта:
            <form action="${pageContext.request.contextPath}/projects/add" method="POST">
                <div class="control-group">
                    <!-- Название станции -->
                    <label class="control-label" for="station_name">Название:</label>
                    <div class="controls">
                        <input type="text" id="station_name" name="name" placeholder=""
                               class="input-xlarge">
                    </div>
                </div>
                <div class="control-group">
                    <!-- Координаты-->
                    <label class="control-label" for="latitude">Широта:</label>
                    <div class="controls">
                        <input type="text" id="latitude" name="latitude" placeholder=""
                               class="input-xlarge">
                    </div>
                    <label class="control-label" for="longitude">Долгота:</label>
                    <div class="controls">
                        <input type="text" id="longitude" name="longitude" placeholder=""
                               class="input-xlarge">
                    </div>
                    <label class="control-label" for="altitude">Превышение над уровнем моря:</label>
                    <div class="controls">
                        <input type="text" id="altitude" name="altitude" placeholder=""
                               class="input-xlarge">
                    </div>
                </div>
                <div class="control-group">
                    <!-- Password-->
                    <label class="control-label" for="nominal_power">Номинальная мощность в КВт:</label>
                    <div class="controls">
                        <input type="number" id="nominal_power" name="nominal_power" placeholder=""
                               class="input-xlarge">
                    </div>
                </div>
                <div class="control-group">
                    <!-- Password-->
                    <label class="control-label" for="zone_id">Смещение часового пояса:</label>
                    <div class="controls">
                        <input type="number" id="zone_id" name="zone_id" placeholder=""
                               class="input-xlarge">
                    </div>
                </div>
                <div class="control-group">
                    <!-- Button -->
                    <div class="controls">
                        <button class="btn btn-success">Создать</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>