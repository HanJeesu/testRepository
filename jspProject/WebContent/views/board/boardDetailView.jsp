<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.kh.board.model.vo.*"%>
<%
	Board b = (Board)request.getAttribute("board");
	Attachment at = (Attachment)request.getAttribute("attachment");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style>
    #detail-area{
        border: 2px double gray
    }
    table th,td {
        border: 1px solid gray;
    }
</style>
</head>
<body>
<%@include file = "../common/menubar.jsp" %>
<div class="outer">
    <br><br>
    <h2 align="center">일반 게시판 상세 보기</h2>
    <table id="detail-area" align="center">
    	<thead>
        <tr>
            <th width="70">카테고리</th>
            <td><%=b.getCategory() %></td>
            <td width="70" >제목</td>
            <td width="300"><%=b.getBoardTitle() %></td>
        </tr>
        <tr>
            <th>작성자</th>
            <td><%=b.getBoardWriter() %></td>
            <td>작성일</td>
            <td width="200"><%=b.getCreateDate() %></td>
        </tr>
        </thead>
        <tbody>
        <tr>
            <th>내용</th>
            <td colspan="3"><p style="height:150px"><%=b.getBoardContent() %></p></td>
        </tr>
        <tr>
            <th>첨부파일</th>
            <td colspan="3">
            <!-- 첨부 파일이 없을 경우 -->
            <%if(at==null){ %>
            	첨부파일이 없습니다.
            <%}else{ %> 
            <!-- 첨부파일이 있을 경우 -->
            <a href="<%=contextPath + at.getFilePath()+"/"+at.getChangeName()%>" download="<%=at.getOriginName()%>"><%=at.getOriginName() %></a>
            <%} %>
            </td>
        </tr>
        </tbody>
    </table>
    <br><br>
   <%if(loginUser!=null&&loginUser.getUserId().equals(b.getBoardWriter())){ %> 
	    <div align="center">
	   		 <button onclick="location.href='<%=contextPath %>/update.bo?boardNo=<%=b.getBoardNo() %>'" class="btn btn-info">수정하기</button> 
	   		 <button onclick="location.href='<%=contextPath %>/delete.bo?boardNo=<%=b.getBoardNo() %>'" class="btn btn-danger">삭제하기</button> 
	    </div>
	<%} %>
    <div align="center">
			<button type="button" onclick="history.back();">뒤로가기</button>
		</div>
		
		
		
	

</div>
</body>
</html>