<jsp:useBean id="dataSetMap" class="java.util.HashMap" scope="application"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
                    <form action="/data/get" method="get">
                        <table class="table table-bordered">
                            <thead>
                            <tr>
                                <th>Выбрать</th>
                                <th>Название</th>
                                <th>Сервер</th>
                                <th>Массив данных</th>
                                <th>Время наблюдений</th>
                                <th>Географические ограничения</th>
                                <th>Параметры</th>
                            </tr>
                            </thead>
                            <tbody>

                            <c:forEach items="${dataSetMap}" var="entry">
                                <c:set var="dataSet" value="${entry.value}" scope="page"/>
                                <%--<jsp:useBean id="dataSet" class="edu.spbpu.models.old_data_accessors.DataSet" scope="page"/>--%>
                                <tr>
                                    <td><input type="radio" name="dataSetName" value="${entry.key}"></td>
                                    <td>${entry.key}</td>
                                    <td>${dataSet.dataSource}</td>
                                    <td>${dataSet.dataSetName}</td>
                                    <td>Начиная с ${dataSet.defaultTime}</td>
                                    <td><p>Широта: ${dataSet.minLatitude} .. ${dataSet.maxLatitude}<br>
                                        Долгота: ${dataSet.minLongitude} .. ${dataSet.maxLongitude}</p></td>
                                    <td>
                                        <p><select size="1">
                                            <option>Список параметров</option>
                                            <c:forEach items="${dataSet.parameters}" var="parameter">
                                                <option>${parameter}</option>
                                            </c:forEach>
                                        </select></p>
                                    </td>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>
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