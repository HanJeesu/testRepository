package com.kh.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import com.kh.board.model.service.BoardService;
import com.kh.board.model.vo.Attachment;
import com.kh.board.model.vo.Board;
import com.kh.board.model.vo.Category;
import com.kh.common.MyFileRenamePolicy;
import com.oreilly.servlet.MultipartRequest;

/**
 * Servlet implementation class BoardInsertController
 */
@WebServlet("/insert.bo")
public class BoardInsertController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardInsertController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//카테고리 목록 조회해오기
		ArrayList<Category> list = new BoardService().categoryList();
		
		request.setAttribute("clist", list);
		
		request.getRequestDispatcher("views/board/boardEnrollform.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		/*
		 * form에서 multipart/form-data형식으로 전송했기 때문에 기존 request의 parameter영역에서 
		 * 꺼낼 수 없다. 특정 라이브러리를 사용하여 전달받아야 함
		String category = request.getParameter("category");
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String file = request.getParameter("file");
		int userNo = Integer.parseInt(request.getParameter("userNo"));
		
//		System.out.println(category);
//		System.out.println(title);
//		System.out.println(content);
//		System.out.println(file);
//		System.out.println(userNo);
		*/
		
		//cos.jar 라이브러리 추가 후 작업하기
		//form 전송 방식이 일반 방식이 아닌 multipart/form-data방식이라면 
		//request를 multipart객체로 이관시켜서 다뤄야 한다.
		
		//enctype이 multipart로 작성되어 넘어왔을 경우에만 수정이 되도록
		if(ServletFileUpload.isMultipartContent(request)) {
			//파일 업로드를 위한 라이브러리 cos.jar
			
			//1.전송되는 파일을 처리할 작업 내용(용량제한,전달된 파일을 저장할 경로 설정)
			//1_1.용량 제한하기(10Mbyte)
			/*
			 * byte - kbyte - mbyte - gbyte- tbyte
			 * */
			int maxSize = 10 * 1024 * 1024;
			
			//1_2.전송된 파일을 저장할 서버의 폴더 경로 알아내기
			/*
			 * 세션 객체에서 제공하는 getRealPath 메소드를 사용
			 * WebContent에 board_files폴더 경로까지는 잡아주어야 한다. 해당 폴더에 저장될 것이기 때문에
			 * */
			//경로 마지막에 /붙여주기(그 폴더 안에 저장할 것이기 때문)
			String savePath = request.getSession().getServletContext().getRealPath("/resources/board_files/");
//			System.out.println(savePath);
			
			/*
			 * 2.전달된 파일명 수정 및 서버에 업로드 작업
			 * -HttpServletRequest request -> MulipartRequest multiRequest로 변환하기
			 * 
			 * 매개변수 생성자로 생성
			 * MultipartRequest multiRequest = new MultipartRequest(request객체,저장할 폴더 경로,용량제한,인코딩값,파일명 수정 객체);
			 *
			 * 위 구문 한 줄이 실행되는 순간 지정한 경로의 파일이 업로드 된다.
			 * 사용자가 올린 파일명은 그대로 해당 폴더에 업로드하지 않는 것이 일반적 - 같은 파일 명이 있을 경우 덮어씌워질 경우도 있고
			 * 한글/특수문자가 포함된 파일명 경우는 업로드되는 서버에 따라 오류를 발생시킬 여지가 있기 때문ㅇ
			 * 
			 * 기본적으로 파일명을 변경해주는 객체를 제공한다.
			 * DefaultFileRenamePoligy 객체
			 * 내부적으로 rename()이라는 메소드가 실행되며 파일명 수정을 해준다.
			 * 기본적으로 제공된 객체는 파일명 수정을 파일명 뒤에 숫자를 카운팅하는 형식으로만 진행해준다.
			 * ex)hello.jpg,hello1.jpg...
			 * 
			 * 해당 rename작업을 따로 정의하여 사용해 볼 것
			 * rename 메소드를 정의하면 해당 작업을 할 수 있다.(내 방식대로)
			 * */
			//기본 제공 파일명 변경 객체 사용
			//MultipartRequest multiRequest = new MultipartRequest(request,savePath,maxSize,"UTF-8",new DefaultFileRenamePolicy());
				
			//직접 만든 파일변경 객체 사용
			MultipartRequest multiRequest = new MultipartRequest(request,savePath,maxSize,"UTF-8",new MyFileRenamePolicy());
			//3.DB에 저장할 정보들 추출하기
			//-카테고리 번호,제목,내용,작성자번호를 추출하여 board 테이블에 insert하기
			String category = multiRequest.getParameter("category");
			String title = multiRequest.getParameter("title");
			String content = multiRequest.getParameter("content");
			String boardWriter = multiRequest.getParameter("userNo");
			
			Board b = new Board();
			b.setCategory(category);
			b.setBoardTitle(title);
			b.setBoardContent(content);
			b.setBoardWriter(boardWriter);
			
			Attachment at = null;//처음에는 null로 초기화,첨부파일이 있다면 객체 생성
			
			//multiRequest.getOriginalFileName("키");
			//첨부파일이 있을 경우 원본 명 반환/없는 경우 null을 반환
			if(multiRequest.getOriginalFileName("upfile")!=null) {
				//조회가 된 경우(첨부파일이 있다.)
				at = new Attachment();
				at.setOriginName(multiRequest.getOriginalFileName("upfile"));//원본명
				at.setChangeName(multiRequest.getFilesystemName("upfile"));//수정명(실제 서버에 업로드된 파일명)
				at.setFilePath("/resources/board_files");
			}
			
			
			
			//서비스에게 준비된 객체들 전달하며 서비스 요청하기
			int result = new BoardService().insertBoard(b,at);
			
			if(result>0) {
				request.getSession().setAttribute("alertMsg", "게시글 작성 성공");
				response.sendRedirect(request.getContextPath()+"/list.bo?currentPage=1");
			}else {
				//실패 시에는 업로드 된 파일을 지워주는 작업이 필요하다.(게시글은 없는데 업로드 파일이 자원을 쓰고 있기 때문)
				if(at!=null) {//넘어온 파일이 있어서 객체가 생성됐다면
					//해당 파일 경로 잡아서 File객체 생성 후 delete메소드로 파일 삭제 작업
					new File(savePath+at.getChangeName()).delete();
				}
				request.setAttribute("errorMsg", "게시글 작성 실패");
				request.getRequestDispatcher("views/common/errorPage.jsp").forward(request, response);			
				
			}
			
		}
	}

}
