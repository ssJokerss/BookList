package com.jnu.booklist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jnu.booklist.data.BookFragmentAdapter;
import com.jnu.booklist.data.BookListFragment;
import com.jnu.booklist.data.BookSaver;
import com.jnu.booklist.data.MapViewFragment;
import com.jnu.booklist.data.WebViewFragment;
import com.jnu.booklist.data.model.Book;

import java.util.ArrayList;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class BookListMainActivity extends AppCompatActivity {
    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_ADDNEW = CONTEXT_MENU_DELETE+1;
    public static final int CONTEXT_MENU_UPDATE = CONTEXT_MENU_ADDNEW+1;
    public static final int CONTEXT_MENU_ABOUT = CONTEXT_MENU_UPDATE+1;
    public static final int REQUEST_CODE_NEW_BOOK = 901;
    public static final int REQUEST_CODE_UPDATE_BOOK= 902;
    //ListView ListViewBooks;//创建视图
    BookAdapter bookAdapter;//自定义适配器BookAdapter
    private List<Book> listBooks = new ArrayList<>();//创建ArrayList
    BookSaver bookSaver;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        bookSaver.save();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        bookSaver=new BookSaver(this);//少了个这个
        listBooks = bookSaver.load();
        if (listBooks.size()==0)
            init();

        bookAdapter = new BookAdapter(
                BookListMainActivity.this, R.layout.list_view_item_book, listBooks);//加不加android有什么区别吗
        BookFragmentAdapter myPageAdapter = new BookFragmentAdapter(getSupportFragmentManager());

        ArrayList<Fragment> datas = new ArrayList<Fragment>();
        datas.add(new BookListFragment(bookAdapter));//将适配器放入bookAdapter函数
        datas.add(new WebViewFragment());
        datas.add(new MapViewFragment());
        myPageAdapter.setData(datas);

        ArrayList<String> titles = new ArrayList<String>();
        titles.add("图书");
        titles.add("新闻");
        titles.add("卖家");
        myPageAdapter.setTitles(titles);

        //将TabLayout和ViewPager相关联
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(myPageAdapter);//设置适配器
        tabLayout.setupWithViewPager(viewPager);//


    }

    @Override//重写的生成上下文菜单
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v == findViewById(R.id.list_view_books)) {
            //获取适配器
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //设置标题
            menu.setHeaderTitle(listBooks.get(info.position).getTitle());
            //设置内容 参数1为分组，参数2对应条目的id，参数3是指排列顺序，默认排列即可
            //用const 将常数变为宏变量 更具连贯性
            menu.add(0, CONTEXT_MENU_DELETE, 0, "删除");
            menu.add(0, CONTEXT_MENU_ADDNEW, 0, "新建");
            menu.add(0, CONTEXT_MENU_UPDATE, 0, "修改");
            menu.add(0, CONTEXT_MENU_ABOUT, 0, "关于...");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_NEW_BOOK:
                if (resultCode == RESULT_OK){
                    String title = data.getStringExtra("title");
                    int insertPosition=data.getIntExtra("insert_position",0);
                    listBooks.add(insertPosition,new Book(title,R.drawable.book3));
                    bookAdapter.notifyDataSetChanged();//通知数据已经改变
                }
                break;
            case REQUEST_CODE_UPDATE_BOOK:
                if (resultCode == RESULT_OK){
                    int insertPosition=data.getIntExtra("insert_position",0);
                    Book bookAtPostion = listBooks.get(insertPosition);
                    bookAtPostion.setTitle(data.getStringExtra("title"));

                    bookAdapter.notifyDataSetChanged();//通知数据已经改变
                }
                break;
        }

    }

    @Override
    //类别选择
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position=menuInfo.position;
        switch (item.getItemId()) {
            case CONTEXT_MENU_DELETE :
                //删除一个图片项目
                /*
                AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                listBooks.remove(info.position);
                bookAdapter.notifyDataSetChanged();
                Toast.makeText(BookListMainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                break;
                */
                //引入这个对话框的包
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("询问");
                builder.setMessage("你确定要删除\""+listBooks.get(position).getTitle()+ "\"？");
                builder.setCancelable(true);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listBooks.remove(position);
                        bookAdapter.notifyDataSetChanged();//数据更新
                    }
                });  //正面的按钮（肯定）
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    //点击响应函数
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }); //反面的按钮（否定)
                builder.create().show();
                Toast.makeText(BookListMainActivity.this,"删除成功",Toast.LENGTH_LONG).show();
                break;

            case CONTEXT_MENU_ADDNEW:
                Intent intent = new Intent(this,EditBookActivity.class);
                intent.putExtra("title","无名书籍");
                intent.putExtra("insert_position","无名书籍");
                startActivityForResult(intent, REQUEST_CODE_NEW_BOOK);
                /*
                listBooks.add(position+1,new Book("无名书籍",R.drawable.book3));
                bookAdapter.notifyDataSetChanged();//通知数据已经改变
                */
                break;
            case CONTEXT_MENU_UPDATE:{
                int Position = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                Intent intent2 = new Intent(this,EditBookActivity.class);
                intent2.putExtra("title",listBooks.get(Position).getTitle());
                intent2.putExtra("insert_position",Position);
                startActivityForResult(intent2,REQUEST_CODE_UPDATE_BOOK);

            }
            break;
            case CONTEXT_MENU_ABOUT:
                Toast.makeText(BookListMainActivity.this,"图书列表V1.0,coded by casper",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onContextItemSelected(item);
    }

    //初始化函数
    private void init() {
        listBooks.add(new Book("信息安全数学基础（第2版）",R.drawable.book1));
        listBooks.add(new Book("软件项目管理案例教程（第4版）",R.drawable.book2));
        listBooks.add(new Book("创新工程实践",R.drawable.book3));
    }
    //自定义适配器BookAdapter
    public class BookAdapter extends ArrayAdapter<Book> {

        private int resourceId;

        public BookAdapter(Context context, int resource, List<Book> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Book book = getItem(position);//获取当前项的实例
            View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            ((ImageView) view.findViewById(R.id.image_view_book_cover)).setImageResource(book.getCoverResourceId());//显示图片
            ((TextView) view.findViewById(R.id.text_view_book_title)).setText(book.getTitle());//显示名称
            return view;
        }
    }
}