package main.java.com.termux.activity.ubuntulistfragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.R;

import java.io.File;
import java.util.List;

import main.java.com.termux.activity.UbuntuListActivity;
import main.java.com.termux.app.TermuxActivity;
import main.java.com.termux.app.TestActivity;
import main.java.com.termux.core.CoreGuiBean;
import main.java.com.termux.core.CoreGuiInstall;
import main.java.com.termux.core.CoreGuiInstallListener;
import main.java.com.termux.utils.SaveData;
import main.java.com.termux.utils.VNCActivityUtils;
import main.java.com.termux.view.MyDialog;

public class UbuntuLinuxFragment extends BaseFragment implements View.OnClickListener {

    private TextView start_linux;

    private TextView install_linux;

    private TextView msg;


    @Override
    public View getFragmentView() {
        return View.inflate(getContext(), R.layout.fragment_debian_linux_1, null);
    }

    @Override
    public void initFragmentView(View mView) {

        start_linux = (TextView) findViewById(R.id.start_linux);

        install_linux = (TextView) findViewById(R.id.install_linux);

        msg = (TextView) findViewById(R.id.msg);


        install_linux.setOnClickListener(this);
        start_linux.setOnClickListener(this);
        //startSystemVnc
        String winlog_2 = SaveData.getData("winlog_2");

        if (winlog_2.equals("yes")) {
            install_linux.setVisibility(View.VISIBLE);
            start_linux.setVisibility(View.VISIBLE);
        } else {
            install_linux.setVisibility(View.VISIBLE);
            start_linux.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.install_linux:
                File file = new File(Environment.getExternalStorageDirectory(), "/xinhao/iso/debian_linux_2.tar.gz");

                if (!file.exists()) {
                    Toast.makeText(getActivity(), file.getAbsolutePath() + "  文件不存在!", Toast.LENGTH_SHORT).show();
                    return;
                }

                UbuntuListActivity.mIsRun = true;
                CoreGuiBean coreGuiBean = new CoreGuiBean();

                coreGuiBean.password = "12345678";
                coreGuiBean.vncPassword = "12345678";
                coreGuiBean.username = "ubuntu";
                SaveData.saveData("winlog_2", "def");

                install_linux.setVisibility(View.GONE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                        CoreGuiInstall.install(
                            new File(Environment.getExternalStorageDirectory(), "/xinhao/iso/debian_linux_2.tar.gz").getAbsolutePath(),
                            "winlog_2",
                            coreGuiBean,
                            new CoreGuiInstallListener() {
                                @Override
                                public void position(String name, int position, int size) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            msg.setText("日志:\n" + name);

                                        }
                                    });
                                }

                                @Override
                                public void error(String msg) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            install_linux.setVisibility(View.VISIBLE);

                                            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                                            ab.setTitle("错误!");

                                            ab.setMessage("安装失败,出现了一个错误,请重新安装\n" + msg);

                                            ab.setPositiveButton("好", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ab.create().dismiss();
                                                }
                                            });
                                            ab.show();
                                        }
                                    });
                                    UbuntuListActivity.mIsRun = false;
                                }

                                @Override
                                public void complete() {


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            install_linux.setVisibility(View.VISIBLE);
                                            start_linux.setVisibility(View.VISIBLE);

                                            AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                                            ab.setTitle("成功!");

                                            ab.setMessage("安装成功,请点击启动按钮,来启动您的系统");

                                            ab.setPositiveButton("好", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ab.create().dismiss();
                                                }
                                            });
                                            ab.show();
                                        }
                                    });

                                    UbuntuListActivity.mIsRun = false;
                                    SaveData.saveData("winlog_2", "yes");


                                }
                            }
                        );


                    }
                }).start();

                break;

            case R.id.start_linux:


                AlertDialog.Builder ab = new AlertDialog.Builder(getActivity());
                ab.setTitle("启动选项");
                ab.setMessage("请选择启动方式:\n\n连接方式:\n\nvnc:\n\n127.0.0.1:5951\n\n用户名:ubuntu\n密码:12345678\n\nVNC和ssh一样都是上述密码");
                ab.setPositiveButton("SSH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ab.create().dismiss();

                        MyDialog myDialog = new MyDialog(getActivity());
                        myDialog.getDialog_title().setText("正在启动");
                        myDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CoreGuiBean coreGuiBean1 = new CoreGuiBean();
                                coreGuiBean1.password = "12345678";
                                coreGuiBean1.vncPassword = "12345678";
                                coreGuiBean1.username = "ubuntu";
                                CoreGuiInstall.startSystemSsh("winlog_2", coreGuiBean1);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDialog.dismiss();
                                        TermuxActivity.mTerminalView.sendTextToTerminal("clear \n");
                                        TermuxActivity.mTerminalView.sendTextToTerminal("ssh ubuntu@127.0.0.1 -p 2022 \n");
                                        startActivity(new Intent(getActivity(), TermuxActivity.class));

                                    }
                                });

                            }
                        }).start();


                    }
                });

                ab.setNeutralButton("XSDL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ab.create().dismiss();
                        if(true){
                            Toast.makeText(getActivity(), "暂不支持", Toast.LENGTH_SHORT).show();
                            return ;
                        }
                        MyDialog myDialog = new MyDialog(getActivity());
                        myDialog.getDialog_title().setText("正在启动");
                        myDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CoreGuiBean coreGuiBean1 = new CoreGuiBean();
                                coreGuiBean1.password = "12345678";
                                coreGuiBean1.vncPassword = "12345678";
                                coreGuiBean1.username = "ubuntu";
                                CoreGuiInstall.startSystemXSDL("winlog_2", coreGuiBean1);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDialog.dismiss();

                                        //dev/tty0
                                        File file1 = new File("/data/data/com.termux/files/winlog_2/dev/tty0");
                                        if (!file1.exists()) {
                                            file1.mkdirs();
                                        }

                                        try {
                                            Intent i = new Intent(Intent.ACTION_MAIN, Uri.parse("x11://give.me.display:4713"));
                                            startActivityForResult(i, 1);
                                        } catch (Exception e) {
                                            Toast.makeText(getContext(), "面对疾风吧:没有找到XSDL,请到群文件中下载并安装XSDL", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }).start();


                    }
                });

                ab.setNegativeButton("VNC", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ab.create().dismiss();

                        ab.create().dismiss();

                        MyDialog myDialog = new MyDialog(getActivity());
                        myDialog.getDialog_title().setText("正在启动");
                        myDialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CoreGuiBean coreGuiBean1 = new CoreGuiBean();
                                coreGuiBean1.password = "12345678";
                                coreGuiBean1.vncPassword = "12345678";
                                coreGuiBean1.username = "ubuntu";
                                CoreGuiInstall.startSystemVnc("winlog_2", coreGuiBean1);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myDialog.dismiss();

                                   /*     Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setType("application/vnd.vnc");
                                        //vnc://127.0.0.1:5951/?VncUsername=$username&VncPassword=$vncPassword
                                        intent.setData(Uri.parse("vnc://127.0.0.1:5951/?VncUsername=ubuntu&VncPassword=12345678"));
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                                        if (resolveInfos.size() > 0) {
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(getActivity(), "你没有安装群中的vnc,但是建议您使用全能版!", Toast.LENGTH_LONG).show();
                                        }
*/

                                        VNCActivityUtils.getVNCIntent(getActivity(),"5951","127.0.0.1","12345678");
                                    }
                                });

                            }
                        }).start();

                    }
                });
                ab.show();

                break;
        }

    }
}
