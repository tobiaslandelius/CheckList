package checklistofdoom;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.example.checklistofdoom.R;

import util.Constants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import clients.AbstractClient;
import clients.LoginClient;
import encryption.SHA256;

public class LoginActivity extends Activity {

	private static final String HOSTNAME = Constants.HOSTNAME;
	private static final int PORT = Constants.PORT;

	private Button loginButton;
	private Button signUpButton;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private TextView usernameWarningTextView;
	private TextView passwordWarningTextView;
	private TextView wrongUserInfoWarningTextView;

	private boolean permissionToLogin;
	private String identifier;
	private String username;

	private int responsToFailedConnectNumber;

	private CountDownLatch latch; 	 // Latch used to wait for thread calling
									 // server

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setOnClickListener(loginButtonListener);
		signUpButton = (Button) findViewById(R.id.sign_up_button);
		signUpButton.setOnClickListener(signUpButtonListener);
		usernameEditText = (EditText) findViewById(R.id.username_edit_text);
		passwordEditText = (EditText) findViewById(R.id.password_edit_text);
		usernameWarningTextView = (TextView) findViewById(R.id.username_warning_textfield);
		passwordWarningTextView = (TextView) findViewById(R.id.password_warning_textfield);
		wrongUserInfoWarningTextView = (TextView) findViewById(R.id.wrong_userinfo_warning_textfield);

	}

	View.OnClickListener loginButtonListener = new View.OnClickListener() {
		private Thread serverThread;

		public void onClick(View v) {
			clearWarnings();
			if (checkIfCorrectUserInput()) {
				this.serverThread = new Thread(new ServerThread());
				latch = new CountDownLatch(1);
				serverThread.start();
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (permissionToLogin) {
					usernameWarningTextView.setText("");
					Intent resultIntent = new Intent();
					resultIntent.putExtra("username", username);
					resultIntent.putExtra("identifier", identifier);
					setResult(Activity.RESULT_OK, resultIntent);
					finish();
				} else {
					wrongUserInfoWarningTextView
							.setText(Constants.responsesToFailedServerConnect[responsToFailedConnectNumber]);
				}
			}
		}

		private boolean checkIfCorrectUserInput() {
			username = usernameEditText.getText().toString().trim();
			if (isEditTextEmpty(usernameEditText)) {
				usernameWarningTextView.setText("Please enter username");
				return false;
			}
			if (isEditTextEmpty(passwordEditText)) {
				passwordWarningTextView.setText("Please enter password");
				return false;
			}
			return true;
		}
	};

	View.OnClickListener signUpButtonListener = new View.OnClickListener() {

		public void onClick(View v) {
			signUpActivity();
		}
	};

	@Override
	public void onBackPressed() {
	}

	private void signUpActivity() {
		startActivity(new Intent(this, SignUpActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ServerThread implements Runnable {

		@Override
		public void run() {
			String password = SHA256.encrypt(passwordEditText.getText()
					.toString());
			boolean permission = false;

			try {
				AbstractClient client = new LoginClient(HOSTNAME, PORT);
				permission = client.challange(username, password);
				setPermission(permission);
				setIdentifier(((LoginClient) client).getIdentifier());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				responsToFailedConnectNumber = 1;
			}
			latch.countDown();
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	private void setPermission(boolean permission) {
		this.permissionToLogin = permission;
	}

	private void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	private void clearWarnings() {
		usernameWarningTextView.setText("");
		passwordWarningTextView.setText("");
		wrongUserInfoWarningTextView.setText("");
	}

	private boolean isEditTextEmpty(EditText edittext) {
		return edittext.getText().toString().trim().length() == 0;
	}
}
