package com.mrezanasirloo.data.loader;

import android.content.Context;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mrezanasirloo.data.SortOrder;
import com.mrezanasirloo.domain.model.AlbumDomain;
import com.mrezanasirloo.domain.model.ArtistDomain;
import com.mrezanasirloo.domain.model.SongDomain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class ArtistLoader {
    public static String getSongLoaderSortOrder(Context context) {
        return SortOrder.ArtistSortOrder.ARTIST_A_Z + ", " + SortOrder.ArtistAlbumSortOrder.ALBUM_A_Z + ", " + SortOrder.AlbumSongSortOrder.SONG_A_Z;
    }

    @NonNull
    public static List<ArtistDomain> getAllArtists(@NonNull final Context context) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                null,
                null,
                getSongLoaderSortOrder(context))
        );
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songDomains));
    }

    @NonNull
    public static List<ArtistDomain> getArtists(@NonNull final Context context, String query) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST + " LIKE ?",
                new String[]{"%" + query + "%"},
                getSongLoaderSortOrder(context))
        );
        return splitIntoArtists(AlbumLoader.splitIntoAlbums(songDomains));
    }

    @NonNull
    public static ArtistDomain getArtist(@NonNull final Context context, int artistId) {
        ArrayList<SongDomain> songDomains = SongLoader.getSongs(SongLoader.makeSongCursor(
                context,
                AudioColumns.ARTIST_ID + "=?",
                new String[]{String.valueOf(artistId)},
                getSongLoaderSortOrder(context))
        );
        return new ArtistDomain(AlbumLoader.splitIntoAlbums(songDomains));
    }

    @NonNull
    public static List<ArtistDomain> splitIntoArtists(@Nullable final List<AlbumDomain> albumDomains) {
        List<ArtistDomain> artistDomains = new ArrayList<>();
        if (albumDomains != null) {
            for (AlbumDomain albumDomain : albumDomains) {
                getOrCreateArtist(artistDomains, albumDomain.getArtistId()).getAlbumDomains().add(albumDomain);
            }
        }
        return artistDomains;
    }

    private static ArtistDomain getOrCreateArtist(List<ArtistDomain> artistDomains, int artistId) {
        for (ArtistDomain artistDomain : artistDomains) {
            if (!artistDomain.getAlbumDomains().isEmpty() && !artistDomain.getAlbumDomains().get(0).getSongDomains().isEmpty() && artistDomain
                    .getAlbumDomains().get(0).getSongDomains().get(0).getArtistId() == artistId) {
                return artistDomain;
            }
        }
        ArtistDomain artistDomain = new ArtistDomain();
        artistDomains.add(artistDomain);
        return artistDomain;
    }
}
