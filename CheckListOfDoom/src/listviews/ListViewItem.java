package listviews;

public class ListViewItem {

	private String item;
	private String amount;
	private boolean selected = false;
	
	public ListViewItem(String stuff, String amount) {
		super();
		this.item = stuff;
		this.amount = amount;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
