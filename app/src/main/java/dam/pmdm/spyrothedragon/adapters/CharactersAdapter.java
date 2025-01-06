package dam.pmdm.spyrothedragon.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;

import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;
    private OnSpyroLongClickListener onSpyroLongClickListener;
    private static final String SPYRO_NAME = "Spyro";

    public interface OnSpyroLongClickListener {
        void onSpyroLongClick();
    }

    public void setOnSpyroLongClickListener(OnSpyroLongClickListener listener) {
        this.onSpyroLongClickListener = listener;
    }

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {
        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        holder.imageImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d("CharactersAdapter", "onLongClick called");

                if (character.getName().equals(SPYRO_NAME)) {

                    if (onSpyroLongClickListener != null) {
                        onSpyroLongClickListener.onSpyroLongClick();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
