package br.edu.insper.al.leonardomm4.bookdatabase;

public class Item {

        String bookListName;
        int bookListImage;
        int bookListId;

        public Item(String bookName,int bookImage,int id)
        {
            this.bookListImage=bookImage;
            this.bookListName=bookName;
        }
        public String getbookName()
        {
            return bookListName;
        }
        public int getbookImage() {
            return bookListImage;
        }
        public int getbookId() {
        return bookListId;
    }
}
