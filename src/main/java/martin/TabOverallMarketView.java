package martin;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import martin.screener.MysqlConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CountDownLatch;

public class TabOverallMarketView {

	private static MysqlConnect mysqlConnect;

	public TabOverallMarketView(MysqlConnect mysqlConnect) {
		this.mysqlConnect = mysqlConnect;
	}


	public static Tab getTabPane() {

		BorderPane borderPane = new BorderPane();
		Tab tab = new Tab("Overall Market");

		TableView tableView1 = new TableView();
		TableView tableView2 = new TableView();
		tableView1.setPrefSize(1280, 377);
		tableView2.setPrefSize(1280, 377);

		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setVmax(500);
		scrollPane.setPrefSize(800, 400);

		BuilderTableKeyStats.buildingData(mysqlConnect, tableView1);
		BuilderTableCompanyData.buildingData(mysqlConnect, tableView2);

		borderPane.setTop(tableView2);
		borderPane.setCenter(tableView1);

		tab.setContent(borderPane);

		return tab;
	}


}