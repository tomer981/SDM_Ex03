package sdm.servlet;


import com.google.gson.Gson;
import market.Market;
import xml.schema.generated.Location;
import xml.schema.generated.SDMStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static sdm.constants.Constants.*;

class AddStore {
    public String name;
    public int coordX;
    public int coordY;
    public int ppk;

    public SDMStore toStore() {
        Location loc = new Location();
        loc.setX(coordX);
        loc.setY(coordY);

        SDMStore store = new SDMStore();
        store.setName(name);
        store.setDeliveryPpk(ppk);
        store.setLocation(loc);

        return store;
    }
}

@WebServlet(name = "ManagerServlet", urlPatterns = {"/ManagerServlet"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class ManagerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        AddStore addStore = gson.fromJson(req.getReader(), AddStore.class);

        System.out.println("Add Store: " + addStore);

        Market market = Market.getMarketInstance();
        String name = (String) req.getSession().getAttribute(USER_NAME);
        String zone = (String) req.getSession().getAttribute(ZONE_NAME);

        market.addStoreToManager(name, zone, addStore.toStore());

        super.doPut(req, resp);
    }
}
