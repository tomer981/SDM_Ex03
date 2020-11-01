package sdm.servlet;


import com.google.gson.Gson;
import market.Market;
import xmlBuild.schema.generated.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static sdm.constants.Constants.*;

class SellInfo {
    public int id;
    public int price;


    public SDMSell toSale() {
        SDMSell sale = new SDMSell();

        sale.setPrice(price);
        sale.setItemId(id);

        return sale;
    }
}

class StoreInfo {
    public String name;
    public int coordX;
    public int coordY;
    public int ppk;
    public SellInfo[] sales;

    public SDMStore toStore() {
        Location loc = new Location();
        loc.setX(coordX);
        loc.setY(coordY);

        SDMPrices prices = new SDMPrices();
        prices.getSDMSell().addAll(
                Arrays.stream(sales).map(SellInfo::toSale).collect(Collectors.toList())
        );

        SDMStore store = new SDMStore();
        store.setName(name);
        store.setDeliveryPpk(ppk);
        store.setLocation(loc);
        store.setSDMPrices(prices);
        store.setSDMDiscounts(new SDMDiscounts());

        return store;
    }
}

@WebServlet(name = "ManagerServlet", urlPatterns = {"/ManagerServlet"})
//@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ManagerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        StoreInfo storeInfo = gson.fromJson(req.getReader(), StoreInfo.class);

        Market market = Market.getMarketInstance();
        String name = (String) req.getSession().getAttribute(USER_NAME);
        String zone = (String) req.getSession().getAttribute(ZONE_NAME);

        market.addStoreToManager(name, zone, storeInfo.toStore());
    }
}
