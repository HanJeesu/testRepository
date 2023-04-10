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
 * Servlet implementation class BoardUpdateController
 */
@WebServlet("/update.bo")
public class BoardUpdateController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BoardUpdateController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		int bNo = Integer.parseInt(request.getParameter("boardNo"));
		Board b = new BoardService().selectBoard(bNo);
		Attachment at = new BoardService().selectAttachment(bNo);
		ArrayList<Category> clist = new BoardService().categoryList();
		
		
		request.setAttribute("b", b);
		request.setAttribute("bNo", b);
		request.setAttribute("at", at);
		request.setAttribute("clist", clist);
		request.getRequestDispatcher("views/board/boardUpdateForm.jsp").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//수정처리
		request.setCharacterEncoding("UTF-8");
		
		//첨부파일 있고 없고 차이 생각해보기
		//enctype이 multipart/form-data형식인지 확인
		if(ServletFileUpload.isMultipartContent(request)) {
			//전송파일 용량 제한
			int maxSize = 10 * 1024 * 1024;
			
			//저장시킬 서버 저장 경로 찾기(물리적인 서버 폴더 경로)
			String savePath = request.getSession().getServletContext().getRealPath("/resources/board_files");
			
			//파일명 수정 작업 객체 추가하기
			MultipartRequest multiRequest = new MultipartRequest(request, savePath, maxSize, "UTF-8", new MyFileRenamePolicy());
			
			//수정작업에 필요한 기존 데이터 추출하기
			String boardTitle = multiRequest.getParameter("title");
			String boardContent = multiRequest.getParameter("content");
			String category = multiRequest.getParameter("category");
			int boardNo = Integer.parseInt(multiRequest.getParameter("boardNo"));

			Board b = new Board();
			b.setBoardTitle(boardTitle);
			b.setBoardNo(boardNo);
			b.setCategory(category);
			b.setBoardContent(boardContent);
			
			//새롭게 전달된 첨부파일이 있다면 처리하기
			
			Attachment at = null;
			
			if(multiRequest.getOriginalFileName("reUpfile")!=null) {
				
				at = new Attachment();
				
				at.setOriginName(multiRequest.getOriginalFileName("reUpfile"));
				at.setChangeName(multiRequest.getFilesystemName("reUpfile"));
				at.setFilePath("/resources/board_files");
				
				//기존에 첨부파일이 있었을 경우 해당 데이터에서 수정작업을 해야 함
				//form에서 hidden으로 파일번호와 변경된 이름(서버에 저장된 이름)을 전달했기 때문에
				//해당 정보가 있는지 없는지 판별해주면 된다.
				if(multiRequest.getParameter("fileNo")!=null) {
					//새로운 첨부 파일이 있고 기존에도 첨부파일이 있는 경우
					//update attachment
					//기존 파일 번호(식별자)를 이용하여 데이터 변경하기
					at.setFileNo(Integer.parseInt(multiRequest.getParameter("fileNo")));
					
					//기존의 첨부파일 삭제하기
					new File(savePath+"/"+multiRequest.getParameter("originFileName")).delete();
				}else {//원래 첨부 파일이 없었고 새롭게 들어온 경우
					//현재 게시글 번호를 참조하게 INSERT하기
					at.setRefBno(boardNo);
				}
				//DML(update)
				int result = new BoardService().updateBoard(b,at);
				//새로운 첨부파일 없고 기존 첨부파일도 없는 경우 - board update
				//새로운 첨부파일 있고 기존 첨부파일 없는 경우 -board-update / attachment-insert
				//새로운 첨부파일이 있고 기존 첨부파일 있는 경우 -board-update / attachment-update
				
				if(result>0) {
				request.getSession().setAttribute("alertMsg", "게시글 수정 완료");
				//성공 시 상세보기 페이지 띄워주기
				response.sendRedirect(request.getContextPath()+"/detail.bo?bno="+boardNo);
				}else {
				//실패 시 에러페이지
				request.setAttribute("errorMsg", "게시글 수정 실패");
				request.getRequestDispatcher("views/common/errorPage.jsp").forward(request, response);
			}

			
		}
	
		
		}
	}

}
