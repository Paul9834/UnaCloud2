package virtualMachineManager.task;

import com.losandes.enums.VirtualMachineExecutionStateEnum;

import communication.send.report.ServerMessageSender;
import exceptions.VirtualMachineExecutionException;
import virtualMachineManager.ImageCacheManager;
import virtualMachineManager.entities.ImageCopy;
import virtualMachineManager.entities.VirtualMachineExecution;

/**
 * Task to start a virtual machine execution
 * @author CesarF
 *
 */
public class StartVirtualMachineTask implements Runnable{
	VirtualMachineExecution machineExecution;
	/**
	 * class constructor
	 * @param machineExecution VM instance to be started
	 */
	public StartVirtualMachineTask(VirtualMachineExecution machineExecution) {
		this.machineExecution = machineExecution;
	}
	
	/**
	 * Executes start machine task
	 */
	@Override
	public void run() {
		System.out.println("StartVirtualMachine");
		try{
			//get image 
			ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
			System.out.println("Get Image");
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
			System.out.println("endStartVirtualMachine");
		}catch(VirtualMachineExecutionException ex){
			try {
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FAILED,ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}