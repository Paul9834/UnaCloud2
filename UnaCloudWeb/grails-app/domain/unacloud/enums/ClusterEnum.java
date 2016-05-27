package unacloud.enums;

/**
 *  Represents Cluster states
 *  @author CesarF
 *
 */
public enum ClusterEnum {
	/**
	 * Some images are not availables
	 */
	UNAVAILABLE("UNAVAILABLE"),
	/**
	 * Disabled for admin
	 */
	DISABLE("DISABLE"),
	/**
	 * can be used
	 */
	AVAILABLE("AVAILABLE"),
	/**
	 * FREEZE: can't be deployed because some image is being processed
	 */
	FREEZE("FREEZE");
	
	String name;
	
	private ClusterEnum(String name){
		this.name = name;
	}
}
