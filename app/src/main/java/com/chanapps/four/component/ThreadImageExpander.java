package com.chanapps.four.component;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.chanapps.four.data.ChanFileStorage;
import com.chanapps.four.data.ChanPost;
import com.chanapps.four.gallery.ChanImage;
import com.chanapps.four.viewer.ThreadViewHolder;
import com.chanapps.four.viewer.ThreadViewer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: johnarleyburns
 * Date: 4/9/13
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadImageExpander {

    public static final String WEBVIEW_BLANK_URL = "about:blank";
    private static final String TAG = ThreadImageExpander.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final int BIG_IMAGE_SIZE_BYTES = 1024 * 250; // more than 250kb, show in web view
    private static final int BYTES_PER_PIXEL = 4; // Bitmap.Config.ARGB_8888;
    //private static final double MAX_EXPANDED_SCALE = 1.5;

    private ThreadViewHolder viewHolder;
    private String thumbUrl = null;
    private String postImageUrl = null;
    private int thumbW = 0;
    private int thumbH = 0;
    private int postW = 0;
    private int postH = 0;
    private String fullImagePath = null;
    private boolean withProgress;
    private int stub;
    private Point targetSize;
    private String postExt;
    private int fsize;
    private long postNo;
    private long threadNo;
    private String boardCode;
    private View.OnClickListener expandedImageListener;
    private boolean showContextMenu = true;
    private boolean isVideo = false;
    private View.OnClickListener videoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context c = viewHolder.list_item != null ? viewHolder.list_item.getContext() : null;
            if (c == null) {
                return;
            }
            Activity a = c instanceof Activity ? (Activity) c : null;
            String mimeType = ChanImage.videoMimeType(postExt);
            Uri uri = Uri.parse(postImageUrl);
            ChanImage.startViewer(a, uri, mimeType);
        }
    };

    public ThreadImageExpander(ThreadViewHolder viewHolder, final Cursor cursor, boolean withProgress, int stub, View.OnClickListener expandedImageListener, boolean showContextMenu) {
        this.viewHolder = viewHolder;
        this.withProgress = withProgress;
        this.stub = stub;
        this.expandedImageListener = expandedImageListener;

        boardCode = cursor.getString(cursor.getColumnIndex(ChanPost.POST_BOARD_CODE));
        long resto = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_RESTO));
        postNo = cursor.getLong(cursor.getColumnIndex(ChanPost.POST_ID));
        threadNo = resto > 0 ? postNo : resto;

        postExt = cursor.getString(cursor.getColumnIndex(ChanPost.POST_EXT));
        fsize = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_FSIZE));
        Uri uri = ChanFileStorage.getHiddenLocalImageUri(viewHolder.list_item.getContext(), boardCode, postNo, postExt);

        thumbW = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_TN_W));
        thumbH = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_TN_H));
        postW = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_W));
        postH = cursor.getInt(cursor.getColumnIndex(ChanPost.POST_H));
        postImageUrl = cursor.getString(cursor.getColumnIndex(ChanPost.POST_FULL_IMAGE_URL));
        thumbUrl = cursor.getString(cursor.getColumnIndex(ChanPost.POST_IMAGE_URL));
        if (postImageUrl != null && postImageUrl.endsWith(".gif")) {
            File thumbFile = ImageLoader.getInstance().getDiscCache().get(thumbUrl);
            fullImagePath = thumbFile != null ? thumbFile.getAbsolutePath() : null;
        } else {
            fullImagePath = (new File(URI.create(uri.toString()))).getAbsolutePath();
        }
        this.showContextMenu = showContextMenu;
        if (DEBUG) Log.i(TAG, "postUrl=" + postImageUrl + " postSize=" + postW + "x" + postH);
    }

    static public void setImageDimensions(ThreadViewHolder viewHolder, Point targetSize) {
        //ViewGroup.LayoutParams params = viewHolder.list_item_image_expanded.getLayoutParams();
        if (DEBUG) Log.i(TAG, "setImageDimensions() to " + targetSize.x + "x" + targetSize.y);
        if (viewHolder.list_item_image_expanded_click_effect != null) {
            ViewGroup.LayoutParams params2 = viewHolder.list_item_image_expanded_click_effect.getLayoutParams();
            if (params2 != null) {
                params2.width = targetSize.x;
                params2.height = targetSize.y;
            }
        }
        if (viewHolder.list_item_image_expanded_webview != null) {
            ViewGroup.LayoutParams params3 = viewHolder.list_item_image_expanded_webview.getLayoutParams();
            if (params3 != null) {
                params3.width = targetSize.x;
                params3.height = targetSize.y;
            }
        }
        if (viewHolder.list_item_image_expanded_wrapper != null) {
            ViewGroup.LayoutParams params4 = viewHolder.list_item_image_expanded_wrapper.getLayoutParams();
            if (params4 != null) {
                //params3.width = params.width; always match width
                params4.height = targetSize.y;
            }
        }
    }

    public void displayImage() {
        viewHolder.isWebView = true;
        isVideo = ChanImage.isVideo(postExt, fsize, postW, postH);
        int width = isVideo ? thumbW : postW;
        int height = isVideo ? thumbH : postH;
        targetSize = ThreadViewer.sizeHeaderImage(width, height, showContextMenu);
        if (DEBUG)
            Log.i(TAG, "inputSize=" + width + "x" + height + " targetSize=" + targetSize.x + "x" + targetSize.y);
        setImageDimensions(viewHolder, targetSize);
        displayWebView(width, height);
    }

    protected void displayWebView(int width, int height) {
        if (viewHolder.list_item_image_expanded_wrapper != null)
            viewHolder.list_item_image_expanded_wrapper.setVisibility(View.VISIBLE);
        if (viewHolder.list_item_image_wrapper != null)
            viewHolder.list_item_image_wrapper.setVisibility(View.GONE);
        if (viewHolder.list_item_image_header != null)
            viewHolder.list_item_image_header.setVisibility(View.GONE);

        WebView v = viewHolder.list_item_image_expanded_webview;
        if (v == null) return;
        //v.loadUrl(WEBVIEW_BLANK_URL); // needed so we don't get old image showing
        v.setVisibility(View.INVISIBLE);
        //v.setWebViewClient(webViewClient);

        if (DEBUG) Log.i(TAG, "Loading anim gif webview url=" + postImageUrl);
        int scale = calcScale(width, height);
        v.setInitialScale(scale);
        displayClickEffect();
        if (DEBUG)
            Log.i(TAG, "displayWebView() imageSize=" + width + "x" + height + " targetSize=" + targetSize.x + "x" + targetSize.y + " scale=" + scale);

        if (isVideo) {
            v.loadUrl(thumbUrl);
        } else {
            v.loadUrl(postImageUrl);
        }
    }

    private int calcScale(int width, int height) {
        float maxWidth = width > 1 ? width : 250;
        float maxHeight = height > 1 ? height : 250;
        float itemWidth = targetSize.x;
        float itemHeight = targetSize.y;
        return (int) Math.min(Math.ceil(itemWidth * 100 / maxWidth), Math.ceil(itemWidth * 100 / maxWidth));
    }

    private void displayClickEffect() {
        if (viewHolder.list_item_image_expanded_click_effect != null) {
            viewHolder.list_item_image_expanded_click_effect.setVisibility(View.VISIBLE);
            //viewHolder.list_item_image_expanded_click_effect.setOnClickListener(collapseImageListener);
            if (isVideo) {
                setVideoListener();
            } else {
                setGalleryListener();
            }
        }
    }

    private void setVideoListener() {
        viewHolder.list_item_image_expanded_click_effect.setOnClickListener(videoListener);
    }

    private void setGalleryListener() {
        viewHolder.list_item_image_expanded_click_effect.setOnClickListener(expandedImageListener);
    }
}
