package listeners;

import java.util.Observable;



import checklistofdoom.SignUpActivity;
import android.text.Editable;
import android.text.TextWatcher;

public class PasswordTextChangedListener extends Observable implements TextWatcher {

	private boolean getOk;
	
	public PasswordTextChangedListener(SignUpActivity signUpActivity) {
		addObserver(signUpActivity);
	}
	
	@Override
	public void afterTextChanged(Editable message) {
		String password = message.toString();
		setChanged();

		if (password.length() < 6) {
			getOk = false;
			notifyObservers("To short");
		} else if (!password.matches("[A-Za-z0-9]+")) {
			getOk = false;
			notifyObservers("Illegal characters");
		} else {
			getOk = true;
			notifyObservers();
		}
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}
	
	public boolean getOk() {
		return getOk;
	}

}
