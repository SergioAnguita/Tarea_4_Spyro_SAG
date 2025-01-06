package dam.pmdm.spyrothedragon.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import dam.pmdm.spyrothedragon.models.Character;
import dam.pmdm.spyrothedragon.adapters.CharactersAdapter;
import dam.pmdm.spyrothedragon.databinding.FragmentCharactersBinding;


public class CharactersFragment extends Fragment implements CharactersAdapter.OnSpyroLongClickListener {

    private FragmentCharactersBinding binding;
    private SpyroFireView spyroFireView;
    private Handler handler = new Handler();
    private RecyclerView recyclerView;
    private CharactersAdapter adapter;
    private List<Character> charactersList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCharactersBinding.inflate(inflater, container, false);

        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        charactersList = new ArrayList<>();
        adapter = new CharactersAdapter(charactersList);
        recyclerView.setAdapter(adapter);

        loadCharacters();
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spyroFireView = view.findViewById(R.id.spyroFireView);
        recyclerView = binding.recyclerViewCharacters;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        charactersList = new ArrayList<>();
        loadCharacters();
        adapter = new CharactersAdapter(charactersList);
        recyclerView.setAdapter(adapter);
        // Asignar el listener al Adapter
        adapter.setOnSpyroLongClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSpyroLongClick() {
        Log.d("CharactersFragment", "onSpyroLongClick called");
        spyroFireView.setVisibility(View.VISIBLE);
        spyroFireView.startAnimation();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                spyroFireView.setVisibility(View.GONE);
                spyroFireView.stopAnimation();
            }
        }, 3000);
    }

    private void loadCharacters() {
        try {
            // Cargamos el archivo XML desde res/xml (NOTA: ahora se usa R.xml.characters)
            InputStream inputStream = getResources().openRawResource(R.raw.characters);

            // Crear un parser XML
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            int eventType = parser.getEventType();
            Character currentCharacter = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = null;

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName)) {
                            currentCharacter = new Character();
                        } else if (currentCharacter != null) {
                            if ("name".equals(tagName)) {
                                currentCharacter.setName(parser.nextText());
                            } else if ("description".equals(tagName)) {
                                currentCharacter.setDescription(parser.nextText());
                            } else if ("image".equals(tagName)) {
                                currentCharacter.setImage(parser.nextText());
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();

                        if ("character".equals(tagName) && currentCharacter != null) {
                            charactersList.add(currentCharacter);
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