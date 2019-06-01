package br.edu.insper.al.leonardomm4.bookdatabase;

public class Item {

        String bookListName;
        String bookListAuthor;
        int bookListImage;
        int bookListId;

        public Item(String bookName,String bookAuthor,int bookImage,int id)
        {
            this.bookListImage=bookImage;
            this.bookListAuthor=bookAuthor;
            this.bookListName=bookName;
        }
        public String getbookName()
        {
            return bookListName;
        }
        public String getbookAuthor()
    {
        return bookListAuthor;
    }
        public int getbookImage() {
            return bookListImage;
        }
        public int getbookId() {
        return bookListId;
    }
}
