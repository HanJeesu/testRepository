package com.kh.board.model.service;

import java.sql.Connection;
import java.util.ArrayList;

import com.kh.board.model.dao.BoardDao;
import com.kh.board.model.vo.Attachment;
import com.kh.board.model.vo.Board;
import com.kh.board.model.vo.Category;
import com.kh.common.JDBCTemplate;
import com.kh.common.model.vo.PageInfo;


public class BoardService {

	//총 게시글 개수 구하기
	public int selectListCount() {
		
		Connection conn = JDBCTemplate.getConnection();
		
		//처리된 행수가 아닌 총 게시글의 개수를 조회해온 것
		int listCount = new BoardDao().selectListCount(conn);
		
		JDBCTemplate.close(conn);
		
		return listCount;
	}

	//게시글 리스트 조회
	public ArrayList<Board> selectList(PageInfo pi) {
		Connection conn = JDBCTemplate.getConnection();
		
		ArrayList<Board> list = new BoardDao().selectList(conn,pi);
		
		JDBCTemplate.close(conn);
		
		return list;
	}
	
	//카테고리 리스트
	public ArrayList<Category> categoryList(){
		Connection conn = JDBCTemplate.getConnection();
		
		ArrayList<Category> list = new BoardDao().categoryList(conn);
		
		JDBCTemplate.close(conn);
		
		return list;
	}

	public int insertBoard(Board b, Attachment at) {
		Connection conn = JDBCTemplate.getConnection();
		
		//게시글이 작성될 때 첨부파일이 있거나 또는 없는 경우도 생각하여 작업하기
		int result = new BoardDao().insertBoard(conn,b);
		//첨부파일이 없어도 게시글 커밋은 해야하니까 해당 조건에 부합하게 1로 초기화 해놓기
		int result2 = 1;
		//첨부파일이 있는 경우에 Attachment 테이블에 insert작업하기
		if(at!=null) {
			result2 = new BoardDao().insertAttachment(conn,at);
		}
		
		if(result>0 && result2>0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		JDBCTemplate.close(conn);
		
		return result*result2;//둘 중 하나라도 0이 나오면 0을 반환하게 작성하기
		
	}

	//조회수 증가
	public int increaseCount(int boardNo) {

		Connection conn = JDBCTemplate.getConnection();
		
		int result = new BoardDao().increaseCount(conn,boardNo);
		
		if(result>0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		JDBCTemplate.close(conn);
		return result;
	}

	public Board selectBoard(int boardNo) {
		
		Connection conn = JDBCTemplate.getConnection();
		
		Board b = new BoardDao().selectBoard(conn,boardNo);
		
		JDBCTemplate.close(conn);
		
		return b;
	}

	public Attachment selectAttachment(int boardNo) {
		Connection conn = JDBCTemplate.getConnection();
		
		Attachment at = new BoardDao().selectAttachment(conn,boardNo);
		
		JDBCTemplate.close(conn);
		
		return at;
	}

	//정보 수정 메소드
	public int updateBoard(Board b, Attachment at) {

		Connection conn = JDBCTemplate.getConnection();
		
		int result = new BoardDao().updateBoard(conn,b);
		
		int result2 = 1;
		
		if(at!=null) {//새롭게 추가된 첨부파일이 있는 경우
			
			if(at.getFileNo() != 0) {//기존에 첨부파일이 있었을 경우(변경)
				result = new BoardDao().updateAttachment(conn,at);
			}else {//기존의 첨부파일이 없었을 경우(추가)
				result2 = new BoardDao().newInsertAttachment(conn,at);
			}
		}
		
		if(result>0 && result2>0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		JDBCTemplate.close(conn);
		
		
		return result*result2;
	}

	public int deleteBoard(int bNo) {
		Connection conn = JDBCTemplate.getConnection();
		
		int result = new BoardDao().deleteBoard(conn,bNo);
		
		if(result>0) {
			JDBCTemplate.commit(conn);
		}else {
			JDBCTemplate.rollback(conn);
		}
		JDBCTemplate.close(conn);
		
		return result;
	}

}
