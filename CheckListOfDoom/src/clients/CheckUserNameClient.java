package clients;

import java.util.concurrent.TimeoutException;

public class CheckUserNameClient extends AbstractClient {

	public CheckUserNameClient(String hostname, int port) throws TimeoutException {
		super(hostname, port);
	}

	@Override
	protected String createMessage(String username, String password) {
		return "CHECK_FOR_USERNAME:" +username;
	}

	@Override
	protected boolean handleRespons(String response) {
		String[] split = response.split(":");
		return (split[0].equals("true"))? true : false;
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

}
