package lev.ariel.arielbookstore;

import android.widget.ImageView;

// This class represents a Book object with all relevant properties
public class Book {

    // The image resource ID representing the book cover (from drawable)
    private int bookImage;

    // Unique ISBN identifier for the book
    private String bookIsbn;

    // Title of the book
    private String bookName;

    // Author of the book
    private String bookAuthor;

    // Short description or summary of the book
    private String bookDescription;

    // Genre/category of the book (e.g., Fiction, Biography)
    private String bookGenre;

    // Number of pages in the book
    private int bookLength;

    // Price of the book
    private float bookPrice;
    private String sellerEmail;

    // Full constructor to initialize all fields when creating a new Book object
    public Book(int bookImage, String bookIsbn, String bookName, String bookAuthor,
                String bookDescription, String bookGenre, int bookLength, float bookPrice) {
        this.bookImage = bookImage;
        this.bookIsbn = bookIsbn;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookDescription = bookDescription;
        this.bookGenre = bookGenre;
        this.bookLength = bookLength;
        this.bookPrice = bookPrice;
    }

    // Empty constructor (needed for Firebase or serialization)
    public Book() {
    }

    // Getter and setter methods for each field:
    // Used to read and update the properties of the book

    public int getBookImage() {
        return bookImage;
    }

    public void setBookImage(int bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(String bookGenre) {
        this.bookGenre = bookGenre;
    }

    public int getBookLength() {
        return bookLength;
    }

    public void setBookLength(int bookLength) {
        this.bookLength = bookLength;
    }

    public float getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(float bookPrice) {
        this.bookPrice = bookPrice;
    }
    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    // Overrides the default toString() method to return all book details as a single string
    @Override
    public String toString() {
        return "Book{" +
                "bookImage=" + bookImage +
                ", bookIsbn='" + bookIsbn + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                ", bookDescription='" + bookDescription + '\'' +
                ", bookGenre='" + bookGenre + '\'' +
                ", bookLength=" + bookLength +
                ", bookPrice=" + bookPrice +
                '}';
    }
}
