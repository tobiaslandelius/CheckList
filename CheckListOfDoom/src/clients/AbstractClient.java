package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.util.Log;

public abstract class AbstractClient {

	private String hostname;
	private int port;
	private Socket clientSocket;

	public AbstractClient(String hostname, int port) throws TimeoutException {
		this.hostname = hostname;
		this.port = port;
		connect();
	}

	private void connect() throws TimeoutException  {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Log.e("Kebab", "Innan Future");
		Future<String> future = executor.submit(new InitializeSocket());
		
		try {
            System.out.println("Started..");
            System.out.println(future.get(3, TimeUnit.SECONDS));
            System.out.println("Finished!");
        }catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public boolean challange(String username, String password)
			throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

		String serverMessage = createMessage(username, password);
		out.write(serverMessage + "\n");
		out.flush();
		String response = in.readLine();

		in.close();
		out.close();
		clientSocket.close();
		return handleRespons(response);
	}

	protected abstract String createMessage(String username, String password);

	protected abstract boolean handleRespons(String response);

	protected abstract String getErrorMessage();
	
	public class InitializeSocket implements Callable<String> {

		@Override
		public String call() throws Exception {
			clientSocket = new Socket(hostname, port);
			return "Done";
		}

	}
}
