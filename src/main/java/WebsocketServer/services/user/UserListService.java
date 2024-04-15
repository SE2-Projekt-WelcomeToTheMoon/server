package WebsocketServer.services.user;

/**
 * Provides access to the methods needed to manage the user list.
 */
public class UserListService {
    public static final ManageUserService userList = new ManageUserService();

    private UserListService() {}
}
