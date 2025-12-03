package com.example.cineverse_movie_app_two.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.cineverse_movie_app_two.Models.Reel;
import com.example.cineverse_movie_app_two.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.ReelViewHolder> {

    private final Context context;
    private final List<Reel> items = new ArrayList<>();

    // Updated by activity on page change
    private int currentPlayingPosition = -1;

    public ReelAdapter(Context ctx) {
        this.context = ctx;
    }

    public void setCurrentPlayingPosition(int pos) {
        currentPlayingPosition = pos;
    }

    public void setItems(List<Reel> reels) {
        items.clear();
        if (reels != null) items.addAll(reels);
        notifyDataSetChanged();
    }

    public void addItem(Reel r) {
        if (r != null) {
            items.add(r);
            notifyItemInserted(items.size() - 1);
        }
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_reel, parent, false);
        ReelViewHolder holder = new ReelViewHolder(v);

        // Bind lifecycle to avoid leaks
        if (context instanceof LifecycleOwner) {
            ((LifecycleOwner) context).getLifecycle().addObserver(holder.youtubePlayerView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        Reel reel = items.get(position);

        // Movie info
        holder.tvMovieTitle.setText(reel.getMovieTitle());
        holder.tvMovieGenre.setText(
                reel.getGenre() == null || reel.getGenre().isEmpty()
                        ? "Unknown Genre" : reel.getGenre()
        );
        if (holder.tvMovieDescription != null) {
            holder.tvMovieDescription.setText(
                    reel.getDescription() != null ? reel.getDescription() : ""
            );
        }

        // Thumbnail reset
        if (holder.imgThumbnail != null) {
            holder.imgThumbnail.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(reel.getThumbnailUrl())
                    .centerCrop()
                    .into(holder.imgThumbnail);
        }

        // Loader reset
        holder.lottiePlaceholder.setVisibility(View.VISIBLE);
        holder.lottiePlaceholder.playAnimation();

        // Remove any old listener
        if (holder.playerListener != null) {
            holder.youtubePlayerView.removeYouTubePlayerListener(holder.playerListener);
        }

        holder.playerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                holder.youTubePlayer = youTubePlayer;

                // Live adapter position check to avoid stale holder use
                int adapterPos = holder.getAdapterPosition();
                if (adapterPos != RecyclerView.NO_POSITION
                        && adapterPos == currentPlayingPosition) {
                    holder.shouldPlayOnReady = true;
                    youTubePlayer.loadVideo(reel.getVideoKey(), 0f);
                } else {
                    youTubePlayer.cueVideo(reel.getVideoKey(), 0f);
                }
            }

            @Override
            public void onStateChange(@NonNull YouTubePlayer youTubePlayer,
                                      @NonNull PlayerConstants.PlayerState state) {
                // Hide loader/thumb on play, show on buffer
                if (state == PlayerConstants.PlayerState.PLAYING) {
                    holder.lottiePlaceholder.cancelAnimation();
                    holder.lottiePlaceholder.setVisibility(View.GONE);
                    if (holder.imgThumbnail != null)
                        holder.imgThumbnail.setVisibility(View.GONE);
                } else if (state == PlayerConstants.PlayerState.BUFFERING) {
                    holder.lottiePlaceholder.setVisibility(View.VISIBLE);
                    holder.lottiePlaceholder.playAnimation();
                    if (holder.imgThumbnail != null)
                        holder.imgThumbnail.setVisibility(View.VISIBLE);
                }
            }
        };

        holder.youtubePlayerView.addYouTubePlayerListener(holder.playerListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ReelViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        // Autoplay when visible
        holder.shouldPlayOnReady = true;
        if (holder.youTubePlayer != null
                && holder.getAdapterPosition() == currentPlayingPosition) {
            holder.youTubePlayer.play();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ReelViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Pause when offscreen
        holder.shouldPlayOnReady = false;
        if (holder.youTubePlayer != null) {
            holder.youTubePlayer.pause();
        }
    }

    @Override
    public void onViewRecycled(@NonNull ReelViewHolder holder) {
        // Clean up on recycle
        if (holder.playerListener != null) {
            holder.youtubePlayerView.removeYouTubePlayerListener(holder.playerListener);
            holder.playerListener = null;
        }
        holder.youTubePlayer = null;
        super.onViewRecycled(holder);
    }

    public static class ReelViewHolder extends RecyclerView.ViewHolder {
        public final YouTubePlayerView youtubePlayerView;
        public final LottieAnimationView lottiePlaceholder;
        public final TextView tvMovieTitle, tvMovieGenre, tvMovieDescription;
        public final ImageView imgThumbnail;
        public YouTubePlayer youTubePlayer;
        public AbstractYouTubePlayerListener playerListener;
        public boolean shouldPlayOnReady = false;

        public ReelViewHolder(@NonNull View itemView) {
            super(itemView);
            youtubePlayerView = itemView.findViewById(R.id.youtube_player_view);
            lottiePlaceholder = itemView.findViewById(R.id.lottie_placeholder);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieGenre = itemView.findViewById(R.id.tvMovieGenre);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            tvMovieDescription = itemView.findViewById(R.id.tvMovieDescription);
        }
    }
}
