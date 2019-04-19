package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import static com.example.myapplication2.Index.*;

public class MemberDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_detail);
        Context _this = MemberDetail.this;
        Intent intent = this.getIntent();
        int seq = intent.getExtras().getInt("seq");
        Toast.makeText(_this, "넘어온 시퀀스 : " + seq, Toast.LENGTH_LONG).show();
        ItemDetail detail = new ItemDetail(_this);
        Index.Member m = (Member) new ISupplier() {
            @Override
            public Object get() {
                return detail.get(seq);
            }
        }.get();
        TextView name = findViewById(R.id.name);
        name.setText(m.name);
        TextView email = findViewById(R.id.email);
        email.setText(m.email);
        TextView phone = findViewById(R.id.phone);
        phone.setText(m.phone);
        TextView addr = findViewById(R.id.addr);
        addr.setText(m.addr);
        ItemPhoto query = new ItemPhoto(_this);
        ImageView imageView = findViewById(R.id.profile);
        imageView.setImageDrawable(
                getResources()
                        .getDrawable(
                                getResources()
                                        .getIdentifier(
                                                _this.getPackageName() + ":drawable/"
                                                        + ((String) new Index.ISupplier() {
                                                    @Override
                                                    public Object get() {
                                                        return query.get(seq);
                                                    }
                                                }.get()),
                                                null,
                                                null),
                                _this.getTheme()
                        )
        );
        findViewById(R.id.callBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.dialBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.smsBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.emailBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.albumBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.movieBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.mapBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.musicBtn).setOnClickListener(
                (v) -> {
                }
        );
        findViewById(R.id.updateBtn).setOnClickListener(
                (v) -> {
                    Intent intent2 = new Intent(_this, MemberUpdate.class);
                    intent2.putExtra("spec",
                            m.seq + "," +
                                    m.name + "," +
                                    m.pw + "," +
                                    m.email + "," +
                                    m.phone + "," +
                                    m.addr + "," +
                                    m.photo
                    );
                    startActivity(intent2);
                });
        findViewById(R.id.listBtn).setOnClickListener(
                (v) -> {
                    startActivity(new Intent(_this, MemberList.class));
                }
        );
    }

    private class MemberDetailQuery extends Index.QueryFactory {
        SQLiteOpenHelper helper;

        public MemberDetailQuery(Context _this) {
            super(_this);
            helper = new Index.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }

    private class ItemDetail extends MemberDetailQuery {
        public ItemDetail(Context _this) {
            super(_this);
        }

        public Index.Member get(int seq) {
            Index.Member m = new Index.Member();
            Cursor c = this.getDatabase().rawQuery(String.format("SELECT * FROM MEMBER WHERE %s LIKE %s", Index.MSEQ, seq), null);
            if (c != null) {
                if (c.moveToNext()) {
                    m.name = c.getString(c.getColumnIndex(Index.MNAME));
                    m.pw = c.getString(c.getColumnIndex(Index.MPW));
                    m.email = c.getString(c.getColumnIndex(Index.MEMAIL));
                    m.phone = c.getString(c.getColumnIndex(Index.MPHONE));
                    m.addr = c.getString(c.getColumnIndex(Index.MADDR));
                    m.photo = c.getString(c.getColumnIndex(Index.MPHOTO));
                }
            }
            return m;
        }
    }

    private class PhotoListQuery extends Index.QueryFactory {
        SQLiteOpenHelper helper;

        public PhotoListQuery(Context _this) {
            super(_this);
            helper = new Index.SQLiteHelper(_this);
        }

        @Override
        public SQLiteDatabase getDatabase() {
            return helper.getReadableDatabase();
        }
    }

    private class ItemPhoto extends PhotoListQuery {
        public ItemPhoto(Context _this) {
            super(_this);
        }

        public String get(int seq) {
            Cursor c = getDatabase()
                    .rawQuery(String.format(
                            " SELECT %s FROM %s "
                                    + " WHERE %s LIKE '%s' "
                            , Index.MPHOTO, Index.MEMBER,
                            Index.MSEQ, seq), null
                    );
            String result = "";
            if (c != null) {
                if (c.moveToNext()) {
                    result = c.getString(c.getColumnIndex(Index.MPHOTO));
                }
            }
            return result;
        }
    }
}