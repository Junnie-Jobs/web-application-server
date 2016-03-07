package webserver;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpRequest;
import http.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		String uri = "";

		try {
			HttpRequest req = new HttpRequest(connection.getInputStream());
			uri = req.getUri();
		} catch (IOException error) {
			log.error(error.getMessage());
		}

		try {
			HttpResponse resp = new HttpResponse(connection.getOutputStream());
			resp.forward(uri);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
