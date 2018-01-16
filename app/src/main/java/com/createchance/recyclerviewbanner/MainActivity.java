package com.createchance.recyclerviewbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.createchance.recyclerbanner.RecyclerBanner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    RecyclerBanner recyclerBanner;

    private List<Banner> bannerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerBanner = findViewById(R.id.banner);
        initBannerList();
        recyclerBanner.setBannerCount(bannerList.size());
        recyclerBanner.setRecyclerBannerCallback(new RecyclerBanner.RecyclerBannerCallback() {
            @Override
            public View createBannerView(ViewGroup parent) {
                View bannerView = getLayoutInflater().inflate(R.layout.banner_item, parent, false);

                bannerView.findViewById(R.id.banner_title).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = recyclerBanner.getCurrentPosition();
                        Toast.makeText(MainActivity.this, "第" + pos + "个title被点击了", Toast.LENGTH_SHORT).show();
                    }
                });

                bannerView.findViewById(R.id.banner_img).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = recyclerBanner.getCurrentPosition();
                        Toast.makeText(MainActivity.this, "第" + pos + "个image被点击了", Toast.LENGTH_SHORT).show();
                    }
                });

                bannerView.findViewById(R.id.banner_footer).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = recyclerBanner.getCurrentPosition();
                        Toast.makeText(MainActivity.this, "第" + pos + "个footer被点击了", Toast.LENGTH_SHORT).show();
                    }
                });

                return bannerView;
            }

            @Override
            public void switchBanner(int position, View bannerView) {
                Banner banner = bannerList.get(position);

                TextView title = bannerView.findViewById(R.id.banner_title);
                title.setText("第" + position + "张title");

                Glide.with(MainActivity.this)
                        .load(banner.getUrl())
                        .error(R.mipmap.ic_launcher)
                        .into((ImageView) bannerView.findViewById(R.id.banner_img));

                TextView footer = bannerView.findViewById(R.id.banner_footer);
                footer.setText("第" + position + "张footer");
            }
        });
    }

    public void refresh(View view) {
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471181%26di%3Db73c4da8c247d39544a2d6c0c92c1c8b%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fimg2.niutuku.com%252Fdesk%252F1208%252F2027%252Fntk-2027-16107.jpg&thumburl=https%3A%2F%2Fss1.bdstatic.com%2F70cFvXSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1639473443%2C2804235162%26fm%3D27%26gp%3D0.jpg"));
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471180%26di%3Df80d983ecf94b1e175c7bbae0fde7e4f%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fimgsrc.baidu.com%252Fimgad%252Fpic%252Fitem%252Fb3b7d0a20cf431ada46c8e114036acaf2edd9858.jpg&thumburl=https%3A%2F%2Fss2.bdstatic.com%2F70cFvnSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1470376000%2C2278695940%26fm%3D200%26gp%3D0.jpg"));
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471178%26di%3D6a613d3370fac53c0df859badd090d07%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fimg2.niutuku.com%252Fdesk%252F1208%252F1307%252Fntk-1307-6459.jpg&thumburl=https%3A%2F%2Fss0.bdstatic.com%2F70cFuHSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D2094003366%2C1604040358%26fm%3D27%26gp%3D0.jpg"));
        recyclerBanner.setBannerCount(bannerList.size());
        recyclerBanner.refresh();
    }

    private void initBannerList() {
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471178%26di%3D42187f41f8f936b68ab35b5193d05373%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fdl.bizhi.sogou.com%252Fimages%252F2015%252F06%252F26%252F1214905.jpg&thumburl=https%3A%2F%2Fss2.bdstatic.com%2F70cFvnSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1134876658%2C3727605308%26fm%3D27%26gp%3D0.jpg"));
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471217%26di%3D951829eaa49d43826feb84a432d57ff5%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fimgsrc.baidu.com%252Fimage%252Fc0%25253Dshijue1%25252C0%25252C0%25252C294%25252C40%252Fsign%253D511f803a03f79052fb124f7d649abdbf%252F9922720e0cf3d7ca0a70cc5ff81fbe096b63a911.jpg&thumburl=https%3A%2F%2Fss1.bdstatic.com%2F70cFvXSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1324608224%2C397061657%26fm%3D200%26gp%3D0.jpg"));
        bannerList.add(new Banner("https://image.baidu.com/search/down?tn=download&word=download&ie=utf8&fr=detail&url=https%3A%2F%2Ftimgsa.baidu.com%2Ftimg%3Fimage%26quality%3D80%26size%3Db9999_10000%26sec%3D1516098471216%26di%3D822619f57ea1b552f285228832ef3e78%26imgtype%3D0%26src%3Dhttp%253A%252F%252Fimgsrc.baidu.com%252Fimgad%252Fpic%252Fitem%252Fa50f4bfbfbedab64228eb135fd36afc378311e42.jpg&thumburl=https%3A%2F%2Fss2.bdstatic.com%2F70cFvnSh_Q1YnxGkpoWK1HF6hhy%2Fit%2Fu%3D1536714922%2C3019469498%26fm%3D200%26gp%3D0.jpg"));
    }

    private class Banner {

        String url;

        public Banner(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }
}
