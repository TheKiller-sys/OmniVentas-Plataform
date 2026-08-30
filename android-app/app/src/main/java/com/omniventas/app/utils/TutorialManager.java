package com.omniventas.app.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.AnimationUtils;

import com.omniventas.app.R;
import com.omniventas.app.MainActivity;

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

    // Callback para cuando se completa el tutorial
    public interface TutorialCallback {
        void onTutorialComplete();
        void onTutorialSkip();
    }

    private TutorialCallback callback;

    public TutorialManager(Context context, ViewGroup rootView) {
        this.context = context;
        this.rootView = rootView;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.steps = createSteps(); // ✅ Usa el nuevo método
    }

    // Clase interna para pasos del tutorial
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

    // 🔥 CORREGIDO: SOLO 4 PASOS (SIN PERFIL)
    private List<TutorialStep> createSteps() {
        List<TutorialStep> steps = new ArrayList<>();
        
        // Paso 1: Bienvenida
        steps.add(new TutorialStep(
            "¡Bienvenido a OmniVentas! 🎉",
            "Estamos encantados de tenerte aquí. Esta guía te mostrará todo lo que necesitas saber para comenzar a vender.",
            R.drawable.ic_launcher_foreground
        ));
        
        // Paso 2: Dashboard
        steps.add(new TutorialStep(
            "Tu Panel de Control 📊",
            "Este es tu Dashboard. Aquí podrás ver tus ventas del día, ingresos, y las ventas más recientes en tiempo real.",
            R.drawable.ic_dashboard
        ));
        
        // Paso 3: Ventas
        steps.add(new TutorialStep(
            "Registrar Ventas 🛒",
            "En la sección de Ventas puedes buscar productos, seleccionarlos, ajustar cantidades y confirmar la venta en segundos.",
            R.drawable.ic_ventas
        ));
        
        // Paso 4: Inventario
        steps.add(new TutorialStep(
            "Inventario 📦",
            "Consulta el stock disponible, busca productos y mantén tu inventario actualizado. Los productos con poco stock se marcan en amarillo.",
            R.drawable.ic_inventario
        ));
        
        // ❌ ELIMINADO: Paso 5: Perfil - ya no se muestra
        
        return steps;
    }

    // 🔥 MÉTODO PRINCIPAL: Mostrar tutorial si es primera vez
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
        
        // Inflar el overlay
        LayoutInflater inflater = LayoutInflater.from(context);
        overlay = inflater.inflate(R.layout.overlay_tutorial, rootView, false);
        
        // Agregar al rootView
        rootView.addView(overlay, new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        
        // Animación de entrada
        overlay.setAlpha(0f);
        overlay.animate()
            .alpha(1f)
            .setDuration(300)
            .start();
        
        // Configurar controles
        setupControls();
        
        // Mostrar primer paso
        showStep(0);
    }

    private void setupControls() {
        Button btnNext = overlay.findViewById(R.id.btn_tutorial_siguiente);
        TextView tvSkip = overlay.findViewById(R.id.tv_tutorial_saltar);
        
        btnNext.setOnClickListener(v -> {
            if (currentStep < steps.size() - 1) {
                currentStep++;
                animateToStep(currentStep);
            } else {
                completeTutorial();
            }
        });
        
        tvSkip.setOnClickListener(v -> skipTutorial());
        
        // Animación del botón
        btnNext.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }

    private void showStep(int stepIndex) {
        TutorialStep step = steps.get(stepIndex);
        
        // Actualizar textos
        TextView tvTitulo = overlay.findViewById(R.id.tv_tutorial_titulo);
        TextView tvDescripcion = overlay.findViewById(R.id.tv_tutorial_descripcion);
        ImageView ivIcono = overlay.findViewById(R.id.iv_tutorial_icono);
        Button btnSiguiente = overlay.findViewById(R.id.btn_tutorial_siguiente);
        
        // Aplicar animación fade in
        tvTitulo.setAlpha(0f);
        tvDescripcion.setAlpha(0f);
        ivIcono.setAlpha(0f);
        
        tvTitulo.setText(step.titulo);
        tvDescripcion.setText(step.descripcion);
        ivIcono.setImageResource(step.icono);
        
        tvTitulo.animate().alpha(1f).setDuration(300).setStartDelay(100).start();
        tvDescripcion.animate().alpha(1f).setDuration(300).setStartDelay(200).start();
        ivIcono.animate().alpha(1f).setDuration(300).setStartDelay(300).start();
        
        // Actualizar botón según el paso
        if (stepIndex == steps.size() - 1) {
            btnSiguiente.setText("Comenzar a Vender 🚀");
        } else {
            btnSiguiente.setText("Siguiente →");
        }
        
        // Actualizar indicador de pasos
        updateStepIndicator();
    }

    private void animateToStep(int stepIndex) {
        LinearLayout contenido = overlay.findViewById(R.id.ll_contenido_tutorial);
        
        // Animación de salida
        contenido.animate()
            .alpha(0f)
            .translationX(-100f)
            .setDuration(200)
            .withEndAction(() -> {
                showStep(stepIndex);
                // Animación de entrada
                contenido.setTranslationX(100f);
                contenido.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .setDuration(300)
                    .start();
            })
            .start();
    }

    private void updateStepIndicator() {
        LinearLayout indicatorContainer = overlay.findViewById(R.id.ll_indicador_pasos);
        indicatorContainer.removeAllViews();
        
        for (int i = 0; i < steps.size(); i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                24, 6
            );
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            
            if (i == currentStep) {
                // Paso activo - más ancho y color primario
                params.width = 36;
                dot.setBackgroundColor(context.getColor(R.color.primary));
            } else if (i < currentStep) {
                // Pasos completados - color éxito
                dot.setBackgroundColor(context.getColor(R.color.success));
            } else {
                // Pasos futuros - gris
                dot.setBackgroundColor(context.getColor(R.color.gray_light));
            }
            
            indicatorContainer.addView(dot);
        }
    }

    public void completeTutorial() {
        // Guardar en preferencias
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
