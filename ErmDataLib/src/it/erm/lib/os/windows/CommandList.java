package it.erm.lib.os.windows;

import it.erm.lib.utils.Utils;

public class CommandList {

	public static final String CMD = "CMD";
	/**
	 * Carries out the command specified by the string and then terminates
	 */
	public static final String CARRY_OUT_CMD = "/C";
	public static final String CARRY_OUT_CMD_K = "/K";
	
	public static String getServiceInfo(String serviceName, String filter) {
		String result = "sc query \""+serviceName;
		if( !Utils.isNull(filter) ) {
		    result += "\" | find \"  "+filter+"\"";
		}
		return result;
	}
	
	
	
	
	
}
