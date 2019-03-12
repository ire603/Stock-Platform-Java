package martin;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

public class WatchListController2 {
	public static Tab getWatchListBorderPane(Tab tab2) {
		SplitPane splitPane;
		splitPane = new SplitPane();
		TableView tableView = new TableView();
		tableView = StockQuoteTableView.stockQuoteOLTableView();
		ListView listView = new ListView();
		splitPane.setOrientation(Orientation.VERTICAL);
		splitPane.getItems().add(0, tableView);
		tab2.setContent(splitPane);
		return tab2;
	}
}
