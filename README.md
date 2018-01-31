# RecyclerBanner
Auto play banner list view, implemented by recycler view.

Only one java class, it is tiny and clean.

# How to use
## 1. define in xml layout:

    <com.createchance.recyclerbanner.RecyclerBanner
            android:id="@+id/banner"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            app:autoPlaying="true"
            app:indicatorSelectedSrc="#00ff00"
            app:indicatorUnselectedSrc="@android:color/holo_green_light" />
        
## 2. define banner view layout(sample):

    <?xml version="1.0" encoding="utf-8"?>
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tool="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    
        <ImageView
            android:id="@+id/banner_img"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop"
            tool:background="#4c4c4c" />
    
        <TextView
            android:id="@+id/banner_title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="10dp"
            android:layout_marginTop="10dp"
            android:textColor="@android:color/holo_orange_light"
            tool:text="@string/app_name" />
    
        <TextView
            android:id="@+id/banner_footer"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true"
            android:layout_alignParentEnd="true"
            android:layout_marginBottom="10dp"
            android:layout_marginEnd="10dp"
            android:textColor="@android:color/holo_orange_light"
            tool:text="@string/app_name" />
    
    </RelativeLayout>

## 3. use in activity

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
# xml attribute
    autoPlaying(boolean): set auto play.
    playInterval(integer): auto play interval time.
    indicatorSelected(color|reference): selected indicator source color or image.
    indicatorUnselected(color|reference): unselected indicator source color or image.
    showIndicator(boolean): whether show indicator of current position.
    indicatorSize(dimension|reference): size of indicator view.
    indicatorSpace(dimension|reference): space of indicator view.
    indicatorMargin(dimension|reference): margin(left, top, right, bottom) of indicator view.
    indicatorGravity(enum): gravity of indicator views in it's container.
# java api
    See comments of RecyclerBanner.java class, that is more clean than plain words.
    
