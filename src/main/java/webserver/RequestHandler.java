package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		HttpResponse hr = new HttpResponse(); 
		HttpRequest hreq = new HttpRequest();

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();

			if (line == null) {
				return;
			}

			String url = HttpRequestUtils.getUrl(line);
			Map<String, String> headers = new HashMap<String, String>();

			line = hreq.getHeader(br, line, headers);

			if (url.startsWith("/user/create")) {
				hr.createUser(hr, out, br, headers);

			} else if (url.equals("/user/login")) {
				hr.loginCheck(hr, out, br, headers);

			} else if (url.endsWith(".css")) {
				hr.cssCheck(hr, out, url);

			}

			else {
				hr.defaultResponse(hr, out, url);

			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

}