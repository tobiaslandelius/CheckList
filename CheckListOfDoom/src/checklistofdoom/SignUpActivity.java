package checklistofdoom;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.example.checklistofdoom.R;

import util.Constants;
import listeners.PasswordTextChangedListener;
import listeners.UsernameTextChangedListener;
import clients.AbstractClient;
import clients.CreateUserClient;
import encryption.SHA256;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignUpActivity extends Activity implements Observer {

	private static final String HOSTNAME = Constants.HOSTNAME;
	private static final int PORT = Constants.PORT;
	public static final int MIN_CHARACTERS_IN_USERNAME = 3;

	private Button signUpButton;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText repeatPasswordEditText;

	private TextView usernameWarningTextView;
	private TextView passwordWarningTextView;
	private TextView repeatPasswordWarningTextView;
	private TextView generalSignUpWarningTextView;
	private LinkedList<TextView> warningTextViewSet;

	private CountDownLatch latch;
	private boolean signUpResponse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		signUpButton = (Button) findViewById(R.id.confirm_sign_up_button);
		signUpButton.setOnClickListener(confirmSignUpListener);

		usernameEditText = (EditText) findViewById(R.id.new_username_edit_text);
		usernameEditText
				.addTextChangedListener(new UsernameTextChangedListener(this));

		passwordEditText = (EditText) findViewById(R.id.new_password_edit_text);
		passwordEditText
				.addTextChangedListener(new PasswordTextChangedListener(this));

		repeatPasswordEditText = (EditText) findViewById(R.id.repeat_new_password_edit_text);
		repeatPasswordEditText
				.addTextChangedListener(new RepeatPasswordTextChangedListener(
						this));

		usernameWarningTextView = (TextView) findViewById(R.id.new_username_warning_textfield);
		passwordWarningTextView = (TextView) findViewById(R.id.new_password_warning_textfield);
		repeatPasswordWarningTextView = (TextView) findViewById(R.id.repeat_new_password_warning_textfield);
		generalSignUpWarningTextView = (TextView) findViewById(R.id.general_sign_up_warning_textfield);
		warningTextViewSet = new LinkedList<TextView>();
		warningTextViewSet.add(usernameWarningTextView);
		warningTextViewSet.add(passwordWarningTextView);
		warningTextViewSet.add(repeatPasswordWarningTextView);
		warningTextViewSet.add(generalSignUpWarningTextView);
	}

	View.OnClickListener confirmSignUpListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			clearWarnings();
			latch = new CountDownLatch(1);
			if (checkIfCorrectUserInput()) {
				latch = new CountDownLatch(1);
				Thread serverThread = new Thread(new ServerThread());
				serverThread.start();
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (signUpResponse) {
					// Maybe fill intent with login information here...
					finish();
				}
			}
		}

		private boolean checkIfCorrectUserInput() {
			if (isEditTextEmpty(usernameEditText)) {
				usernameWarningTextView.setText("Please enter username");
				return false;
			}
			if (isEditTextEmpty(passwordEditText)) {
				passwordWarningTextView.setText("Please enter password");
				return false;
			}
			if (passwordEditText.getText().toString().trim().length() < 6) {
				generalSignUpWarningTextView
						.setText("Password must be at least 6 characters");
				return false;
			}
			if (isEditTextEmpty(repeatPasswordEditText)) {
				repeatPasswordWarningTextView
						.setText("Please re-enter password");
				return false;
			}
			if (!passwordEditText.getText().toString()
					.equals(repeatPasswordEditText.getText().toString())) {
				generalSignUpWarningTextView
						.setText("Passwords does not match");
				return false;
			}
			return true;
		}
	};

	class ServerThread implements Runnable {

		@Override
		public void run() {
			String username = usernameEditText.getText().toString();
			String password = SHA256.encrypt(passwordEditText.getText()
					.toString());
			try {
				AbstractClient client = new CreateUserClient(HOSTNAME, PORT);
				signUpResponse = client.challange(username, password);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			latch.countDown();
		}
	}

	private boolean isEditTextEmpty(EditText edittext) {
		return edittext.getText().toString().trim().length() == 0;
	}

	public void clearWarnings() {
		for (TextView v : warningTextViewSet)
			v.setText("");
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof UsernameTextChangedListener) {
			String response = (String) arg1;
			generalSignUpWarningTextView.setText("" + response);
		} else if (arg0 instanceof PasswordTextChangedListener) {
			if (((PasswordTextChangedListener) arg0).getOk()) {
				passwordWarningTextView.setText("OK!");
			} else {
				passwordWarningTextView.setText((String) arg1);
			}
		} else if (arg0 instanceof RepeatPasswordTextChangedListener){
			repeatPasswordWarningTextView.setText("HÖBÖLÖ");
		}
	}

	private class RepeatPasswordTextChangedListener extends Observable
			implements TextWatcher {

		public RepeatPasswordTextChangedListener(SignUpActivity signUpActivity) {
			addObserver(signUpActivity);
		}

		@Override
		public void afterTextChanged(Editable s) {
			String password = passwordEditText.getText().toString();
			String repeatPassword = s.toString();
			setChanged();
			if (password.equals(repeatPassword)) {
				notifyObservers(true);
			} else {
				notifyObservers(false);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	}
}
