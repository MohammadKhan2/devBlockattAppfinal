package com.app.blockaat.productlisting.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.app.blockaat.cart.model.AddCartRequestModel;
import com.app.blockaat.helper.AddWishListInterface;
import com.app.blockaat.helper.Constants;
import com.app.blockaat.helper.DBHelper;
import com.app.blockaat.helper.Global;
import com.app.blockaat.helper.StickHeaderItemDecoration;
import com.app.blockaat.home.interfaces.HomeItemClickInterface;
import com.app.blockaat.login.LoginActivity;
import com.app.blockaat.productlisting.interfaces.OnHeaderClicked;
import com.app.blockaat.productlisting.interfaces.OnProductListListener;
import com.app.blockaat.productlisting.interfaces.OnSubcategorySelectListener;
import com.app.blockaat.productlisting.interfaces.OnTvClickListener;
import com.app.blockaat.productlisting.model.Tvs;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.demono.AutoScrollViewPager;
import com.app.blockaat.R;
import com.app.blockaat.category.adapter.SubCategoriesAdapter;
import com.app.blockaat.category.model.Subcategory;
import com.app.blockaat.instadotview.InstaDotView;
import com.app.blockaat.productlisting.model.InfluencerDetails;
import com.app.blockaat.productlisting.model.ProductListingProductModel;
import com.app.blockaat.wishlist.modelclass.WishListResponseModel;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;


import org.jcodec.common.DictionaryCompressor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;

public class ProductListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickHeaderItemDecoration.StickyHeaderInterface {

    private static final int TYPE_SUBCATEGORY = 0;
    private static final int TYPE_TV = 1;

    private static final int TYPE_BANNER = 2;
    private static final int TYPE_HEADER = 3;
    private static final int TYPE_ITEM = 4;
    private static final int TYPE_LOADING = 5;
    private Boolean isLoadingAdded = false;
    private double width = 0;
    private double height = 0;
    private double screenWidth = 0;
    private DBHelper productsDBHelper;
    private HomeItemClickInterface homeItemClickInterface;
    private ArrayList<ProductListingProductModel> arrListProductListing;
    private Activity activity;
    private String isFromStr;
    private String categoryTitle;
    private boolean showFilterView;
    private OnHeaderClicked headerClickListener;
    private OnSubcategorySelectListener onSelectListener;
    private OnTvClickListener onTvClickListener;
    private double deviceMultiplier = 0.0;
    private OnProductListListener onProductListListener;

    public ProductListAdapter(Activity activity, ArrayList<ProductListingProductModel> arrListProductListing,
                              OnHeaderClicked headerClickListener, String isFromStr,
                              boolean showFilterView,
                              OnSubcategorySelectListener onSelectListener,
                              OnTvClickListener onTvClickListener,
                              String categoryTitle,
                              OnProductListListener onProductListListener, HomeItemClickInterface homeItemClickInterface) {
        productsDBHelper = new DBHelper(activity);
        deviceMultiplier = Global.INSTANCE.getDeviceWidth(activity) / Double.valueOf(320);

        screenWidth = ((Global.INSTANCE.getDeviceWidth(activity)) - (30 * deviceMultiplier));
        width = screenWidth / 2;
        height = screenWidth / 2;
        this.arrListProductListing = arrListProductListing;
        this.activity = activity;
        this.isFromStr = isFromStr;
        this.showFilterView = showFilterView;
        this.categoryTitle = categoryTitle;
        this.headerClickListener = headerClickListener;
        this.onSelectListener = onSelectListener;
        this.onTvClickListener = onTvClickListener;
        this.onProductListListener = onProductListListener;
        this.homeItemClickInterface = homeItemClickInterface;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {

            case TYPE_SUBCATEGORY:
                view = LayoutInflater.from(activity).inflate(R.layout.item_category_list, parent, false);
                return new SubCategoryHolderView(view);
            case TYPE_TV:
                view = LayoutInflater.from(activity).inflate(R.layout.item_tv_videos_list, parent, false);
                return new TvVideoView(view);
            case TYPE_BANNER:
                view = LayoutInflater.from(activity).inflate(R.layout.item_products_banner, parent, false);
                return new BannerItemView(view);
            case TYPE_HEADER:
                view = LayoutInflater.from(activity).inflate(R.layout.item_product_sort_filter, parent, false);
                return new HeaderItemView(view);

            case TYPE_LOADING:
                view = LayoutInflater.from(activity).inflate(R.layout.item_product_footer, parent, false);
                return new FooterHolderView(view);
            default:
                view = LayoutInflater.from(activity).inflate(R.layout.item_product_list, parent, false);
                return new ItemViewHolder(view, activity);
        }
    }

    private boolean isPositionFooter(int position) {
        return (position == arrListProductListing.size() - 1 && isLoadingAdded);
    }


    public void addLoadingFooter() {
        Log.i("footer", "addLoadingFooter");
        if (!isLoadingAdded) {
            isLoadingAdded = true;
            add(new ProductListingProductModel());
        }

    }

    public void clearData() {
        if (!arrListProductListing.isEmpty()) {
            arrListProductListing.clear();
        }

    }


    public void removeLoadingFooter() {
        Log.i("footer", "removeLoadingFooter");
        if (isLoadingAdded) {
            isLoadingAdded = false;
            int position = arrListProductListing.size() - 1;
            ProductListingProductModel item = getItem(position);
            if (item != null) {
                arrListProductListing.remove(position);
                notifyItemRemoved(position);
            }
        }

    }

    private ProductListingProductModel getItem(int position) {
        return arrListProductListing.get(position);
    }

    private void add(ProductListingProductModel mc) {
        arrListProductListing.add(mc);
        notifyItemInserted(arrListProductListing.size() - 1);
    }

    public void addAll(java.util.ArrayList<ProductListingProductModel> mcList) {

        for (ProductListingProductModel mc : mcList) {
            add(mc);
        }
    }

    private void remove(ProductListingProductModel city) {
        int position = arrListProductListing.indexOf(city);
        if (position > 0) {
            arrListProductListing.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof SubCategoryHolderView) {
            ((SubCategoryHolderView) holder).bind(arrListProductListing, position, onSelectListener, onTvClickListener);
        } else if (holder instanceof TvVideoView) {
            ((TvVideoView) holder).bind(arrListProductListing, position, onSelectListener, onTvClickListener);
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(arrListProductListing, position);
        } else if (holder instanceof FooterHolderView) {

        } else if (holder instanceof HeaderItemView) {
            ((HeaderItemView) holder).bind(arrListProductListing, position);

        } else if (holder instanceof BannerItemView) {
            ((BannerItemView) holder).bind(arrListProductListing, position, homeItemClickInterface);

        }

    }

    @Override
    public int getItemCount() {
        return arrListProductListing.size();
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 1;
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        if (getItemViewType(headerPosition) == TYPE_HEADER) {
            return R.layout.item_product_sort_filter;
        } else {
            return R.layout.item_empty_header;
        }
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        if (getItemViewType(headerPosition) == TYPE_HEADER) {
            TextView txtRefine = header.findViewById(R.id.txtRefine);
            TextView txtItemCount = header.findViewById(R.id.txtItemCount);
            txtRefine.setTypeface(Global.INSTANCE.getFontRegular());
            txtItemCount.setTypeface(Global.INSTANCE.getFontMedium());

        }

    }

    @Override
    public boolean isHeader(int itemPosition) {
        return itemPosition == TYPE_HEADER || itemPosition == TYPE_BANNER || itemPosition == TYPE_SUBCATEGORY || itemPosition == TYPE_TV;
    }

    public boolean isSubcategory(int itemPosition) {
        return itemPosition == TYPE_SUBCATEGORY;
    }

    public boolean isTvVideos(int itemPosition) {
        return itemPosition == TYPE_TV;
    }

    public boolean isBanner(int itemPosition) {
        return itemPosition == TYPE_BANNER;
    }


    @Override
    public int getItemViewType(int position) {
        if (isSubcategory(position)) {
            return TYPE_SUBCATEGORY;
        } else if (isTvVideos(position)) {
            return TYPE_TV;
        } else if (isBanner(position)) {
            return TYPE_BANNER;
        } else if (isHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_LOADING;
        } else {
            return TYPE_ITEM;
        }
    }

    public void clear() {
        if (arrListProductListing != null && !arrListProductListing.isEmpty()) {
            arrListProductListing.clear();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProduct, ivWishlist, ivDiscount;
        TextView txtName, txtBrandName, txtFinalPrice, txtRegularPrice, txtDiscount,txtSoldOut;
        LinearLayout linHead;
        RelativeLayout relDiscount;

        private ItemViewHolder(@NonNull View itemView, Activity activity) {
            super(itemView);

            linHead = itemView.findViewById(R.id.linHead);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            ivWishlist = itemView.findViewById(R.id.ivWishlist);
            txtName = itemView.findViewById(R.id.txtName);
            txtFinalPrice = itemView.findViewById(R.id.txtFinalPrice);
            txtDiscount = itemView.findViewById(R.id.txtDiscount);
            txtRegularPrice = itemView.findViewById(R.id.txtRegularPrice);
            txtBrandName = itemView.findViewById(R.id.txtBrandName);
            relDiscount = itemView.findViewById(R.id.relDiscount);
            ivDiscount = itemView.findViewById(R.id.ivDiscount);
            txtSoldOut = itemView.findViewById(R.id.txtSoldOut);
        }


        public void bind(ArrayList<ProductListingProductModel> arrListProductListing, int position) {

          /*  if (position % 2 == 0) {
                Log.v("ProductAdapter", "position % 2: " + position);
//                ((RecyclerView.LayoutParams) linHead.getLayoutParams()).setMarginStart((int) (10 * deviceMultiplier));
//                ((RecyclerView.LayoutParams) linHead.getLayoutParams()).setMarginEnd((int) (5 * deviceMultiplier));

            } else {
                Log.v("ProductAdapter", "Normal: " + position);
//                ((RecyclerView.LayoutParams) linHead.getLayoutParams()).setMarginStart((int) (5 * deviceMultiplier));
//                ((RecyclerView.LayoutParams) linHead.getLayoutParams()).setMarginEnd((int) (10 * deviceMultiplier));
            }*/

            if (position > -1) {
                /* if (position > 3) {
                    ((RecyclerView.LayoutParams) linHead.getLayoutParams()).topMargin = (int) (10 * deviceMultiplier);
                } else {
                    ((RecyclerView.LayoutParams) linHead.getLayoutParams()).topMargin = (int) (0 * deviceMultiplier);

                }*/
                ProductListingProductModel a = arrListProductListing.get(position);
                txtBrandName.setText(a.getBrand_name());
                txtName.setText(a.getName());
                //txtFinalPrice.setText(activity.getResources().getString(R.string.KWD)+" "+a.getFinal_price());
                if (!a.getFinal_price().isEmpty()) {
                    txtFinalPrice.setText(Global.INSTANCE.setPriceWithCurrency(activity, a.getFinal_price()));
                }

                if (!a.getRegular_price().isEmpty()) {
                    txtRegularPrice.setText(Global.INSTANCE.setPriceWithCurrency(activity, a.getRegular_price()));
                    txtRegularPrice.setPaintFlags(STRIKE_THRU_TEXT_FLAG);
                }
                if (!Global.INSTANCE.isEnglishLanguage(activity)) {
                    ivDiscount.setRotation(180f);
                }

                if(arrListProductListing.get(position).is_saleable() == 0){
                    txtSoldOut.setVisibility(View.VISIBLE);
                }else {
                    txtSoldOut.setVisibility(View.GONE);
                }

                Integer disData = Global.INSTANCE.getDiscountedPrice(a.getFinal_price() + "", a.getRegular_price() + "");
                if (!(disData == 0)) {
                    txtDiscount.setText(String.valueOf(disData) + "%");
                } else {
                    relDiscount.setVisibility(View.GONE);
                    txtRegularPrice.setVisibility(View.GONE);
                }

                Glide.with(activity)
                        .load(a.getImage())
                        .override((int) width, (int) height)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })

                        .transition(DrawableTransitionOptions.withCrossFade(800))
                        .into(ivProduct);


                //change wishList icon color
                if (a.getItem_in_wishlist() == 1) {
                    ivWishlist.setImageResource(R.drawable.ic_wishlist_selected);
                    //  ivWishlist.setColorFilter(R.color.tab_select);
                } else {
                    ivWishlist.setImageResource(R.drawable.ic_wishlist_unselected);
                    // ivWishlist.setColorFilter(R.color.tab_select);
                }

                //set fonts
                txtName.setTypeface(Global.INSTANCE.getFontRegular());
                txtBrandName.setTypeface(Global.INSTANCE.getFontNavbar());
                txtFinalPrice.setTypeface(Global.INSTANCE.getFontPriceBold());
                txtRegularPrice.setTypeface(Global.INSTANCE.getFontRegular());

              /*  txtAddCart.setOnClickListener(view -> {
                    if (arrListProductListing.get(position).getProduct_type() != null && arrListProductListing.get(position).getProduct_type().equalsIgnoreCase("Simple")) {
                        if (arrListProductListing.get(position).is_saleable() == 1) {
                            AddCartRequestModel model = new AddCartRequestModel();
                            model.setId(arrListProductListing.get(position).getId());
                            model.setEntity_id(arrListProductListing.get(position).getId());
                            model.setName(arrListProductListing.get(position).getName());
                            model.setSKU(arrListProductListing.get(position).getSKU());
                            model.setRegular_price(arrListProductListing.get(position).getRegular_price());
                            model.setFinal_price(arrListProductListing.get(position).getFinal_price());
                            model.set_saleable(arrListProductListing.get(position).is_saleable());
                            model.setBrand_name(arrListProductListing.get(position).getBrand_name());
                            model.setImage(arrListProductListing.get(position).getImage());
                            model.setProduct_type(arrListProductListing.get(position).getProduct_type().toString());

                            if (Global.INSTANCE.getStringFromSharedPref(activity, Constants.INSTANCE.PREFS_isUSER_LOGGED_IN).equalsIgnoreCase("yes")) {
                                Global.INSTANCE.addToCartOnline(activity, model);
                            } else {
                                Global.INSTANCE.addToCartOffline(activity, model);
                            }
                        } else {
                            if (Global.INSTANCE.isUserLoggedIn(itemView.getContext())) {
                                Global.INSTANCE.notifyMe(activity, arrListProductListing.get(position).getId().toString());
                            } else {
                               *//* Intent i = new Intent(activity, LoginActivity.class);
                                i.putExtra("isFromProducts", "yes");
                                activity.startActivityForResult(i, 5);*//*
                                Global.INSTANCE.notifyMe(activity, arrListProductListing.get(position).getId().toString());

                            }
                        }
                    } else {
                        linHead.performClick();
                    }*/

                   /* if (arrListProductListing.get(position).is_saleable() == 1 && arrListProductListing.get(position).getProduct_type().equalsIgnoreCase("Simple")) {
                        AddCartRequestModel model = new AddCartRequestModel();
                        model.setId(arrListProductListing.get(position).getId());
                        model.setEntity_id(arrListProductListing.get(position).getId());
                        model.setName(arrListProductListing.get(position).getName());
                        model.setSKU(arrListProductListing.get(position).getSKU());
                        model.setRegular_price(arrListProductListing.get(position).getRegular_price());
                        model.setFinal_price(arrListProductListing.get(position).getFinal_price());
                        model.set_saleable(arrListProductListing.get(position).is_saleable());
                        model.setBrand_name(arrListProductListing.get(position).getBrand_name());
                        model.setImage(arrListProductListing.get(position).getImage());
                        model.setProduct_type(arrListProductListing.get(position).getProduct_type());

                        if (Global.INSTANCE.getStringFromSharedPref(activity, Constants.INSTANCE.PREFS_isUSER_LOGGED_IN).equalsIgnoreCase("yes")) {
                            Global.INSTANCE.addToCartOnline(activity, model);
                        } else {
                            Global.INSTANCE.addToCartOffline(activity, model);
                        }

                    } else {
                        linHead.performClick();
                    }*/
//                });


                ivWishlist.setOnClickListener(view -> {
                    if (Global.INSTANCE.isUserLoggedIn(activity)) {
                        Boolean flag = arrListProductListing.get(position).getItem_in_wishlist() == 0;
                        Global.INSTANCE.addOrRemoveWishList(activity, arrListProductListing.get(position).getId(), productsDBHelper, flag, new AddWishListInterface() {
                            @Override
                            public void onRemove(@NotNull WishListResponseModel result) {
                                onProductListListener.onProductClicked(arrListProductListing.get(position), "wishlist");
                            }

                            @Override
                            public void onAdd(@NotNull WishListResponseModel result) {
                                onProductListListener.onProductClicked(arrListProductListing.get(position), "wishlist");
                            }
                        });
                       /* if (arrListProductListing.get(position).getItem_in_wishlist() == 1) {
                            Global.INSTANCE.deleteFromWishlist(activity, arrListProductListing.get(position).getId(), productsDBHelper);
                            arrListProductListing.get(position).setItem_in_wishlist(0);
                        } else {
                            Global.INSTANCE.addToWishlist(activity, arrListProductListing.get(position).getId(), productsDBHelper);
                            arrListProductListing.get(position).setItem_in_wishlist(1);
                        }*/

                    } else {
                        Intent i = new Intent(activity, LoginActivity.class);
                        i.putExtra("isFromProducts", "yes");
                        activity.startActivityForResult(i, 5);
                    }
                });

                linHead.setOnClickListener(view -> {
                    onProductListListener.onProductClicked(arrListProductListing.get(position));
                });

            }
        }
    }


    private class FooterHolderView extends RecyclerView.ViewHolder {
        public FooterHolderView(@NonNull View itemView) {
            super(itemView);
        }
    }

    private class SubCategoryHolderView extends RecyclerView.ViewHolder {

        private RecyclerView rvSubCategory;
        private SubCategoriesAdapter subCategoriesAdapter;
        private ImageView ivBanner;
        private View view;

        public SubCategoryHolderView(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            rvSubCategory = itemView.findViewById(R.id.rvSubCategory);
            ivBanner = itemView.findViewById(R.id.ivBanner);
        }

        public void bind(ArrayList<ProductListingProductModel> arrListProductListing, int position,
                         OnSubcategorySelectListener onSelectListener, OnTvClickListener onTvClickListener) {


            Global.INSTANCE.loadImagesUsingGlide(view.getContext(),
                    arrListProductListing.get(position).getImage(), ivBanner);

            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            ArrayList<Subcategory> arrListSubcategory = arrListProductListing.get(position).getArrListSubCategory();

            if (arrListSubcategory != null) {
                param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                itemView.setVisibility(View.VISIBLE);
                rvSubCategory.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
                subCategoriesAdapter = new SubCategoriesAdapter(activity, arrListSubcategory, onSelectListener);
                rvSubCategory.setAdapter(subCategoriesAdapter);
                subCategoriesAdapter.notifyDataSetChanged();
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }

    private class TvVideoView extends RecyclerView.ViewHolder {

        private CategoryTvAdapter tvListAdapter;
        private LinearLayout linTvVideos;
        private LinearLayout linMain;
        private LinearLayout linAllProducts;
        private TextView txtTvVideoTitle;
        private TextView txtAllProducts;
        private View viewBottom;
        private TextView txtTvVideosView;
        private RecyclerView rvTvList;

        public TvVideoView(@NonNull View itemView) {
            super(itemView);
            linTvVideos = itemView.findViewById(R.id.linTvVideos);
            linAllProducts = itemView.findViewById(R.id.linAllProducts);
            linMain = itemView.findViewById(R.id.linMain);
            txtAllProducts = itemView.findViewById(R.id.txtAllProducts);
            txtTvVideoTitle = itemView.findViewById(R.id.txtTvVideoTitle);
            txtTvVideoTitle = itemView.findViewById(R.id.txtTvVideoTitle);
            txtTvVideosView = itemView.findViewById(R.id.txtTvVideosView);
            viewBottom = itemView.findViewById(R.id.viewBottom);
            rvTvList = itemView.findViewById(R.id.rvTvList);
        }

        public void bind(ArrayList<ProductListingProductModel> arrListProductListing, int position, OnSubcategorySelectListener onSelectListener, OnTvClickListener onTvClickListener) {

            txtAllProducts.setTypeface(Global.INSTANCE.getFontMedium());
            txtTvVideoTitle.setTypeface(Global.INSTANCE.getFontMedium());
            txtTvVideosView.setTypeface(Global.INSTANCE.getFontMedium());
            ArrayList<Tvs> arrListTvs = new ArrayList<>();
            arrListTvs = arrListProductListing.get(position).getCategory_tvs();
            if (isFromStr == "subCategory" || (arrListTvs != null && arrListTvs.size() > 0)) {
                if (arrListTvs != null && arrListTvs.size() > 0) {
                    linTvVideos.setVisibility(View.VISIBLE);
                }
                txtAllProducts.setVisibility(View.VISIBLE);
                linAllProducts.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
                linMain.setVisibility(View.VISIBLE);
                txtTvVideoTitle.setText(activity.getResources().getString(R.string.tv_list_title, categoryTitle));
                rvTvList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
                tvListAdapter = new CategoryTvAdapter(activity, arrListTvs, new OnTvClickListener() {
                    @Override
                    public void onTvClickListener(@NotNull String type, int pos, int itemPosition) {
                        if (type == "itemClick") {
                            onTvClickListener.onTvClickListener("itemClick", position, itemPosition);
                        }
                    }
                });
                rvTvList.setAdapter(tvListAdapter);
                tvListAdapter.notifyDataSetChanged();
            } else {
                txtAllProducts.setVisibility(View.GONE);
                viewBottom.setVisibility(View.GONE);
                linAllProducts.setVisibility(View.GONE);
                linMain.setVisibility(View.GONE);
                linTvVideos.setVisibility(View.GONE);
            }

            txtTvVideosView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onTvClickListener.onTvClickListener("viewAll", position, 0);
                }
            });
        }
    }

    private class HeaderItemView extends RecyclerView.ViewHolder {
        private TextView txtFilterItem, txtItemCount, txtRefine;
        private TextView txtSortItem;
        private LinearLayout linSortItem, linRefine;
        private LinearLayout linFilterItem;
        private RelativeLayout relRefine;
        private View viewFilterItem;

        public HeaderItemView(@NonNull View itemView) {
            super(itemView);
//            txtSortItem = itemView.findViewById(R.id.txtSortItem);
//            txtFilterItem = itemView.findViewById(R.id.txtFilterItem);
//            linSortItem = itemView.findViewById(R.id.linSortItem);
//            linFilterItem = itemView.findViewById(R.id.linFilterItem);
//            viewFilterItem = itemView.findViewById(R.id.viewFilterItem);
            relRefine = itemView.findViewById(R.id.relRefine);
            txtItemCount = itemView.findViewById(R.id.txtItemCount);
            linRefine = itemView.findViewById(R.id.linRefine);
            txtRefine = itemView.findViewById(R.id.txtRefine);
        }

        public void bind(ArrayList<ProductListingProductModel> arrListProductListing, int position) {
            txtItemCount.setTypeface(Global.INSTANCE.getFontMedium());
            txtRefine.setTypeface(Global.INSTANCE.getFontRegular());
            txtItemCount.setText(arrListProductListing.get(position).getTotalItem().toString() + " " + activity.getResources().getString(R.string.items));
            linRefine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.v("bind", "linSortItem clicked");
                    headerClickListener.onHeaderClicked("");
                }
            });

//            if (showFilterView) {
//                viewFilterItem.setVisibility(View.VISIBLE);
//            }
           /* linRefine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Log.v("bind", "linFilterItem clicked");
                    headerClickListener.onHeaderClicked("filter");

                }
            });*/
        }
    }


    class BannerItemView extends RecyclerView.ViewHolder {
        private View view, viewBanner, viewBannerSlider;
        private ImageView imgBanner;
        private ImageView ivBanner;
        private YouTubePlayerView youtubePlayer;
        private ConstraintLayout clVideo, clMain;
        private AutoScrollViewPager autoPager;
        private RelativeLayout relPager;
        private InstaDotView viewPagerIndicator;
        private LinearLayout linBanner;
        private LinearLayout linBannerImage;
        private TextView txtMyPicks;
        private RelativeLayout relMyPics;

        public ImageView getImgBanner() {
            return imgBanner;
        }

        public BannerItemView(View view) {
            super(view);
            this.view = view;
            imgBanner = view.findViewById(R.id.imgBanner);
            ivBanner = view.findViewById(R.id.ivBanner);
            youtubePlayer = view.findViewById(R.id.youtubePlayer);
            clVideo = view.findViewById(R.id.clVideo);
            autoPager = view.findViewById(R.id.autoPager);
            viewPagerIndicator = view.findViewById(R.id.viewPagerIndicator);
            imgBanner = view.findViewById(R.id.imgBanner);
            linBanner = view.findViewById(R.id.linBanner);
            txtMyPicks = view.findViewById(R.id.txtMyPicks);
            relMyPics = view.findViewById(R.id.relMyPics);
            linBannerImage = view.findViewById(R.id.linBannerImage);
            viewBanner = view.findViewById(R.id.viewBanner);
            viewBannerSlider = view.findViewById(R.id.viewBannerSlider);
            clMain = view.findViewById(R.id.clMain);
        }


        public void bind(ArrayList<ProductListingProductModel> arrListProductListing, int position, HomeItemClickInterface homeItemClickInterface) {
            ProductListingProductModel productModel = arrListProductListing.get(position);
            InfluencerDetails influencerDetails = productModel.getInfluencer_details();
            txtMyPicks.setTypeface(Global.INSTANCE.getFontMedium());
            txtMyPicks.setVisibility(View.VISIBLE);
            boolean influencerPresent = false;
            boolean videoPresent = false;
            boolean bannerPresent = false;

            if (influencerDetails != null) {
                if (influencerDetails.getBanners() != null && !influencerDetails.getBanners().isEmpty()) {
                    Log.i("ProductAdapter", "display influencer");
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            (int) Global.INSTANCE.getDimenVallue(view.getContext(), 135.0));
                    autoPager.setLayoutParams(params);
                    autoPager.setAdapter(new InfluencerBannerAdapter(view.getContext(), influencerDetails.getBanners(), homeItemClickInterface));
                    viewPagerIndicator.setNoOfPages(influencerDetails.getBanners().size());
                    viewPagerIndicator.setVisibleDotCounts(7);
                    //viewPagerIndicator.onPageChange(0);

                    autoPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            if (position > viewPagerIndicator.getNoOfPages() - 1) {
                                viewPagerIndicator.onPageChange(viewPagerIndicator.getNoOfPages() - 1);
                            } else {
                                viewPagerIndicator.onPageChange(position);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    linBanner.setVisibility(View.VISIBLE);
                    viewBannerSlider.setVisibility(View.VISIBLE);
                    viewBanner.setVisibility(View.VISIBLE);
                    relMyPics.setVisibility(View.VISIBLE);
                    influencerPresent = true;
                } else {
                    linBanner.setVisibility(View.VISIBLE);
                    viewBannerSlider.setVisibility(View.GONE);
                    viewBanner.setVisibility(View.GONE);
                    relMyPics.setVisibility(View.GONE);
                    ((ViewGroup.LayoutParams) linBanner.getLayoutParams()).height = 0;
                    influencerPresent = false;
                }
                if (productModel.getInfluencer_details().getTvs() != null && !productModel.getInfluencer_details().getTvs().isEmpty()) {
                    Log.i("ProductAdapter", "display tv");

                    clVideo.setVisibility(View.VISIBLE);
                    String strYoutubeID = productModel.getInfluencer_details().getTvs().get(0).getVideo_id();
                    Global.INSTANCE.loadImagesUsingGlide(view.getContext(), productModel.getInfluencer_details().getTvs().get(0).getImage_name(), imgBanner);
                    youtubePlayer.initialize(
                            initializedYouTubePlayer -> initializedYouTubePlayer.addListener(
                                    new AbstractYouTubePlayerListener() {
                                        @Override
                                        public void onReady() {
                                            initializedYouTubePlayer.loadVideo(strYoutubeID, 0);
                                            initializedYouTubePlayer.pause();
                                            imgBanner.setVisibility(View.GONE);
                                        }
                                    }), true);
                    relMyPics.setVisibility(View.GONE);
                    videoPresent = true;
                } else {
                    clVideo.setVisibility(View.GONE);
                    videoPresent = false;
                }
            } else {
                influencerPresent = false;
                videoPresent = false;
            }
            if (productModel.getTopBanner() != null) {
                Log.i("ProductAdapter", "display banner");

                linBannerImage.setVisibility(View.GONE);
                viewBannerSlider.setVisibility(View.GONE);
                viewBanner.setVisibility(View.GONE);
                System.out.println("image view" + productModel.getTopBanner().getBanner());
                int height = 0;
                if (!isFromStr.equalsIgnoreCase("categories")) {
                    height = 135;
                } else {
                    height = 100;
                }
                ivBanner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) Global.INSTANCE.getDimenVallue(view.getContext(), height)));



                Global.INSTANCE.loadImagesUsingGlide(view.getContext(), productModel.getTopBanner().getBanner(), ivBanner);
                bannerPresent = true;
            } else {
                viewBannerSlider.setVisibility(View.GONE);
                viewBanner.setVisibility(View.GONE);

                bannerPresent = false;
            }

            if (!influencerPresent && !bannerPresent && !videoPresent) {
                clMain.setVisibility(View.GONE);
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                param.height = 0;
                itemView.setLayoutParams(param);


                Log.i("ProductAdapter", "Nothing visible");
            }
        }

    }
}


