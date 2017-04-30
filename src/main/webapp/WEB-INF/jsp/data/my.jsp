<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<c:set var="dataSeriesList" value="${sessionScope.get('arrayOfDataSet')}" scope="page"/>
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
                    <table class="table table-bordered">
                        <thead>
                        <tr>
                            <th>№</th>
                            <th>DataSet</th>
                            <th>Параметр</th>
                            <th>Широта</th>
                            <th>Долгота</th>
                            <th>Начало</th>
                            <th>Конец</th>
                            <th>Статус</th>
                            <th>Ссылка</th>
                        </tr>
                        </thead>
                        <tbody>

                        <c:forEach items="${dataSeriesList}" var="dataSeries">

                            <%--<jsp:useBean id="dataSeries" class="edu.spbpu.models.old_data_accessors.DataSeries" scope="page"/>--%>
                            <tr>
                                <td>${dataSeriesList.indexOf(dataSeries)+1}</td>
                                <td>${dataSeries.dataSet.dataSetName}</td>
                                <td>${dataSeries.parameter}</td>
                                <td>${dataSeries.latitude}</td>
                                <td>${dataSeries.longitude}</td>
                                <td>${dataSeries.timeStart}</td>
                                <td>${dataSeries.timeEnd}</td>
                                <td><c:if test="${empty dataSeries.dataMap}">Идёт загрузка</c:if>
                                    <c:if test="${not empty dataSeries.dataMap}">
                                        <form action="/data/get_file">
                                            <input type="hidden" name="index"
                                                   value="${dataSeriesList.indexOf(dataSeries)}">
                                            <input type="submit" value="Данные загружены">
                                        </form>
                                    </c:if></td>
                                <td><input maxlength="25" size="40" value="${dataSeries.getLink()}"></td>
                            </tr>
                        </c:forEach>

                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<jsp:include page="${contextPath}/WEB-INF/jsp/common/scripts.jsp"/>
</body>
</html>
