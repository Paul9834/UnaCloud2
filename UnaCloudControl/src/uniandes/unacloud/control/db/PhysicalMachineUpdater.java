package uniandes.unacloud.control.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Class used to execute query, update and delete processes in database for Physical Machine Entity. 
 * Also this class execute query in execution entities
 * @author CesarF
 *
 */
public class PhysicalMachineUpdater {

	/**
	 * Updates a physical machine entity on database based in hostname
	 * @param host name in network for physical machine
	 * @param hostUser user in physical machine
	 * @param ip where message is coming
	 * @param con Database Connection
	 * @param freeSpace quantity in bytes about free space in data folder in physical machine
	 * @param dataSpace quantity in bytes about total space in data folder in physical machine
	 * @param version current agent version
	 * @return true in case physical machines could be updated, false in case not
	 * TODO: add validation of ip 
	 */
	public static boolean updatePhysicalMachine(String host, String hostUser, String ip, Long freeSpace, Long dataSpace, String version, Connection con) {
		try {
			String query = "update physical_machine pm set pm.with_user= ?, pm.state = CASE WHEN pm.state = \'" + PhysicalMachineStateEnum.OFF.name()
					+ "\' THEN  \'"+PhysicalMachineStateEnum.ON.name() + "\' ELSE pm.state END, pm.last_report = CURRENT_TIMESTAMP "
					+ (dataSpace != null ? ", pm.data_space = ?" : "")
					+ (freeSpace != null ? ", pm.free_space = ?" : "")
					+ (version != null ? ", pm.agent_version = ?" : "")
					+ " WHERE pm.name = ? AND pm.ip_id = (SELECT id FROM ip as i WHERE i.ip = ?)"; PreparedStatement ps = con.prepareStatement(query);
			int pos = 1;
			ps.setBoolean(pos++, (hostUser != null && !hostUser.isEmpty() && !(hostUser.replace(">","").replace(" ","")).equals("null")));
			if (dataSpace != null) ps.setLong(pos++, dataSpace);
			if (freeSpace != null) ps.setLong(pos++, freeSpace);
			if (version != null) ps.setString(pos++, version);
			ps.setString(pos++, host.toUpperCase());
			ps.setString(pos++, ip);
			ps.executeUpdate();
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	

	/**
	 * Updates status from of execution
	 * @param id execution 
	 * @param host unique in net	
	 * @param message description
	 * @param status in agent
	 * @param con connection to database
	 * @return true in case execution could be updated, false in case not
	 */
	public static boolean updateExecution(Long id, String host, String message, ExecutionStateEnum status, Connection con) {
		try {
			String query = "update execution vm set vm.message= ?, vm.last_report = CURRENT_TIMESTAMP, vm.status = ?  WHERE vm.id = ? and vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?);"; 
			PreparedStatement ps = con.prepareStatement(query);			
			ps.setString(1, message);
			ps.setString(2, status.name());
			ps.setLong(3, id);
			ps.setString(4, host);
			System.out.println(ps.toString() + " changes " + ps.executeUpdate() + " lines ");
			try {
				ps.close();
			} catch (Exception e) {
				
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return false;
	}
	
	
	/**
	 * Updates all executions by name and id in array
	 * @param ids executions 
	 * @param host which reports
	 * @param con Database connection
	 * @return list of executions which should be stopped in agents
	 */
	public static List<Long> updateExecutions(Long[]ids, String host, Connection con) {
		if(ids == null || ids.length == 0)
			return null;
		try {
			StringBuilder builder = new StringBuilder();
			for (@SuppressWarnings("unused") Long pm : ids)
				builder.append("?,");
			
			builder = builder.deleteCharAt( builder.length() -1 );
			List<Long> idsToStop = new ArrayList<Long>();
			String query = "SELECT vm.id FROM execution vm where vm.id in (" + builder.toString() + ") AND (vm.status = \'" + ExecutionStateEnum.FAILED.name() + "\' OR vm.status = \'" + ExecutionStateEnum.FINISHED.name() + "\' OR vm.status = \'" + ExecutionStateEnum.FINISHING.name() + "\')";
			PreparedStatement ps = con.prepareStatement(query);
			int index = 1;
			for (Long idvme : ids) {
				ps.setLong(index, idvme);
				index++;
			}
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				idsToStop.add(rs.getLong(1));
			try {
				rs.close();
				ps.close();
			} catch (Exception e) {
				
			}			
			String update = "update execution vm set vm.last_report = CURRENT_TIMESTAMP where vm.id in (" + builder.toString() + ") and vm.execution_node_id = (SELECT pm.id FROM physical_machine pm WHERE pm.name = ?)";
			PreparedStatement ps2 = con.prepareStatement(update);
			index = 1;
			for (Long idvme : ids) {
				ps2.setLong(index, idvme);
				index++;
			}
			ps2.setString(index, host);
			System.out.println(ps2.toString() + " changes " + ps2.executeUpdate() + " lines ");
			try {				
				ps2.close();
			} catch (Exception e) {
				
			}
			return idsToStop;
		} catch (Exception e) {
			e.printStackTrace();			
		}		
		return null;
	}

}
