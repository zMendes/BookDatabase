package br.edu.insper.al.leonardomm4.bookdatabase;

public class Item {

        String bookListName;
        int bookListImage;
        String bookListImageString;
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
        public String getbookImageString()
    {
        return bookListImageString;
    }
        public void setbookImageString(String imageString){
            this.bookListImageString = imageString;
        }

}
