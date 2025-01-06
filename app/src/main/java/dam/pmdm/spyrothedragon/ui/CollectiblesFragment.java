package dam.pmdm.spyrothedragon.ui;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.adapters.CollectiblesAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCollectiblesBinding;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesFragment extends Fragment implements CollectiblesAdapter.OnGemClickListener {

    private FragmentCollectiblesBinding binding;
    private RecyclerView recyclerView;
    private CollectiblesAdapter adapter;
    private List<Collectible> collectiblesList;

    private int contadorToques = 0;
    private Runnable reinicioContador;
    private boolean pulsadoGema = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCollectiblesBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewCollectibles;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        collectiblesList = new ArrayList<>();
        adapter = new CollectiblesAdapter(collectiblesList);
        recyclerView.setAdapter(adapter);
        adapter.setOnGemClickListener(this);

        loadCollectibles();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        reinicioContador = new Runnable() {
            @Override
            public void run() {
                contadorToques = 0; // Reiniciar el contador
                pulsadoGema = false; // Reiniciar la variable
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Método para reproducir el video secreto
    private void reproducirVideoSecreto() {
        final Dialog dialogo = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogo.setContentView(R.layout.dialog_video);
        dialogo.setCancelable(false); // Evitar que se cierre al tocar fuera


        final VideoView videoView = dialogo.findViewById(R.id.videoView);

        final Button btnAtras = dialogo.findViewById(R.id.btnAtras);

        String rutaVideo = "android.resource://" + getContext().getPackageName() + "/" + R.raw.video_secreto;
        Log.d("CollectiblesFragment", "Ruta del video: " + rutaVideo);
        Uri uri = Uri.parse(rutaVideo);
        videoView.setVideoURI(uri);


        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialogo.dismiss();
            }
        });

        dialogo.setOnDismissListener(dialog1 -> {
            videoView.stopPlayback();
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogo.dismiss();
            }
        });

        dialogo.show();
        Log.d("CollectiblesFragment", "Dialogo mostrado");

        videoView.start();
    }


    @Override
    public void onGemClick(ImageView gemImageView) {
        pulsadoGema = true;
        if (!pulsadoGema) {
            return;
        }
        contadorToques++;
        gemImageView.removeCallbacks(reinicioContador);
        gemImageView.postDelayed(reinicioContador, 2000);

        if (contadorToques == 4) {
            contadorToques = 0;
            pulsadoGema = false;
            reproducirVideoSecreto(); // Reproducir el video secreto
        }
    }

    private void loadCollectibles() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.collectibles);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Collectible currentCollectible = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName)) {
                            currentCollectible = new Collectible();
                        } else if (currentCollectible != null) {
                            if ("name".equals(tagName)) {
                                currentCollectible.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCollectible.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCollectible.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("collectible".equals(tagName) && currentCollectible != null) {
                            collectiblesList.add(currentCollectible);
                        }
                        break;
                }

                eventType = parser.next();
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}