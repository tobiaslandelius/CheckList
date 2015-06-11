package listeners;

import java.util.Observable;



import checklistofdoom.SignUpActivity;
import android.text.Editable;
import android.text.TextWatcher;

public class RepeatPasswordTextChangedListener extends Observable implements
		TextWatcher {

	public RepeatPasswordTextChangedListener(SignUpActivity signUpActivity) {
		addObserver(signUpActivity);
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

}
