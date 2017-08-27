package com.rater193.sp;

import java.util.logging.Logger;

public class Debug {

	static Logger logger = Logger.getLogger("Minecraft");
	public static void log(Object message) {
		logger.info("[Structure Physics] " + message);
	}
}
