package lev.ariel.arielbookstore;

public class Book
{
    private int bookImage;
    private String bookIsbn;
    private String bookName;
    private String bookAuthor;
    private String bookDescription;
    private Genre bookGenre;
    private int bookLength;
    private float bookPrice;


    public Book(int bookImage, String bookIsbn, String bookName, String bookAuthor, String bookDescription, Genre bookGenre, int bookLength, float bookPrice) {
        this.bookImage = bookImage;
        this.bookIsbn = bookIsbn;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookDescription = bookDescription;
        this.bookGenre = bookGenre;
        this.bookLength = bookLength;
        this.bookPrice = bookPrice;
    }

    public Book() {
    }

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

    public Genre getBookGenre() {
        return bookGenre;
    }

    public void setBookGenre(Genre bookGenre) {
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
}
