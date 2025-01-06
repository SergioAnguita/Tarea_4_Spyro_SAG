package dam.pmdm.spyrothedragon;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private MediaPlayer botonGuia;

    private MediaPlayer pasoGuia;

    private ActivityMainBinding binding;
    NavController navController = null;
    // Nombre del archivo de SharedPreferences
    private static final String PREFS_NAME = "MyPrefs";
    // Clave para el valor que indica si la guía se ha mostrado
    private static final String GUIDE_SHOWN = "guideShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar y preparar el MediaPlayer
        botonGuia = MediaPlayer.create(this, R.raw.pause);


        // 1. Obtener una Instancia de SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 2. Leer el Valor
        boolean guideShown = prefs.getBoolean(GUIDE_SHOWN, false); // false es el valor por defecto

        // 3. Mostrar la Guía (o No)
        if (!guideShown) {
            // La guía no se ha mostrado, mostrarla
            binding.inicioGuia.setVisibility(0);
            binding.botonContinuarGuia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.inicioGuia.setVisibility(8);
                    binding.navView.setVisibility(0);
                    binding.navHostFragment.setVisibility(0);
                    iniciarGuia();
                    // Reproducir el sonido de clic del botón
                    if (botonGuia != null) {
                        botonGuia.start();
                    }
                }
            });
        } else {
            // La guía ya se ha mostrado, no hacer nada
            binding.inicioGuia.setVisibility(8);
            binding.navView.setVisibility(0);
            binding.navHostFragment.setVisibility(0);
        }


        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar los recursos del MediaPlayer
        if (botonGuia != null) {
            botonGuia.release();
            botonGuia = null;
        }
    }

    public void iniciarGuia() {
        binding.includeLayout.guia.setVisibility(0);
        //boton para saltar la guia
        binding.includeLayout.saltarGuia.setOnClickListener(this::saltarGuia);

        animar(binding.includeLayout.infoGuia);
        binding.includeLayout.textoGuia.setText(R.string.texto_info);
        animacionLatido(binding.includeLayout.textoGuia);

        // Variable para llevar la cuenta de las pulsaciones
        final int[] pulsaciones = {0};


        pasoGuia = MediaPlayer.create(this, R.raw.sparx);


        binding.includeLayout.botonSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pulsaciones[0]++; // Incrementar el contador de pulsaciones
                if (pasoGuia != null) {
                    pasoGuia.start();
                }
                switch (pulsaciones[0]) {
                    case 1:
                        // Transición para el primer paso
                        transicion(binding.includeLayout.infoGuia, binding.includeLayout.personajeGuia, R.string.texto_personajes);
                        break;
                    case 2:
                        // Transición para el segundo paso
                        transicion(binding.includeLayout.personajeGuia, binding.includeLayout.mundosGuia, R.string.texto_mundos);
                        break;
                    case 3:
                        // Transición para el tercer paso
                        transicion(binding.includeLayout.mundosGuia, binding.includeLayout.coleccionGuia, R.string.texto_colecciones);
                        // 4. Almacenar el Valor (cuando se completa la guía)
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(GUIDE_SHOWN, true);
                        editor.apply();
                        break;
                    default:
                        // Si se pulsa más de 3 veces, puedes hacer algo diferente o reiniciar el contador
                        binding.finalGuia.setVisibility(0);
                        binding.includeLayout.guia.setVisibility(8);
                        binding.navView.setVisibility(8);
                        binding.navHostFragment.setVisibility(8);

                        binding.botonFinalizarGuia.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.finalGuia.setVisibility(8);
                                binding.navView.setVisibility(0);
                                binding.navHostFragment.setVisibility(0);

                                if (botonGuia != null) {
                                    botonGuia.start();
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    private void transicion(final ImageView viewOut, final ImageView viewIn, final int textResId) {
        // Desvanecer la vista actual
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewOut, "alpha", 1f, 0f);
        fadeOut.setDuration(500);

        // Aparecer la nueva vista
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewIn, "alpha", 0f, 1f);
        fadeIn.setDuration(500);

        //Animacion de movimiento
        ObjectAnimator move = ObjectAnimator.ofFloat(viewIn, "translationX", 0f, 50f, 0f, -50f, 0f);
        move.setDuration(1000);

        // Crear un AnimatorSet para ejecutar las animaciones en secuencia
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(fadeOut, fadeIn, move);

        // Añadir un listener para cambiar el contenido después del fadeOut
        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Ocultar la vista anterior
                viewOut.setVisibility(View.GONE);
                // Mostrar la nueva vista
                viewIn.setVisibility(View.VISIBLE);
                // Actualizar el texto
                binding.includeLayout.textoGuia.setText(textResId);
            }
        });

        // Iniciar la animación
        animatorSet.start();
    }

    public void animar(ImageView imageView) {

        imageView.setVisibility(0);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 0.5f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(imageView, "alpha", 1f, 0f);

        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).before(fadeIn);
        animatorSet.setDuration(1000);
        animatorSet.start();
    }

    public void animacionLatido(TextView textView) {
        // 1. Obtener Referencia al TextView (ya la tienes como parámetro)

        // 2. Crear ObjectAnimator para scaleX
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(textView, "scaleX", 1f, 1.1f, 1f, 0.9f, 1f);
        // 3. Crear ObjectAnimator para scaleY
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(textView, "scaleY", 1f, 1.1f, 1f, 0.9f, 1f);

        // 4. Definir Duración y Otros Parámetros
        scaleXAnimator.setDuration(500); // Duración de 0.5 segundos
        scaleYAnimator.setDuration(500); // Duración de 0.5 segundos

        // Repetición
        scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repetir infinitamente
        scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repetir infinitamente
        scaleXAnimator.setRepeatMode(ValueAnimator.RESTART); // Modo de repetición REVERSE
        scaleYAnimator.setRepeatMode(ValueAnimator.RESTART); // Modo de repetición REVERSE

        // Interpolador
        scaleXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        // 5. Iniciar la Animación (usando AnimatorSet para que se ejecuten juntas)
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();
    }


    public void saltarGuia(View view) {
        binding.includeLayout.guia.setVisibility(8);
    }


    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }
}