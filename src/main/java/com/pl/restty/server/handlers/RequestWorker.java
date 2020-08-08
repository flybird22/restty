package com.pl.restty.server.handlers;

public interface RequestWorker {

	boolean handle(HttpRequestWrapper request, HttpResponseWrapper response) throws Exception;

}
