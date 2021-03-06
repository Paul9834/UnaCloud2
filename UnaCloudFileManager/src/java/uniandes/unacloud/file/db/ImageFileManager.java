package uniandes.unacloud.file.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.file.db.entities.UserEntity;
import uniandes.unacloud.file.db.entities.ImageFileEntity;
import uniandes.unacloud.share.db.PlatformManager;
import uniandes.unacloud.share.db.StorageManager;

/**
 * Class used to execute query, update and delete processes in database for Image entity. 
 * added some queries for Repository and Platform entities
 * @author CesarF
 *
 */
public class ImageFileManager {
	
	/**
	 * Returns an image entity with information about file and repository
	 * @param id
	 * @param state
	 * @param withUser
	 * @param withConfigurer
	 * @param con Database Connection
	 * @return image
	 */
	//TODO improve query for repository, use hash map
	public static ImageFileEntity getImageWithFile(Long id, ImageEnum state, boolean withUser, boolean withConfigurer, Connection con) {
		try {
			String query = null;
			if (withConfigurer) 
				query = "SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.token, vm.user, vm.password, vm.name, vm.platform_id, os.configurer" + (withUser ? ", vm.owner_id" : "") 
				+ " FROM image vm "
				+ " INNER JOIN operating_system os ON vm.operating_system_id = os.id "
				+ " WHERE vm.state = ? and vm.id = ?;";
			else query = "SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.token, vm.user, vm.password, vm.name, vm.platform_id" + (withUser ? ", vm.owner_id" : "") 
					+ " FROM image vm "
					+ " WHERE vm.state = ? and vm.id = ?;";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, state.name());
			ps.setLong(2, id);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			ImageFileEntity image = null;
			if (rs.next()) {
				image = new ImageFileEntity(rs.getLong(1), state, rs.getString(6), StorageManager.getRepository(rs.getLong(5), con), PlatformManager.getPlatform(rs.getInt(10), con), rs.getBoolean(3), rs.getLong(2), rs.getString(4), rs.getString(9), withConfigurer ? rs.getString(11) : null);
				image.setUser(rs.getString(7));
				image.setPassword(rs.getString(8));
				if (withUser) 
					image.setOwner(new UserEntity(withConfigurer ? rs.getLong(12) : rs.getLong(11), null, null));
			}
			try {
				rs.close();
				ps.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Returns an image entity based in token 
	 * @param token
	 * @param con Database connection
	 * @return image, could be null
	 */
	public static ImageFileEntity getImageWithFile(String token,Connection con) {
		try {
			PreparedStatement ps = con.prepareStatement(
					"SELECT vm.id, vm.fixed_disk_size, vm.is_public, vm.main_file, vm.repository_id, vm.state, vm.name, vm.owner_id, vm.platform_id "
					+ "FROM image vm "
					+ "WHERE vm.token = ? ;");
			ps.setString(1,token);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();	
			ImageFileEntity image = null;
			if (rs.next()) {
				image = new ImageFileEntity(rs.getLong(1), ImageEnum.getEnum(rs.getString(6)), token, StorageManager.getRepository(rs.getLong(5), con), PlatformManager.getPlatform(rs.getInt(9), con), rs.getBoolean(3), rs.getLong(2), rs.getString(4), rs.getString(7), null);
				image.setOwner(new UserEntity(rs.getLong(8),null,null));
			}			 
			try {
				rs.close();
				ps.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			return image;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Updates an image entity on database.
	 * @param image to be updates
	 * @param update if version should be updated
	 * @param con Database connection
	 * @return true in case image was update, false in case not
	 * TODO improve this code
	 */
	public static boolean setImageFile(ImageFileEntity image, boolean update, Connection con, boolean withToken) {
		if (image.getId() == null || image.getId() < 1)
			return false;
		try {
			String query = "UPDATE image vm SET" + 
						(withToken ? ", vm.token = NULL" : "") +
						(image.isPublic() != null ? ", vm.is_public = ? " : "") +
						(image.getRepository() != null ? ", vm.repository_id = ? " : "") +
						(image.getMainFile() != null ? ", vm.main_file = ? " : "") +
						(image.getState() != null ? ", vm.state = ? " : "") +
						(!withToken && image.getToken() != null ? ", vm.token = ? " : "");
			if (query.contains(",")) {
				query = query.replaceFirst(",", "");
				if (update)
					query += ", vm.image_version = vm.image_version + 1 ";
				if (image.getFixDisk() != null && image.getFixDisk() > 0)
					query += ", vm.fixed_disk_size = " + image.getFixDisk() + " ";
				query += "WHERE vm.id = ? AND vm.id > 0;";				
				PreparedStatement ps = con.prepareStatement(query);
				int id = 1;
				if (image.isPublic() != null) ps.setBoolean(id++, image.isPublic());
				if (image.getRepository() != null) ps.setLong(id++, image.getRepository().getId());
				if (image.getMainFile() != null) ps.setString(id++, image.getMainFile());
				if (image.getState() != null) ps.setString(id++, image.getState().name());
				if (!withToken && image.getToken() != null) ps.setString(id++, image.getToken());
				ps.setLong(id, image.getId());
				System.out.println(ps.toString());
				System.out.println("Change " + ps.executeUpdate() + " lines");
				try {
					ps.close();
				} catch(Exception e){
					e.printStackTrace();
				}			
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	/**
	 * Returns a list of images belonged by user
	 * @param userId
	 * @param con Database connection
	 * @return
	 */
	//TODO improve query for repository, use hash map
	public static List<ImageFileEntity> getAllImagesByUser(Long userId, Connection con) {
		try {
			List<ImageFileEntity> list = new ArrayList<ImageFileEntity>();	
			String query = "SELECT vm.id, vm.state, vm.token, vm.repository_id, vm.is_public, vm.fixed_disk_size, vm.main_file, vm.name, vm.platform_id "
							+ "FROM image vm "
							+ "WHERE vm.owner_id = ?;";
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setLong(1, userId);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();		
			while (rs.next())
				list.add(new ImageFileEntity(rs.getLong(1)
						, ImageEnum.getEnum(rs.getString(2)), rs.getString(3), StorageManager.getRepository(rs.getLong(4), con), PlatformManager.getPlatform(rs.getInt(9), con), rs.getBoolean(5), rs.getLong(6), rs.getString(7), rs.getString(8), null));
			try {
				rs.close();
				ps.close();
			} catch(Exception e){
				e.printStackTrace();
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
