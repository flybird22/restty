package com.pl.restty.server.provider;

import java.io.IOException;

public interface RegisterHelper {

	void register(String ip, String port, String path,
			String configData) throws Exception;

	void release();

}
