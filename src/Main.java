import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        Book book = new Book("Harry Potter and the Philosopher's Stone", "J.K.Rowling", "978-4863890077");
        System.out.println("title: " + book.title + ",author: " + book.author + ",isbn: " + book.isbn);


        ScannerUtil scUtil = new ScannerUtil();

        System.out.println(scUtil.isbnPatternDigitCheck("978-4863890077"));

//		Front front = new Front();

//		front.registerNewBook();


    }
}

// if(public final variable == (private final variable + getter)) {
// delete getter and replace private final variable to public final variable
// }

//class Book {
//    private final String title; // maybe public ?
//    private final String author;
//    private final String isbn;
//    private boolean isOutOnLoan;
//    // when ppl borrow a book, this User variable hold info about who is borrowing. and when ppl returen the book,
//    // use this User info to remove the book from their borrowList.
//    private Optional<User> whoBorrowMe;
//
//    Book(String title, String author, String isbn) {
//        this.title = title;
//        this.author = author;
//        this.isbn = isbn;
//        isOutOnLoan = false;
//        whoBorrowMe = Optional.empty();
//    }
//
//    public void lendingBook(User user) {
//        isOutOnLoan = true;
//        whoBorrowMe = Optional.of(user);
//        user.borrowingBook(this);
//    }
//
//    public void returnOfBook() {
//        isOutOnLoan = false;
//        if (whoBorrowMe.isPresent()) whoBorrowMe.get().returningBook(this);
//        else {
//            //if else, search all users of borrowList
//            //my first attempt was making static method on library that will search all user borrowing data
//            //but static makes static everything and that's annoying as f-
//
//
//        }
//    }
//
//    public boolean isOutOnLoan() {
//        return isOutOnLoan;
//    }
//
//    public String getBookInfo() {
//        return "Title: \"" + title + "\" Author: \"" + author + "\" isbn: \"" + isbn + "\"";
//    }
//
//}

class Book {
    public final String title;
    public final String author;
    public final String isbn;
    public boolean isOutOnLoan;
    // when ppl borrow a book, this User variable hold info about who is borrowing. and when ppl returen the book,
    // use this User info to remove the book from their borrowList.
    public Optional<User> whoBorrowMe;

    Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        isOutOnLoan = false;
        whoBorrowMe = Optional.empty();
    }

    public void lendingBook(User user) {
        isOutOnLoan = true;
        whoBorrowMe = Optional.of(user);
        user.borrowingBook(this);
    }

    public void returnOfBook() {
        isOutOnLoan = false;
        // I already judge whoBorrowMe has instance
        whoBorrowMe.get().returningBook(this);
        whoBorrowMe = Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Book book = (Book) obj;
        return isbn.equals(book.isbn);
    }
}




class User {
    public final String userID;
    public final String name;
    public final List<Book> borrowList;

    User(String userID, String name) {
        this.userID = userID;
        this.name = name;
        borrowList = new ArrayList<>();
    }

    public void borrowingBook(Book book) {
        borrowList.add(book);
    }

    public void returningBook(Book book) {
        borrowList.remove(book);
    }

}

// im just making class only if code looks cooler. so i dont know what encapsulation looks like.
// and also im making variable private, cuz it feels like so programmer-ish.
class Library {
    private final List<Book> bookList;
    private final List<User> userList;

    Library() {
        bookList = new ArrayList<>();
        userList = new ArrayList<>();
    }
//  -------------------for registering ----------------------
    public void registerBook(Book book) {
        bookList.add(book);
    }

    public void registerUser(User user) {
        userList.add(user);
    }

//  --------------------for rending and returning------------

    public void lendsBook(Book book, User user) {
        book.lendingBook(user);
    }

    public void getBookBack(Book book) {
        book.returnOfBook();
    }
// ---------------------when book is not returned properly-------------
    public void forceGetBookBack(Book whosBook){
        whosBook.returnOfBook();
        for(User user : userList){
            for(Book book : user.borrowList){
                if(book.equals(whosBook)) user.returningBook(whosBook);
            }
        }
    }

//  --------------------for general searching-------------------
	public List<Book> searchBookByTitle(String title){
        Pattern p = Pattern.compile(".*" + Pattern.quote(title) + ".*");
        List<Book> resultList = new ArrayList<>();
        for(Book book : bookList){
            Matcher m = p.matcher(book.title);
            if(m.find()) resultList.add(book);
        }
        return resultList;
	}

	public List<Book> searchBookByAuthor(String author){
        Pattern p = Pattern.compile(".*" + Pattern.quote(author) + ".*");
        List<Book> resultList = new ArrayList<>();
        for(Book book : bookList){
            Matcher m = p.matcher(book.author);
            if(m.find()) resultList.add(book);
        }
        return resultList;
	}

	public Optional<Book> searchBookByIsbn(String isbn){
        Optional<Book> theBook = Optional.empty();
        for(Book book : bookList){
            if(isbn.equals(book.isbn)) theBook = Optional.of(book);
        }
        return theBook;
	}


    //public User searchUserByName(String name){
//		//general expression
//	}
//
//	public User searchUserByUserID(String userID){
//		//general expression
//	}

    public List<String> getUserIDList() {
        return userList.stream()
                .map(user -> user.userID)
                .toList();

    }


}

class ScannerUtil {
    /* reasons:
        1.for breaking line(next() and nextInt())
        2.for approving
        3.for isbn-pattern-check + isbn-digit-check (13ver)
    */
    private final String isbnPatternTemplate = "(ISBN)?97[89]([- ])(?=.{13}$)\\d{1,5}\\2\\d{1,7}\\2\\d{1,6}\\2\\d|(ISBN)?97[89]\\d{10}|(ISBN)?97[89]([- ])\\d{10}";
    private final Pattern isbnPattern = Pattern.compile(isbnPatternTemplate);

    private final Scanner sc;

    ScannerUtil() {
        sc = new Scanner(System.in);
    }


    // isbn-digit-check worked properly
    public boolean isbnPatternDigitCheck(String isbn) {
        boolean flag = false;

        if (isbnPattern.matcher(isbn).matches()) {
            isbn = isbn.replace("ISBN", "").replace("-", "").replace(" ", "");
            char[] isbnChar = isbn.toCharArray();
            int[] isbnInt = new int[isbnChar.length];
            for (int i = 0; i < isbnChar.length; i++) {
                isbnInt[i] = Character.getNumericValue(isbnChar[i]);
            }

            int sumOdd = 0;
            int sumEven = 0;
            for (int i = 0; i < isbnInt.length - 1; i++) {
                if (i % 2 == 0) sumOdd += isbnInt[i];
                else sumEven += isbnInt[i];
            }
            sumEven *= 3;

//			System.out.println("odd:" + sumOdd + " /even:" + sumEven);

            int sum = sumOdd + sumEven;
            int extra = sum % 10;
            int judgeNum = 10 - extra;
//			System.out.println("judgeNum:" + judgeNum + " /isbn13th digit:" + isbnInt[12] );
            if (isbnInt[12] == judgeNum) flag = true;
        }
//		else System.out.println("Not matched");
        return flag;
    }


    public String strCheck(String question) {
        String answer;
        while (true) {
            System.out.print(question);
            answer = sc.nextLine();
            System.out.println("Can you confirm \"" + answer + "\"? y/n");
            String YorN = sc.next();
            sc.nextLine();
            if (YorN.equals("y")) break;
        }
        return answer;
    }

    public String isbnCheck(String question) {
        String isbn;
        while (true) {
            System.out.print(question);
            isbn = sc.next();
            sc.nextLine();
            System.out.println("Can you confirm \"" + isbn + "\"? y/n");
            String YorN = sc.next();
            sc.nextLine();
            if (YorN.equals("y")) {
                if (isbnPatternDigitCheck(isbn)) break;
                else System.out.println("Pattern or Digit error.");
            }
        }
        return isbn;
    }

}

class Front {
    // Front will be accessed directly from the main method, and front will access the rest of the classes
    // how to get instance only from Front
    // cuz I don't want to see main method get Book instance directly.

    private final Library library;
    private final ScannerUtil scUtil;

    Front() {
        library = new Library();
        scUtil = new ScannerUtil();
    }

    //library duplicating id checking
    private String makeID() {
        List<String> idList = library.getUserIDList();
        while (true) {
            double random = Math.random() * 10000;
            String id = String.valueOf((int) random);
            boolean isDuplicating = idList.stream()
                    .anyMatch(ID -> ID.equals(id));
            if (!isDuplicating) return id;
        }
    }

    public void registerNewBook() {
        String title = scUtil.strCheck("Type the title of book: ");
        String author = scUtil.strCheck("Type the author of book: ");
        String isbn = scUtil.isbnCheck("Type the ISBN of book: ");
        library.registerBook(new Book(title, author, isbn));
    }

    public void registerNewUser() {
        String userName = scUtil.strCheck("Type your name: ");
        library.registerUser(new User(userName, makeID()));

    }

    public void getLibraryBookData() {

    }


}

