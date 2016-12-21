<%--suppress JspAbsolutePathInspection --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>Отрядники</title>

    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
    <!-- Custom styles for this template -->
    <link href="/css/theme.css" rel="stylesheet">

</head>
<body>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/"/>">Merra 2</a>
        </div>
    </div>
</nav>
<div class="container" role="main">
    <div class="row">
        <div class="col-xs-2 col-md-2 col-lg-2">
            <ul class="nav nav-list">
                <li class="nav-header">Навигация</li>
                <li><a href="/data/get">Загрузка данных</a></li>
                <li><a href="/data/my">Мои данные</a></li>
            </ul>
        </div>
        <div class="col-xs-10 col-md-10 col-lg-10">
            <c:choose>
                <c:when test="${not empty requestScope.includedPage}">
                    <jsp:include page="jsp/${requestScope.includedPage}.jsp"/>
                </c:when>
                <c:otherwise>
                    <h1>Solar  Web BIM, Polytec</h1><br>
                    <a href="/init" class="btn-block">Инициализировать данные</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="/js/bootstrap.min.js"></script>
</body>
</html>