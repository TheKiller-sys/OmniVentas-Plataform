package com.omniventas.app.utils;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.omniventas.app.R;
import com.omniventas.app.api.RetrofitClient;

public class ImageLoader {
    
    public static void loadProductImage(Context context, String url, ImageView imageView) {
        if (url == null || url.isEmpty()) {
            // Mostrar placeholder por defecto
            imageView.setImageResource(R.drawable.ic_product);
            return;
        }
        
        // Construir URL completa
        String fullUrl = getFullImageUrl(url);
        
        if (fullUrl == null) {
            imageView.setImageResource(R.drawable.ic_product);
            return;
        }
        
        // Cargar con Glide
        try {
            Glide.with(context)
                .load(fullUrl)
                .apply(new RequestOptions()
                    .placeholder(R.drawable.ic_product)
                    .error(R.drawable.ic_product)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(imageView);
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.ic_product);
        }
    }
    
    public static String getFullImageUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        
        if (url.startsWith("http")) {
            return url;
        }
        
        String baseUrl = RetrofitClient.getApiUrl();
        if (baseUrl == null) baseUrl = "https://omnisell-x19d.onrender.com/";
        
        // Asegurar que baseUrl termine en /
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        
        // Quitar el / inicial de la URL relativa
        String cleanUrl = url.startsWith("/") ? url.substring(1) : url;
        
        return baseUrl + cleanUrl;
    }
}
