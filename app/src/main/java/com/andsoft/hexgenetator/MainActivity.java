package com.andsoft.hexgenetator;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    int column = 16;
    int row = 16;
    int size = column * row;
    GVAdapter adapter;
    int currTouchIndex = -1;
    List<Integer> list = new ArrayList();
    List<Integer> listTmp = new ArrayList();
    FontUtils utils;
    boolean[][] matrix = null;

    @BindView(R.id.et_main)
    EditText etMain;
    @BindView(R.id.btn_refresh_char_top)
    Button btnRefreshCharTop;
    @BindView(R.id.gridview)
    GridView gridView;
    @BindView(R.id.btn_color_exchange)
    Button btnColorExchange;
    @BindView(R.id.btn_mirror_exchange)
    Button btnMirrorExchange;
    @BindView(R.id.btn_rotate_90)
    Button btnRotate90;
    @BindView(R.id.author)
    Button author;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.btn_gene)
    Button btnGene;


    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        utils = new FontUtils(this);

        list.clear();

        for (int j = 0; j < size; j++) {
            list.add(0);
        }

        gridView.setNumColumns(column); // 动态设置列数

        adapter = new GVAdapter(getApplicationContext(), R.layout.item, list);

        gridView.setAdapter(adapter);
        
        gridView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {
                int action = me.getActionMasked();
                int position = gridView.pointToPosition((int) me.getX(), (int) me.getY());

                if (!(position == -1 || position == currTouchIndex)) {
                    currTouchIndex = position;
                    list.set(position, list.get(position) == 1 ? 0 : 1);
                    adapter.notifyDataSetChanged();
                }
                if (action == 1) {
                    currTouchIndex = -1;
                }
                return true;
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                list.set(position, list.get(position) == 1 ? 0 : 1);
                adapter.notifyDataSetChanged();
            }
        });

    }


    public void showDialog(final byte[] arr) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("");
        alertDialog.setCancelable(true);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        alertDialog.setView(dialogView);

        final TextView txtResult = (TextView) dialogView.findViewById(R.id.txtResult);
        txtResult.setText(bytesToHexStr(arr, true));

        ((RadioGroup) dialogView.findViewById(R.id.radioGroup)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_BIN){
                    txtResult.setText(bytesToHexStr(arr, false));
                } else {
                    txtResult.setText(bytesToHexStr(arr, true));
                }
            }
        });

        final AlertDialog mAlertDialog = alertDialog.create();

        dialogView.findViewById(R.id.image_cancel).setOnClickListener(v -> mAlertDialog.dismiss());
        dialogView.findViewById(R.id.image_copy).setOnClickListener(v -> copy(txtResult.getText().toString()));
        dialogView.findViewById(R.id.image_share).setOnClickListener(v -> share(txtResult.getText().toString()));

        mAlertDialog.show();
    }

    @OnClick({R.id.btn_refresh_char_top, R.id.btn_color_exchange, R.id.btn_mirror_exchange, R.id.btn_rotate_90, R.id.author, R.id.btn_clear, R.id.btn_gene})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_refresh_char_top:

                String ss = etMain.getText().toString();
                if (ss != null && ss.length() == 1) {
                    matrix = utils.getWordsInfo(ss); // 取得一个汉字的字模，并进行刷新屏幕

                    if (matrix != null && matrix.length > 0) {
                        list.clear();

                        for (int i = 0; i < 16; i++) {
                            boolean[] first = matrix[i];

                            for (int i2 = 0; i2 < 16; i2++) {
                                boolean valve = first[i2];
                                if (valve) {
                                    list.add(1);
                                } else {
                                    list.add(0);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtils.showShort("数组为空");
                    }

                } else {
                    ToastUtils.showShort("输入的长度必须是1");
                }

                break;
            case R.id.btn_color_exchange:
                listTmp.clear();
                for (int i = 0; i < list.size(); i++) {
                    int old = list.get(i);
                    if (old == 1) {
                        listTmp.add(0);
                    } else {
                        listTmp.add(1);
                    }
                }

                list.clear();
                list.addAll(listTmp);

                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_mirror_exchange:
                listTmp.clear();
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        int indexLine = 15 - j;
                        int index = 16 * i + indexLine;
                        listTmp.add(
                                list.get(index)
                        );
                    }
                }

                list.clear();
                list.addAll(listTmp);

                adapter.notifyDataSetChanged();
                break;
            case R.id.btn_rotate_90:
                listTmp.clear();

                for (int i = 0; i < 16; i++) { // 行
                    for (int j = 0; j < 16; j++) {  // 列
                        int index = 15 - i;

                        listTmp.add(list.get(16 * j + index)
                        );
                    }
                }

                list.clear();
                list.addAll(listTmp);

                adapter.notifyDataSetChanged();
                break;
            case R.id.author:
                openWebsiteOuter(this, "");
                break;
            case R.id.btn_clear:
                for (int j = 0; j < size; j++) {
                    list.set(j, 0);
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_gene:
                showDialog(binToBytes(list));
                break;
        }
    }

    ///===================================== utils ===================================///

    public static void openWebsiteOuter(Context context, String url) {
        Intent intent = new Intent();
        //Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }


    private void copy(String str){
        ClipboardManager clipboard = (ClipboardManager) getSystemService("clipboard");
        ClipData clipData = ClipData.newPlainText("Clip", str);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clipData);
        }
        ToastUtils.showShort("复制成功");
    }

    public void share(String str){
        Intent sendIntent = new Intent();
        sendIntent.setAction("android.intent.action.SEND");
        sendIntent.setType("text/plain");
        sendIntent.putExtra("android.intent.extra.SUBJECT", getResources().getString(R.string.app_name));
        sendIntent.putExtra("android.intent.extra.TEXT", str);
        startActivity(Intent.createChooser(sendIntent, "Share app with"));
    }


    public String bytesToHexStr(byte[] arr, boolean isHEX) {
        StringBuilder sb = new StringBuilder();
        String prefix = isHEX ? "0x" : "0b";
        for (int i = 0; i < arr.length; i++) {
            String s1 = (isHEX ? String.format("%02x", new Object[]{Byte.valueOf(arr[i])}) : String.format("%8s", new Object[]{Integer.toBinaryString(arr[i] & 255)}).replace(' ', '0')).toUpperCase();
            if (column != 16) {
                sb.append(prefix).append(s1);
                if (i != arr.length - 1) {
                    sb.append(", ");
                }
            } else if (i % 2 == 0) {
                sb.append(prefix).append(s1);
            } else {
                sb.append(s1);
                if (i != arr.length - 1) {
                    sb.append(", ");
                }
            }
        }
        return sb.toString();
    }


    public byte[] binToBytes(List<Integer> bin){
        byte[] tmp = new byte[(size / 8)];

        for (int i = 0; i < size / 8; i++) { // 以8位长度为一个byte进行处理
            tmp[i] = 0;

            for (int j = 0; j < 8; j++) { // 遍历8位二进制数据

                int current = bin.get(  (i * 8) + j  );

                int de = 7 - j;

                byte e = (byte) ( current << de ); // 按照由高到低的次序分别对应n次

                tmp[i] = (byte) ( e | tmp[i]); // 替换为最新的数据：说明：一个byte经过了8次处理，才转为hex
            }
        }

        return tmp;
    }
    
}
