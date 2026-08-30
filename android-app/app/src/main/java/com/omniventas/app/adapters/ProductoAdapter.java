package com.omniventas.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.omniventas.app.R;
import com.omniventas.app.models.Producto;
import com.omniventas.app.utils.ImageLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private List<Producto> productos = new ArrayList<>();
    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
    }

    public ProductoAdapter(OnProductoClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_producto_busqueda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productos.get(position);
        
        // Mostrar nombre del producto
        holder.tvNombre.setText(p.getNombre());
        
        // Mostrar categoría/sección
        holder.tvSeccion.setText(p.getSeccion() != null ? p.getSeccion() : "Sin categoría");
        
        // Mostrar precio
        holder.tvPrecio.setText("$" + String.format("%.2f", p.getPrecio()));
        
        // ✅ NUEVO: Mostrar foto del producto en la búsqueda
        if (p.getFotoUrl() != null && !p.getFotoUrl().isEmpty()) {
            ImageLoader.loadProductImage(holder.itemView.getContext(), p.getFotoUrl(), holder.ivProductImage);
            holder.ivProductImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivProductImage.setVisibility(View.GONE);
        }
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductoClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos != null ? productos : new ArrayList<>();
        Collections.sort(this.productos, (p1, p2) -> p1.getNombre().compareToIgnoreCase(p2.getNombre()));
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvSeccion, tvPrecio;
        ImageView ivProductImage;  // ✅ NUEVO
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_busqueda);
            tvSeccion = itemView.findViewById(R.id.tv_seccion_busqueda);
            tvPrecio = itemView.findViewById(R.id.tv_precio_busqueda);
            ivProductImage = itemView.findViewById(R.id.iv_product_image_busqueda);  // ✅ NUEVO
        }
    }
}
