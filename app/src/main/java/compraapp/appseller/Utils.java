package compraapp.appseller;

public class Utils {
    public static final String SERVER_URL = "http://172.29.1.8:50083/api/";
    public static int seller_id = 0;

    public static String delivery[] = {"Entrega", "Retirar"};
    public static String item_states[] = {"Nuevo", "Usado"};

    public static int OFFER_OPEN = 0;
    public static int OFFER_ACTION_REJECTED = 1;
    public static int OFFER_ACTION_ACCEPTED = 2;

    public static int PUBLICATION_OPEN = 0;
    public static int PUBLICATION_CLOSE_BY_ACEPTED_OFFER = 1;
    public static int PUBLICATION_CLOSE_BY_USER = 2;
}
