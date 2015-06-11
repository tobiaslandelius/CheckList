package listviews;

import android.content.Context;
import android.widget.CheckBox;

public class ListItem {

	String name;
	CheckBox checkBox;

	public ListItem(String name, Context context) {
		super();
		this.name = name;
		checkBox = new CheckBox(context);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CheckBox getCheckBox() {
		return checkBox;
	}

	public void setCheckBox(CheckBox checkBox) {
		this.checkBox = checkBox;
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(((ListItem) obj).getName());
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name;
	}
}
