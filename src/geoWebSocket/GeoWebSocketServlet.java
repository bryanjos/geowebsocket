package geoWebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;

/**
 * Servlet implementation class GeoWebSocketServlet
 */
@WebServlet("/GeoWebSocketServlet")
public class GeoWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private final Set<GeoserverInbound> connections = new CopyOnWriteArraySet<GeoserverInbound>();

	/**
	 * @see WebSocketServlet#WebSocketServlet()
	 */
	public GeoWebSocketServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String postBody = readInput(request.getReader());
		
		if (postBody != null && postBody.length() > 0) {
			broadcast(postBody);
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println(postBody);
			out.close();
		} else {
			response.sendError(500, "Invalid Post Body.");
		}

	}

	private String readInput(Reader r) throws IOException {
		StringBuffer message = new StringBuffer();
		BufferedReader reader = new BufferedReader(r);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			message.append(readData);
		}

		reader.close();
		return message.toString();
	}

	private void broadcast(String message) {
		for (GeoserverInbound connection : connections) {
			try {
				CharBuffer buffer = CharBuffer.wrap(message);
				connection.getWsOutbound().writeTextMessage(buffer);
			} catch (IOException ignore) {
				// Ignore
			}
		}
	}

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest arg1) {
		return new GeoserverInbound();
	}

	private final class GeoserverInbound extends StreamInbound {

		private GeoserverInbound() {
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
		}

		@Override
		protected void onClose(int status) {
			connections.remove(this);
		}

		@Override
		protected void onTextData(Reader r) throws IOException {
			broadcast(readInput(r));
		}

		@Override
		protected void onBinaryData(InputStream arg0) throws IOException {
			throw new UnsupportedOperationException("Binary message not supported.");

		}

	}

}
