package clients;

import java.util.concurrent.TimeoutException;


public class LoginClient extends AbstractClient {

	private String errorMessage;
	private String identifier;

	public LoginClient(String hostname, int port) throws TimeoutException {
		super(hostname, port);
	}

	@Override
	protected String createMessage(String username, String password) {
		return "LOGIN:" + username + ":" + password;
	}

	@Override
	protected boolean handleRespons(String response) {
		String[] splitResponse = response.split(":");
		if (splitResponse[0].equals("true")) {
			identifier = splitResponse[2];
			return true;
		}
		errorMessage = splitResponse[1];
		return false;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getIdentifier() {
		return identifier;
	}
}
