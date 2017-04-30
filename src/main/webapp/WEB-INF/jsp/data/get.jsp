<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<jsp:useBean id="dataSetMap" class="java.util.HashMap" scope="application"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
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
            <h2>Data Sets</h2>
            <div class="row">
                <div class="col-xs-12">
                    <c:set var="dataSet" value="${dataSetMap.get(param.dataSetName)}" scope="page"/>
                    <%--<jsp:useBean id="dataSet" class="edu.spbpu.models.old_data_accessors.DataSet" scope="page"/>--%>
                    Сервер: ${dataSet.dataSource}<br>
                    Массив данных: ${dataSet.dataSetName}<br>
                    Время наблюдений: c ${dataSet.defaultTime}<br>
                    Географические ограничения <p>Широта: ${dataSet.minLatitude} .. ${dataSet.maxLatitude}<br>
                    Долгота: ${dataSet.minLongitude} .. ${dataSet.maxLongitude}</p><br>
                    <form action="/data/download" method="post">
                        Параметры: <br>
                        <c:forEach items="${dataSet.parameters}" var="parameter">
                            <input type="radio" name="parameter" value="${parameter}"> ${parameter} <br>
                        </c:forEach>
                        Широта: <input type="text" name="latitude" value=""><br>
                        Долгота: <input type="text" name="longitude" value=""><br>
                        Начало: <input type="datetime" name="timeStart"><br>
                        Конец: <input type="datetime" name="timeEnd"><br>
                        <input type="hidden" name="dataSetInfo" value="${param.dataSetName}">
                        <button type="submit" class="btn btn-default">Загрузить</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>