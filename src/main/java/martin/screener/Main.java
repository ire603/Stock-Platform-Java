package martin.screener;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import pl.zankowski.iextrading4j.api.exception.IEXTradingException;
import pl.zankowski.iextrading4j.api.refdata.ExchangeSymbol;
import pl.zankowski.iextrading4j.api.stocks.KeyStats;
import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.refdata.SymbolsRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.KeyStatsRequestBuilder;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/*
                                    Author  - Isaac Igori Martin
                                    Date    - January 16th, 2019
                                    Project - Stock MySQL DB and Screening
 */
public class Main {
    private static BorderPane borderPane;
    private static Button button0;
    private static Button button1;
    private static Button button2;
    private static Button button3;
    private static Button button4;
    private static Button button5;
    private static Button button6;
    private static Button button7;
    private static Button button8;
    private static Button button9;
    private static TextField textFieldSingleStock;
    private static MysqlConnect mysqlConnect;

    public Main(MysqlConnect mysqlConnect) {
        this.mysqlConnect = mysqlConnect;
    }

    public static Tab tab() {

        final IEXTradingClient iexTradingClient = IEXTradingClient.create();

        // Change variable i from 0 to 7 for Desired Step
        button0 = new Button("Insert Symbols Into Table");

        button0.setOnAction(event -> {
            // Step 1 - Insert Symbols into DB
            insertSymbolsIntoTable(iexTradingClient, mysqlConnect);
        });
        button1 = new Button("Insert Data Into Company Data");

        button1.setOnAction(event -> {
            // Step 2 - Insert Data into CompanyData
            getResultSet(mysqlConnect, iexTradingClient);
        });
        button2 = new Button("Insert Data Into Key Stats");

        button2.setOnAction(event -> {
            // Step 3 - Insert Data into KeyStats
            consumeKeyStats(mysqlConnect, iexTradingClient);
        });

        button3 = new Button("Update Single Stock");
        textFieldSingleStock = new TextField("Enter Stock");
        button3.setOnAction(event -> {
            String stock = textFieldSingleStock.getText();
            // Step 4 Optional - Update Just a Single Stock
            updateDataInCompanyDataSingleStock(mysqlConnect, iexTradingClient, stock );
        });
        button4 = new Button("Update All Stocks");

        button4.setOnAction(event -> {
            // Step 5 Optional - Update All Stocks
            updateDataInCompanyData(mysqlConnect, iexTradingClient);
        });
        button5 = new Button("Insert Data Into Company Data That is Not There");

        button5.setOnAction(event -> {
            // Step 6 Optional - Insert Data into Company Data That is Not There
            insertingDataThatsNotThere(mysqlConnect, iexTradingClient);
        });
        button6 = new Button("Insert Data into Key Stats That is Not There");

        button6.setOnAction(event -> {
            // Step 7 Optional - Insert Data int Key Stats That is Not There
            insertingDataThatsNotThereKeyStats(mysqlConnect, iexTradingClient);
        });
        button7 = new Button("Start Screening with Volume");

        button7.setOnAction(event -> {
            // Step 8 - Start Screening with Parameters set
            // Currently set to Volume between 0 And 1 - Change for your own purposes
            StockScreening stockScreening = new StockScreening(mysqlConnect);
//            stockScreening.filterStocks(betweenInt, andInt);
        });
        button8 = new Button("Start Screening with ....");

        button8.setOnAction(event -> {
            // Step 8 - Start Screening with Parameters set
            // Currently set to 3 Month Percent Change between 0 And 1 - Change for your own purposes
            StockScreening stockScreening1 = new StockScreening(mysqlConnect);
            stockScreening1.filterStocks1(0, 1, 0,1);
        });
        button9 = new Button("Start Screening with ....");

        button9.setOnAction(event -> {
            // Step 8 - Start Screening with Parameters set
            // Calculating the Change Percent B/W the 52 Week High And Low
            StockScreening stockScreening2 = new StockScreening(mysqlConnect);
            stockScreening2.filterStocks2();
        });

        borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.add(button0, 0, 0);
        gridPane.add(button1, 0, 1);
        gridPane.add(button2, 0, 2);
        gridPane.add(button3, 0, 3);
        gridPane.add(textFieldSingleStock, 1,3);
        gridPane.add(button4, 0, 4);
        gridPane.add(button5, 0, 5);
        gridPane.add(button6, 0, 6);
        gridPane.add(button7, 0, 7);
        gridPane.add(button8, 0, 8);
        gridPane.add(button9, 0, 9);
        borderPane.setLeft(gridPane);
        borderPane.setCenter(Screener.getGridPaneScreener());
        Tab tab = new Tab();
        tab.setContent(borderPane);
        return tab;
    }
    private static void insertSymbolsIntoTable(IEXTradingClient iexTradingClient, MysqlConnect mysqlConnect) {
        final List<ExchangeSymbol> exchangeSymbols = iexTradingClient.executeRequest(
                new SymbolsRequestBuilder().build());
        for (int i = 0; i < 8710; i++) {
            String symbols = exchangeSymbols.get(i).getSymbol();
            try {
                mysqlConnect.connect().setAutoCommit(false);
                PreparedStatement preparedStatement = mysqlConnect.connect().prepareStatement(
                        "insert into symbols(symbol) values (?)");
                preparedStatement.setString(1, symbols);
                preparedStatement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    private static void getResultSet(MysqlConnect mysqlConnect, IEXTradingClient iexTradingClient) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = mysqlConnect.connect().prepareStatement(
                    "select * from symbols"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Thread.sleep(50);
                System.out.printf("%n%s: ", resultSet.getString(1));
                Quote quotes = iexTradingClient.executeRequest(
                        new QuoteRequestBuilder().withSymbol(resultSet.getString(1)).build());
//                System.out.printf("%n%f", quotes.getAvgTotalVolume());
                enterIntoDB(mysqlConnect, quotes);

            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void enterIntoDB(MysqlConnect mysqlConnect, Quote quotes) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = mysqlConnect.connect().prepareStatement(
                    "INSERT INTO companyData(symbols,companyName, primaryExchange, sector, " +
                            "open, close, latestPrice, latestTime, latestVolume, " +
                            "changeInPrice, changePercent, avgTotalVolume, week52High, week52Low) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            enterValuesIntoPreparedStatement(preparedStatement, quotes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void enterValuesIntoPreparedStatement(PreparedStatement preparedStatement, Quote quotes) {
        try {
            preparedStatement.setString(1, quotes.getSymbol());
            preparedStatement.setString(2, quotes.getCompanyName());
            preparedStatement.setString(3, quotes.getPrimaryExchange());
            preparedStatement.setString(4, quotes.getSector());
            preparedStatement.setBigDecimal(5, quotes.getOpen());
            preparedStatement.setBigDecimal(6, quotes.getClose());
            preparedStatement.setBigDecimal(7, quotes.getLatestPrice());
            preparedStatement.setString(8, quotes.getLatestTime());
            preparedStatement.setBigDecimal(9, quotes.getLatestVolume());
            preparedStatement.setBigDecimal(10, quotes.getChange());
            preparedStatement.setBigDecimal(11, quotes.getChangePercent());
            preparedStatement.setBigDecimal(12, quotes.getAvgTotalVolume());
            preparedStatement.setBigDecimal(13, quotes.getWeek52High());
            preparedStatement.setBigDecimal(14, quotes.getWeek52Low());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateDataInCompanyData(MysqlConnect mysqlConnect, IEXTradingClient iexTradingClient) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = mysqlConnect.connect().prepareStatement(
                    "select * from symbols"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Thread.sleep(0);
                System.out.printf("%n%s: ", resultSet.getString(1));
                Quote quotes = iexTradingClient.executeRequest(
                        new QuoteRequestBuilder().withSymbol(resultSet.getString(1)).build());
                updateIntoDB(mysqlConnect, quotes);

            }
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void updateDataInCompanyDataSingleStock(MysqlConnect mysqlConnect, IEXTradingClient iexTradingClient, String s) {
        Quote quotes = iexTradingClient.executeRequest(new QuoteRequestBuilder().withSymbol(s).build());
        System.out.printf("%nUpdated Stock %s", s);
        updateIntoDB(mysqlConnect, quotes);
    }

    private static void updateIntoDB(MysqlConnect mysqlConnect, Quote quotes) {
        PreparedStatement preparedStatement1;
        try {
            preparedStatement1 = mysqlConnect.connect().prepareStatement(
                    "UPDATE companydata SET companyName = ?, primaryExchange = ?, " +
                            "sector = ?, open = ?, close = ?," +
                            "latestPrice = ?, latestTime = ?, latestVolume = ?, " +
                            "changeInPrice = ?, changePercent = ?, avgTotalVolume = ?," +
                            "week52High = ?, week52Low = ? WHERE symbols = ? "
            );
            enterUpdatedValuesIntoPreparedStatement(preparedStatement1, quotes);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static void enterUpdatedValuesIntoPreparedStatement(PreparedStatement preparedStatement, Quote quotes) {
        try {
            preparedStatement.setString(1, quotes.getCompanyName());
            preparedStatement.setString(2, quotes.getPrimaryExchange());
            preparedStatement.setString(3, quotes.getSector());
            preparedStatement.setBigDecimal(4, quotes.getOpen());
            preparedStatement.setBigDecimal(5, quotes.getClose());
            preparedStatement.setBigDecimal(6, quotes.getLatestPrice());
            preparedStatement.setString(7, quotes.getLatestTime());
            preparedStatement.setBigDecimal(8, quotes.getLatestVolume());
            preparedStatement.setBigDecimal(9, quotes.getChange());
            preparedStatement.setBigDecimal(10, quotes.getChangePercent());
            preparedStatement.setBigDecimal(11, quotes.getAvgTotalVolume());
            preparedStatement.setBigDecimal(12, quotes.getWeek52High());
            preparedStatement.setBigDecimal(13, quotes.getWeek52Low());
            preparedStatement.setString(14, quotes.getSymbol());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertingDataThatsNotThere(MysqlConnect mysqlConnect, IEXTradingClient client) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = mysqlConnect.connect().prepareStatement(
                    "select * from symbols"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Thread.sleep(0);

                System.out.printf("%n%s: ", resultSet.getString(1));
                PreparedStatement ps = mysqlConnect.connect().prepareStatement(
                        "SELECT * FROM companydata WHERE companydata.symbols = ?"
                );
                ps.setString(1, resultSet.getString(1));
                ResultSet resultSet1 = ps.executeQuery();
                if (!resultSet1.next()) {
                    try {
                        Quote quote = client.executeRequest(
                                new QuoteRequestBuilder().withSymbol(resultSet.getString(1)).build());
                        PreparedStatement ps1 = mysqlConnect.connect().prepareStatement(
                                "INSERT INTO companydata(symbols, companyName, primaryExchange, " +
                                        "sector, open, close, latestPrice, " +
                                        "latestTime, latestVolume, changeInPrice, " +
                                        "changePercent, avgTotalVolume, week52High, week52Low) " +
                                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                        );
                        ps1.setString(1, quote.getSymbol());
                        ps1.setString(2, quote.getCompanyName());
                        ps1.setString(3, quote.getPrimaryExchange());
                        ps1.setString(4, quote.getSector());
                        ps1.setBigDecimal(5, quote.getOpen());
                        ps1.setBigDecimal(6, quote.getClose());
                        ps1.setBigDecimal(7, quote.getLatestPrice());
                        ps1.setString(8, quote.getLatestTime());
                        ps1.setBigDecimal(9, quote.getLatestVolume());
                        ps1.setBigDecimal(10, quote.getChange());
                        ps1.setBigDecimal(11, quote.getChangePercent());
                        ps1.setBigDecimal(12, quote.getAvgTotalVolume());
                        ps1.setBigDecimal(13, quote.getWeek52High());
                        ps1.setBigDecimal(14, quote.getWeek52Low());
                        ps1.execute();
                        System.out.printf("%n New Symbol and Data Entered %s: ", quote.getSymbol());
                    } catch (IEXTradingException e) {
                        e.addSuppressed(new Throwable(e));
                    }
                } else {
                    System.out.printf("%n No Symbol and Data Entered");
                }

            }
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void consumeKeyStats(MysqlConnect mysqlConnect, IEXTradingClient client) {
        try {
            PreparedStatement preparedStatement1 = mysqlConnect.connect().prepareStatement(
                    "SELECT * FROM symbols"
            );
            ResultSet resultSet = preparedStatement1.executeQuery();
            while (resultSet.next()) {
                final KeyStats keyStats = client.executeRequest(
                        new KeyStatsRequestBuilder().withSymbol(resultSet.getString(1)).build());
                PreparedStatement preparedStatement;
                try {
                    preparedStatement = mysqlConnect.connect().prepareStatement(
                            "INSERT INTO keyStats(symbols, week52Change, year5ChangePercent, year2ChangePercent, " +
                                    "year1ChangePercent, ytd1ChangePercent, month6ChangePercent, month3ChangePercent, " +
                                    "month1ChangePercent, day5ChangePercent, day30ChangePercent) " +
                                    "VALUES (?,?,?,?,?,?,?,?,?,?,?);"
                    );
                    preparedStatement.setString(1, keyStats.getSymbol());
                    preparedStatement.setBigDecimal(2, keyStats.getWeek52change());
                    preparedStatement.setBigDecimal(3, keyStats.getYear5ChangePercent());
                    preparedStatement.setBigDecimal(4, keyStats.getYear2ChangePercent());
                    preparedStatement.setBigDecimal(5, keyStats.getYear1ChangePercent());
                    preparedStatement.setBigDecimal(6, keyStats.getYtdChangePercent());
                    preparedStatement.setBigDecimal(7, keyStats.getMonth6ChangePercent());
                    preparedStatement.setBigDecimal(8, keyStats.getMonth3ChangePercent());
                    preparedStatement.setBigDecimal(9, keyStats.getMonth1ChangePercent());
                    preparedStatement.setBigDecimal(10, keyStats.getDay5ChangePercent());
                    preparedStatement.setBigDecimal(11, keyStats.getDay30ChangePercent());
                    preparedStatement.execute();
                    System.out.printf("%nSymbol Added : %s ", keyStats.getSymbol());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException | IEXTradingException ee) {
            ee.addSuppressed(new Throwable(ee));
        }

    }
    private static void insertingDataThatsNotThereKeyStats(MysqlConnect mysqlConnect, IEXTradingClient client) {
        PreparedStatement preparedStatement;
        try {
            preparedStatement = mysqlConnect.connect().prepareStatement(
                    "select * from symbols"
            );
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Thread.sleep(0);

                System.out.printf("%n%s: ", resultSet.getString(1));
                PreparedStatement ps = mysqlConnect.connect().prepareStatement(
                        "SELECT * FROM keystats WHERE keystats.symbols = ?"
                );
                ps.setString(1, resultSet.getString(1));
                ResultSet resultSet1 = ps.executeQuery();
                if (!resultSet1.next()) {
                    try {
                        KeyStats keyStats = client.executeRequest(
                                new KeyStatsRequestBuilder().withSymbol(resultSet.getString(1)).build());
                        PreparedStatement ps1 = mysqlConnect.connect().prepareStatement(
                                "INSERT INTO keystats(symbols, week52Change, year5ChangePercent, " +
                                        "year2ChangePercent, year1ChangePercent, ytd1ChangePercent, " +
                                        "month6ChangePercent, month3ChangePercent, month1ChangePercent, " +
                                        "day5ChangePercent, day30ChangePercent) " +
                                        "VALUES (?,?,?,?,?,?,?,?,?,?,?) "
                        );
                        ps1.setString(1, keyStats.getSymbol());
                        ps1.setBigDecimal(2, keyStats.getWeek52change());
                        ps1.setBigDecimal(3, keyStats.getYear5ChangePercent());
                        ps1.setBigDecimal(4, keyStats.getYear2ChangePercent());
                        ps1.setBigDecimal(5, keyStats.getYear1ChangePercent());
                        ps1.setBigDecimal(6, keyStats.getYtdChangePercent());
                        ps1.setBigDecimal(7, keyStats.getMonth6ChangePercent());
                        ps1.setBigDecimal(8, keyStats.getMonth3ChangePercent());
                        ps1.setBigDecimal(9, keyStats.getMonth1ChangePercent());
                        ps1.setBigDecimal(10, keyStats.getDay5ChangePercent());
                        ps1.setBigDecimal(11, keyStats.getDay30ChangePercent());

                        ps1.execute();
                        System.out.printf("%n New Symbol and Data Entered %s: ", keyStats.getSymbol());
                    } catch (IEXTradingException e) {
                        e.addSuppressed(new Throwable(e));
                    }
                } else {
                    System.out.printf("%n No Symbol and Data Entered");
                }

            }
        } catch (InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }


}