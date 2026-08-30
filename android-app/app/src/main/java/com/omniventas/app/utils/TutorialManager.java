package com.omniventas.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omniventas.app.R;

import java.util.ArrayList;
import java.util.List;

public class TutorialManager {
    private static final String TAG = "TutorialManager";
    private static final String PREFS_NAME = "OmniVentasTutorial";
    private static final String KEY_TUTORIAL_COMPLETED = "tutorial_completed";
    private static final String KEY_TUTORIAL_VERSION = "tutorial_version";
    private static final int TUTORIAL_VERSION = 1;

    private Context context;
    private ViewGroup rootView;
    private View overlay;
    private List<TutorialStep> steps;
    private int currentStep = 0;
    private boolean isRunning = false;
    private SharedPreferences prefs;

    public interface TutorialCallback {
        void onTutorialComplete();
        void onTutorialSkip();
    }

    private TutorialCallback callback;

    public TutorialManager(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.steps = createSteps();
    }

    public static class TutorialStep {
        String titulo;
        String descripcion;
        int icono;

        public TutorialStep(String titulo, String descripcion, int icono) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.icono = icono;
        }
    }

    private List<TutorialStep> createSteps() {
        List<TutorialStep> steps = new ArrayList<>();
        
        steps.add(new TutorialStep(
            "¡Bienvenido a OmniVentas! 🎉",
            "Estamos encantados de tenerte aquí. Esta guía te mostrará todo lo que necesitas saber para comenzar a vender.",
            R.drawable.ic_launcher_foreground
        ));
        
        steps.add(new TutorialStep(
            "Tu Panel de Control 📊",
            "Este es tu Dashboard. Aquí podrás ver tus ventas del día, ingresos, y las ventas más recientes en tiempo real.",
            R.drawable.ic_dashboard
        ));
        
        steps.add(new TutorialStep(
            "Registrar Ventas 🛒",
            "En la sección de Ventas puedes buscar productos, seleccionarlos, ajustar cantidades y confirmar la venta en segundos.",
            R.drawable.ic_ventas
        ));
        
        steps.add(new TutorialStep(
            "Inventario 📦",
            "Consulta el stock disponible, busca productos y mantén tu inventario actualizado.",
            R.drawable.ic_inventario
        ));
        
        return steps;
    }

    public void showTutorialIfNeeded() {
        boolean isCompleted = prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false);
        int version = prefs.getInt(KEY_TUTORIAL_VERSION, 0);
        
        if (!isCompleted || version < TUTORIAL_VERSION) {
            showTutorial();
        }
    }

    public void showTutorial() {
        if (isRunning) return;
        
        isRunning = true;
        currentStep = 0;
        
        LayoutInflater inflater = LayoutInflater.from(context);
        overlay = inflater.inflate(R.layout.overlay_tutorial, rootView, false);
        
        rootView.addView(overlay, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        overlay.setAlpha(0f);
        overlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
        
        setupControls();
        showStep(0);
    }

    private void setupControls() {
        Button btnNext = overlay.findViewById(R.id.btn_tutorial_siguiente);
        TextView tvSkip = overlay.findViewById(R.id.tv_tutorial_saltar);
        
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentStep < steps.size() - 1) {
                    currentStep++;
                    animateToStep(currentStep);
                } else {
                    completeTutorial();
                }
            });
        }
        
        if (tvSkip != null) {
            tvSkip.setOnClickListener(v -> skipTutorial());
        }
    }

    private void showStep(int stepIndex) {
        TutorialStep step = steps.get(stepIndex);
        
        TextView tvTitulo = overlay.findViewById(R.id.tv_tutorial_titulo);
        TextView tvDescripcion = overlay.findViewById(R.id.tv_tutorial_descripcion);
        ImageView ivIcono = overlay.findViewById(R.id.iv_tutorial_icono);
        Button btnSiguiente = overlay.findViewById(R.id.btn_tutorial_siguiente);
        
        if (tvTitulo != null) {
            tvTitulo.setAlpha(0f);
            tvTitulo.setText(step.titulo);
            tvTitulo.animate().alpha(1f).setDuration(300).setStartDelay(100).start();
        }
        
        if (tvDescripcion != null) {
            tvDescripcion.setAlpha(0f);
            tvDescripcion.setText(step.descripcion);
            tvDescripcion.animate().alpha(1f).setDuration(300).setStartDelay(200).start();
        }
        
        if (ivIcono != null) {
            ivIcono.setAlpha(0f);
            ivIcono.setImageResource(step.icono);
            ivIcono.animate().alpha(1f).setDuration(300).setStartDelay(300).start();
        }
        
        if (btnSiguiente != null) {
            if (stepIndex == steps.size() - 1) {
                btnSiguiente.setText("Comenzar a Vender 🚀");
            } else {
                btnSiguiente.setText("Siguiente →");
            }
        }
        
        updateStepIndicator();
    }

    // ✅ CORREGIDO: Usar variables finales para el lambda
    private void animateToStep(int stepIndex) {
        // Obtener la vista de contenido
        View contenidoView = overlay.findViewById(R.id.ll_contenido_tutorial);
        if (contenidoView == null) {
            contenidoView = overlay;
        }
        
        // ✅ Crear variables finales para usar en el lambda
        final View viewToAnimate = contenidoView;
        
        // Animación de salida
        viewToAnimate.animate()
            .alpha(0f)
            .translationX(-100f)
            .setDuration(200)
            .withEndAction(() -> {
                showStep(stepIndex);
                viewToAnimate.setTranslationX(100f);
                viewToAnimate.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(300)
                    .start();
            })
            .start();
    }

    private void updateStepIndicator() {
        LinearLayout indicatorContainer = overlay.findViewById(R.id.ll_indicador_pasos);
        if (indicatorContainer == null) return;
        
        indicatorContainer.removeAllViews();
        
        for (int i = 0; i < steps.size(); i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 6);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            
            if (i == currentStep) {
                params.width = 36;
                dot.setBackgroundColor(context.getColor(R.color.primary));
            } else if (i < currentStep) {
                dot.setBackgroundColor(context.getColor(R.color.success));
            } else {
                dot.setBackgroundColor(context.getColor(R.color.gray_light));
            }
            
            indicatorContainer.addView(dot);
        }
    }

    public void completeTutorial() {
        prefs.edit()
            .putBoolean(KEY_TUTORIAL_COMPLETED, true)
            .putInt(KEY_TUTORIAL_VERSION, TUTORIAL_VERSION)
            .apply();
        
        dismissTutorial();
        
        if (callback != null) {
            callback.onTutorialComplete();
        }
        
        Toast.makeText(context, "¡Tutorial completado! ¡A vender! 🎉", Toast.LENGTH_SHORT).show();
    }

    public void skipTutorial() {
        prefs.edit()
            .putBoolean(KEY_TUTORIAL_COMPLETED, true)
            .putInt(KEY_TUTORIAL_VERSION, TUTORIAL_VERSION)
            .apply();
        
        dismissTutorial();
        
        if (callback != null) {
            callback.onTutorialSkip();
        }
    }

    private void dismissTutorial() {
        if (overlay != null) {
            overlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    if (overlay != null && overlay.getParent() != null) {
                        ((ViewGroup) overlay.getParent()).removeView(overlay);
                    }
                    overlay = null;
                    isRunning = false;
                })
                .start();
        }
    }

    public void setCallback(TutorialCallback callback) {
        this.callback = callback;
    }

    public boolean isTutorialCompleted() {
        return prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false);
    }
}
