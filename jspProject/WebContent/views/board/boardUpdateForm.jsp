<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.ArrayList,com.kh.board.model.vo.*"%>
<%
	ArrayList<Category> list = (ArrayList<Category>)request.getAttribute("clist");
	Board b = (Board)request.getAttribute("b");
	Attachment at = (Attachment)request.getAttribute("at");
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style>
	#update-form>table{
		border : 1px solid white;
	}
	#update-form input,textarea{
		width:100%;
		box-sizing:border-box;
	}
</style>
</head>
<body>
	<%@include file="../common/menubar.jsp" %>
	<div class="outer">
		<h2 align="center">글 수정 페이지</h2>
		
		<form action="<%=contextPath %>/update.bo" method="post" id="update-form" enctype="multipart/form-data">
			<!-- 게시글 번호 숨겨 보내기 -->
			<input type="hidden" name="boardNo" value="<%=b.getBoardNo() %>">

				<script>
				$(function(){
					//option에 있는 text와 조회해온 게시글 카테고리와 일치하는지 찾아내어 선택되어 있게 작업하기
					$("#update-form option").each(function(){
						//현재 접근된 요소객체의 text와 조회해온 카테고리가 같다면
						if($(this).text()=="<%=b.getCategory()%>"){
							//해당 요소를 선택되어 있게 만들기
							$(this).attr("selected",true);
						}
					});
				});			
				</script>
				
				
				
				<table align="center">
					<tr>
						<th width="100">카테고리</th>
						<td width="500">
							<select name="category">
								<%for(Category c : list){%>
									<option value="<%=c.getCategoryNo() %>"><%=c.getCategoryName() %></option>
								<%} %>
								<option value="10">공통</option>
								<option value="20">운동</option>
								<option value="30">등산</option>
								<option value="40">게임</option>
								<option value="50">낚시</option>
								<option value="60">요리</option>
								<option value="70">기타</option>
							</select>
						</td>
					</tr>
					
							
					<tr>
						<th>제목</th>
						<td><input type="text" name="title" value="<%=b.getBoardTitle() %>" required></td>
					</tr>
					<tr>
						<th>내용</th>
						<td>
							<textarea name="content" rows="10" cols="30" required><%=b.getBoardContent() %></textarea>
						</td>
					</tr>	
					<tr>
						<th>첨부 파일</th>
						<td>
						<%if(at!=null){ %>
							<!-- 기존 첨부파일이 있었다면 수정할 때 첨부파일 정보를 보내야 한다. -->
							<!-- 파일 번호,변경된 파일명 전달하기 -->
							<%=at.getOriginName() %>
							<input type="hidden" name="fileNo" value="<%=at.getFileNo()%>">
							<input type="hidden" name="originFileName" value="<%=at.getChangeName()%>">
							<%} %>
							<input type="file" name="reUpfile">
						</td>						
					</tr>
				</table>
				<br>
				<div align="center">
				<button type="submit">게시글 수정</button>
				<button type="reset">초기화</button>
				</div>
		</form>
	</div>
</body>
</html>