package martin;

import pl.zankowski.iextrading4j.client.IEXTradingClient;

public class IEX {
    private static IEXTradingClient iexTradingClient;
    public IEX() {

    }
    public static IEXTradingClient initiateIEX() {
        iexTradingClient = IEXTradingClient.create();
        return iexTradingClient;
    }

}
