package clients;

import java.util.concurrent.TimeoutException;

public class CreateUserClient extends AbstractClient {

	public CreateUserClient(String hostname, int port) throws TimeoutException {
		super(hostname, port);
	}

	@Override
	protected String createMessage(String username, String password) {
		return "INSERT:" +username+ ":" +password;
	}

	@Override
	protected boolean handleRespons(String response) {
		return response.split(":")[0].equals("true");
	}

	@Override
	protected String getErrorMessage() {
		return null;
	}

}
