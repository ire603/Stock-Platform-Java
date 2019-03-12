package martin;

import pl.zankowski.iextrading4j.api.stocks.News;
import pl.zankowski.iextrading4j.client.IEXTradingClient;
import pl.zankowski.iextrading4j.client.rest.request.stocks.NewsRequestBuilder;

import java.util.List;

public class NewsData {
	
	private static List<News> news;

	public static void main(String[] args) {
		final IEXTradingClient iexTradingClient = IEXTradingClient.create();
		final List<News> newsList = iexTradingClient.executeRequest(new NewsRequestBuilder().withSymbol("AAPL")
				.build());
        String headline = newsList.get(0).getHeadline();
        System.out.println(headline);
	}
	public static List<News> getNews(String symbol) {
		IEXTradingClient iexTradingClient = IEXTradingClient.create();
		news = iexTradingClient.executeRequest(new NewsRequestBuilder().withSymbol(symbol).build());
		System.out.printf("%n%s", news.get(0).getHeadline());
		return news;
	}
}
