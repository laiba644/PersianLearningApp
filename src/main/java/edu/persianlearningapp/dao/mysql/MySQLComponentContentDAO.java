package edu.persianlearningapp.dao.mysql;

import edu.persianlearningapp.dao.ComponentContentDAO;
import edu.persianlearningapp.domain.ComponentContent;

import edu.persianlearningapp.dao.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//UC-03  Faseeh writes SQL 
public class MySQLComponentContentDAO implements ComponentContentDAO {
	@Override
    public ComponentContent findContent(int chapterId, int componentId) { 
    	String sql="SELECT cc.component_id,cc.persian_content,cc.urdu_content "+
    				"FROM component_contents cc"+
    				"JOIN chapter_components c ON cc.component_id=c.component_id"+
    				"c.chapter_id=? AND cc.component_id=?";
    	try(Connection connection=DatabaseConnection.open();
    		PreparedStatement statement=connection.prepareStatement(sql)){
    		statement.setInt(1,chapterId);
    		statement.setInt(2,componentId);
    		try(ResultSet result=statement.executeQuery()){
    			if(result.next()) {
    				return new ComponentContent(result.getInt("component_id"),result.getString("persian_content"),result.getString("urdu_content"));
    				
    			}
    			return null;
    		}
    		
    	} 
    	catch(SQLException e) {
    		throw new DataAccessException("Could not Load Content",e);
    	}
    }
	
	@Override
    public boolean saveContent(int chapterId, int componentId, String persian, String urdu) { 
		String checkSql = "SELECT component_id "+
						"FROM chapter_components "+
						"WHERE chapter_id = ? AND component_id = ?";
		//below query make sure that add or edit wala kaam dono ho sakain
		String saveSql = "INSERT INTO component_contents "+
	            "(component_id, persian_content, urdu_content) "+
	            "VALUES (?, ?, ?) "+
	            "ON DUPLICATE KEY UPDATE "+
	            "persian_content = VALUES(persian_content), "+
	            "urdu_content = VALUES(urdu_content)";
		 try (Connection connection = DatabaseConnection.open()) {

		        // checking keh component chp may exist krna cahiya
		        try (PreparedStatement check = connection.prepareStatement(checkSql)) {

		            check.setInt(1, chapterId);
		            check.setInt(2, componentId);

		            try (ResultSet result = check.executeQuery()) {
		                if (!result.next()) {
		                    return false;
		                }
		            }
		        }

		        //adding or editing content
		        try (PreparedStatement statement = connection.prepareStatement(saveSql)) {

		            statement.setInt(1, componentId);
		            statement.setString(2, persian);
		            statement.setString(3, urdu);

		            statement.executeUpdate();
		            return true;
		        }

		 } 
		 catch (SQLException e) {
		        throw new DataAccessException("Could not save content.", e);
		 }
		
		
    }
	@Override
    public boolean deleteContent(int chapterId, int componentId) { 
		String sql = "DELETE cc "+
				"FROM component_contents cc "+
	            "JOIN chapter_components c ON cc.component_id = c.component_id "+
	            "WHERE c.chapter_id = ? AND cc.component_id = ?";

	    try (Connection connection = DatabaseConnection.open();
	         PreparedStatement statement = connection.prepareStatement(sql)) {

	        statement.setInt(1, chapterId);
	        statement.setInt(2, componentId);

	        int deletedRows = statement.executeUpdate();

	        return deletedRows > 0;

	    } catch (SQLException e) {
	        throw new DataAccessException("Could not delete content.", e);
	    }
	}
	}
}
