import java.util.ArrayList;

/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final int id;
    private final ArrayList<String> cart;

    private final ArrayList<String> movieIds;

    public User(int id) {
        this.id = id;
        this.cart = new ArrayList<String>();
        this.movieIds = new ArrayList<String>();

    }

    public int getId() {
        return id;
    }

    public ArrayList<String> getCart() { return cart; }

    public ArrayList<String> getMovieIds() { return movieIds; }

    public void addToCart(String title) { cart.add(title); };
}