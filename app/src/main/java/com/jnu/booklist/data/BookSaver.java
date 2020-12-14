package com.jnu.booklist.data;

import android.content.Context;

import com.jnu.booklist.data.model.Book;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class BookSaver {
    public BookSaver(Context context) {
        this.context = context;
    }

    Context context;//读写内部文件

    public ArrayList<Book> getBooks() {
        return books;
    }

    ArrayList<Book> books=new ArrayList<Book>();

    public void save()
    {//捕获异常；引入必要的包，修改要序列化的对象，修改打开文件输出流
        //数值序列化
        try{
            ObjectOutputStream outputStream = new ObjectOutputStream(

                    context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(books);
            outputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<Book> load()
    {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt"));
            books = (ArrayList<Book>) inputStream.readObject();
            inputStream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return books;
    }
}
