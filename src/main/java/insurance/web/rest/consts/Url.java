package insurance.web.rest.consts;

public class Url {

    public static final String POST_QUOTE_CREATE = "/api/v1/quote/create";
    public static final String GET_QUOTE_RETRIEVE = "/api/v1/quote/get/{id}";
    public static final String PUT_QUOTE_UPDATE = "/api/v1/quote/update";
    public static final String DELETE_QUOTE_REMOVE = "/api/v1/quote/delete/{id}";
    public static final String POST_QUOTE_LIST = "/api/v1/quote/list";
    public static final String POST_QUOTE_AGGREGATION = "/api/v1/quote/aggregation";

    private Url() {
    }

}
