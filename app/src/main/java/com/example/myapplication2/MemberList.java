package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import static com.example.myapplication2.Index.*;


public class MemberList extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_list);
        Context _this = MemberList.this;
        ItemList query = new ItemList(_this);
        ListView memberList = findViewById(R.id.memberList);
        memberList.setAdapter(
                new MemberAdapter(_this, (ArrayList<Member>)new ISupplier() {
                    @Override
                    public Object get() {
                        return query.get();
                    }
                }.get())
        );
        memberList.setOnItemClickListener((AdapterView<?> p, View v, int i, long l)->{
            Member m = (Member)memberList.getItemAtPosition(i);
            Toast.makeText(_this,m.name, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(_this, MemberDetail.class);
            intent.putExtra("seq",m.seq);
            startActivity(intent);
        });
    }
    private class MemberListQuery extends QueryFactory{
        SQLiteOpenHelper helper;
        public MemberListQuery(Context _this) {
            super(_this);
            helper = new SQLiteHelper(_this);
        }
        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class ItemList extends MemberListQuery{
        public ItemList(Context _this) {
            super(_this);
        }
        public ArrayList<Member> get(){
            ArrayList<Member> list = new ArrayList<>();
            Cursor c = this.getDatabase().rawQuery(
                    " SELECT * FROM MEMBER ", null
            );
            Member m = null;
            if(c != null){
                while(c.moveToNext()){
                    m = new Member();
                    m.seq = Integer.parseInt(c.getString(c.getColumnIndex(MSEQ)));
                    m.name = c.getString(c.getColumnIndex(MNAME));
                    m.pw = c.getString(c.getColumnIndex(MPW));
                    m.email = c.getString(c.getColumnIndex(MEMAIL));
                    m.phone = c.getString(c.getColumnIndex(MPHONE));
                    m.addr = c.getString(c.getColumnIndex(MEMAIL));
                    m.photo = c.getString(c.getColumnIndex(MPHOTO));
                    Log.d("멤버정보: ", m.name);
                    list.add(m);
                }
            }else{
                Toast.makeText(_this, "등록된 회원이 없음",Toast.LENGTH_LONG).show();
            }
            return list;
        }
    }
    private class MemberAdapter extends BaseAdapter{
        ArrayList<Member> list;
        LayoutInflater inflater;
        Context _this;
        public MemberAdapter(Context _this,ArrayList<Member> list ) {
            this.list = list;
            this._this = _this;
            this.inflater = LayoutInflater.from(_this);
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object getItem(int i) {
            return list.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View v, ViewGroup g) {
            ViewHolder holder;
            if(v == null){
                v = inflater.inflate(R.layout.member_item, null);
                holder = new ViewHolder();
                holder.photo = v.findViewById(R.id.profile);
                holder.name = v.findViewById(R.id.name);
                holder.phone = v.findViewById(R.id.phone);
                v.setTag(holder);
            }else{
                holder = (ViewHolder) v.getTag();
            }
            ItemPhoto query = new ItemPhoto(_this);
            query.seq = list.get(i).seq+"";
            holder.photo
                    .setImageDrawable(
                            getResources()
                                    .getDrawable(
                                            getResources()
                                                    .getIdentifier(
                                                            _this.getPackageName()+":drawable/"
                                                                    +((String)new ISupplier() {
                                                                @Override
                                                                public Object get() {
                                                                    return query.get();
                                                                }
                                                            }.get()),
                                                            null,
                                                            null),
                                            _this.getTheme()
                                    )
                    );
            holder.name.setText(list.get(i).name);
            holder.phone.setText(list.get(i).phone);
            return v;
        }
    }
    static class ViewHolder{
        ImageView photo;
        TextView name, phone;
    }
    //================================================================================================
    private class MemberPhotoQuery extends QueryFactory{
        SQLiteOpenHelper helper;
        public MemberPhotoQuery(Context _this) {
            super(_this);
            helper = new SQLiteHelper(_this);
        }
        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }
    private class ItemPhoto extends MemberListQuery{
        String seq;
        public ItemPhoto(Context _this) {
            super(_this);
        }

        public String get(){
            Cursor c = getDatabase()
                    .rawQuery(String.format(
                            " SELECT %s FROM %s "
                                    +" WHERE %s LIKE '%s' "
                            ,MPHOTO, MEMBER,
                            MSEQ, seq), null
                    );
            String result = "";
            if(c != null){
                if(c.moveToNext()){
                    result = c.getString(c.getColumnIndex(MPHOTO));
                }
            }
            return result;
        }
    }

}