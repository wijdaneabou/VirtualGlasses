package com.virtualglasses.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GlassesDao {
	public List<Glasse> findByFaceShape(String faceShapeName) throws SQLException {
	    String dbFaceShape = convertToDbFaceShape(faceShapeName);
	    String sql = 
	        "SELECT g.id, g.name, g.image_path, g.type_id, g.face_shape_id " +
	        "FROM Glasses g " +
	        "JOIN FaceShapes fs ON g.face_shape_id = fs.id " +
	        "WHERE LOWER(fs.shape_name) = LOWER(?) OR LOWER(fs.forme) = LOWER(?)";

	    List<Glasse> list = new ArrayList<>();
	    try (Connection conn = DbConnextion.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, dbFaceShape);
	        ps.setString(2, dbFaceShape);
	        
	     
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Glasse g = new Glasse();
	                g.setId(rs.getInt("id"));
	                g.setName(rs.getString("name"));
	                g.setImagePath(rs.getString("image_path"));
	                g.setTypeId(rs.getInt("type_id"));
	                g.setFaceShapeId(rs.getInt("face_shape_id"));

	                byte[] imageData = rs.getBytes("image_path");
	                if (imageData != null && imageData.length > 0) {
	                    g.setImageData(imageData);
	                }
	                
	                list.add(g);
	                
	            }
	        }
	    } catch (Exception e) {
	       
	        e.printStackTrace();
	    }
	    
	    return list;
	}
	private String convertToDbFaceShape(String faceShape) {
	    if (faceShape == null) return "iconnu";
	   
	    switch (faceShape.toLowerCase()) {
	        case "oval": return "Oval";
	        case "round": return "Rond";
	        case "square": return "Carré";
	        case "oblong": return "Rectangle";
	        case "heart": return "Coeur";
	        default: return faceShape;
	    }
	}
	public List<Glasse> getRandomGlasses(int limit) throws SQLException {
	   
	    String sql = "SELECT id, name, image_path, type_id, face_shape_id FROM Glasses ORDER BY RAND() LIMIT ?";
	    
	    List<Glasse> list = new ArrayList<>();
	    try (Connection conn = DbConnextion.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, limit);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Glasse g = new Glasse();
	                g.setId(rs.getInt("id"));
	                g.setName(rs.getString("name"));
	                g.setImagePath(rs.getString("image_path"));
	                g.setTypeId(rs.getInt("type_id"));
	                g.setFaceShapeId(rs.getInt("face_shape_id"));
	                
	                byte[] imageData = rs.getBytes("image_path");
	                if (imageData != null && imageData.length > 0) {
	                    g.setImageData(imageData);
	                }
	                
	                list.add(g);
	            }
	        }
	    } catch (Exception e) {
	       
	        e.printStackTrace();
	    }
	    
	    return list;
	}
}