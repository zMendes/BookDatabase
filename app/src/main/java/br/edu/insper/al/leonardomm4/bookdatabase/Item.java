package br.edu.insper.al.leonardomm4.bookdatabase;

public class Item {

        private String bookListName;
        private String bookListAuthor;
        private int bookListImage;
        private String bookListImageFile;
        private int bookListId;

        public Item(String bookName,String bookAuthor,int bookImage,int id)
        {
            this.bookListImage=bookImage;
            this.bookListAuthor=bookAuthor;
            this.bookListName=bookName;
            this.bookListId=id;
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
        public String getbookImageFile()
    {
        return bookListImageFile;
    }
        public void setbookImageFile(String imageFile){
            this.bookListImageFile = imageFile;
        }

}
