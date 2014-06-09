<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${not empty error}">
        ${error}
    </c:when>
    <c:when test="${not empty success}">
        password was changed
    </c:when>
    <c:otherwise>
        <form action="${pageContext.request.contextPath}/resetPassword" method="post">
            <label for="password">Input new password here</label><input type="text" name="password" id="password"/>
            <input type="text" name="code" hidden="hidden" value="${code}"/>
            <input type="submit"/>
        </form>
    </c:otherwise>
</c:choose>