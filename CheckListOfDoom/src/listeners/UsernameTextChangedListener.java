package listeners;

import java.io.IOException;
import java.util.Observable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import util.Constants;
import android.text.Editable;
import android.text.TextWatcher;
import checklistofdoom.SignUpActivity;
import clients.AbstractClient;
import clients.CheckUserNameClient;

public class UsernameTextChangedListener extends Observable implements TextWatcher {

	private static final String HOSTNAME = Constants.HOSTNAME;
	private static final int PORT = Constants.PORT;
	
	private SignUpActivity signUpActivity;
	private String messageToServer;
	private String response;
	private CountDownLatch latch;
	
	public UsernameTextChangedListener(SignUpActivity signUpActivity) {
		this.signUpActivity = signUpActivity;
		addObserver(signUpActivity);
	}
	
	@Override
	public void afterTextChanged(Editable message) {
		signUpActivity.clearWarnings();
		messageToServer = message.toString();
		if (messageToServer.trim().length() < SignUpActivity.MIN_CHARACTERS_IN_USERNAME) {
			response = "To Maddafakking short";
		} else {
			latch = new CountDownLatch(1);
			Thread serverThread = new Thread(new ServerThread());
			serverThread.start();

			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		setChanged();
		notifyObservers(response);
	}

	
	class ServerThread implements Runnable {
		@Override
		public void run() {
			try {
				AbstractClient client = new CheckUserNameClient(HOSTNAME,
						PORT);
				if (client.challange(messageToServer, null)) {
					response = "Username is AAAIGHT!";
				} else {
					response = "Username already maddafakking exists!";
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (TimeoutException e) {
				response = "Could not connect to server!";
			}
			latch.countDown();
		}
	}
	

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}
