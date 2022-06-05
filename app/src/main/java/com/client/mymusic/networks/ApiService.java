
package com.client.mymusic.networks;

import com.client.mymusic.entities.AccessToken;
import com.client.mymusic.entities.ArtistResponse;
import com.client.mymusic.entities.AudioResponse;
import com.client.mymusic.entities.ChartsResponse;
import com.client.mymusic.entities.CheckLike;
import com.client.mymusic.entities.CountryResponse;
import com.client.mymusic.entities.PersonalPlaylistHostResponse;
import com.client.mymusic.entities.PersonalPlaylistResponse;
import com.client.mymusic.entities.PlaylistResponse;
import com.client.mymusic.entities.SlideshowResponse;
import com.client.mymusic.entities.ThemeResponse;
import com.client.mymusic.entities.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("register")
    @FormUrlEncoded
    Call<AccessToken> register(@Field("name") String name, @Field("email") String email, @Field("password") String password, @Field("password_confirmation") String passwordComfirmation);

    @POST("social_auth")
    @FormUrlEncoded
    Call<AccessToken> socialAuth(@Field("name") String name,
                                 @Field("email") String email,
                                 @Field("provider") String provider,
                                 @Field("provider_user_id") String providerUserId);

    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("username") String username, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("change-password")
    @FormUrlEncoded
    Call<Boolean> changePassword(@Field("password") String password, @Field("new_password") String newPassword);

    @GET("user")
    Call<User> user();

    @GET("logout")
    Call<Boolean> logout();

    @POST("filter-song")
    @FormUrlEncoded
    Call<AudioResponse> Filter(@Field("filter") String filter);

    @GET("artists")
    Call<ArtistResponse> artists();

    @GET("themes")
    Call<ThemeResponse> themes();

    @GET("countrys")
    Call<CountryResponse> countrys();

    @GET("slideshows")
    Call<SlideshowResponse> slideshows();

    @GET("playlists-hot")
    Call<PlaylistResponse> playlistHot();

    @GET("top-songs")
    Call<AudioResponse> topSongs();

    @GET("all-songs")
    Call<AudioResponse> allSongs();

    @GET("update-views-song/{id}")
    Call<Boolean> updateViewsSong(@Path("id") int id);

    @GET("update-dowloads-song/{id}")
    Call<Boolean> updateDowloadsSong(@Path("id") int id);

    @GET("update-views-personal-playlist/{id}")
    Call<Boolean> updateViewsPersonalPlaylist(@Path("id") int id);

    @GET("playlists/{id}")
    Call<PlaylistResponse> playlist(@Path("id") int id);

    @GET("playlist-songs/{id}")
    Call<AudioResponse> playlistSong(@Path("id") int id);

    @GET("personal-playlist-hots")
    Call<PersonalPlaylistHostResponse> personalPlaylistHost();

    @GET("personal-playlists")
    Call<PersonalPlaylistResponse> personalPlaylist();

    @POST("create-personal-playlist")
    @FormUrlEncoded
    Call<Boolean> CreatePersonalPlaylist(@Field("name") String name);

    @POST("edit-personal-playlist/{id}")
    @FormUrlEncoded
    Call<Boolean>EditPersonalPlaylist(@Path("id") int id, @Field("name") String name);

    @GET("delete-personal-playlist/{id}")
    Call<Boolean> deletePersonalPlaylist(@Path("id") int id);

    @POST("share-personal-playlists/{id}")
    Call<Boolean> sharePersonalPlaylist(@Path("id") int id);

    @GET("add-to-personal-playlists/{idPlaylist}/{idSong}")
    Call<Boolean> addToPersonalPlaylists(@Path("idPlaylist") int idPlaylist, @Path("idSong") int idSong);

    @GET("delete-personal-playlist-song/{idPlaylist}/{idSong}")
    Call<Boolean> deletePersonalPlaylistSong(@Path("idPlaylist") int idPlaylist, @Path("idSong") int idSong);

    @GET("personal-playlist-songs/{id}")
    Call<AudioResponse> personalPlaylistSongs(@Path("id") int id);

    @GET("artist-songs/{id}")
    Call<AudioResponse> artistSong(@Path("id") int id);

    @GET("charts")
    Call<ChartsResponse> charts();

    @GET("charts-songs/{id}")
    Call<AudioResponse> chartsSongs(@Path("id") int id);

    @GET("check-like/{id}")
    Call<CheckLike> checkLike(@Path("id") int id);

    @GET("like/{id}")
    Call<Boolean> like(@Path("id") int id);

    @Multipart
    @POST("edit-profile")
    Call<Boolean>  editProfile(
            @Part("full_name") RequestBody full_name,
            @Part MultipartBody.Part file
    );

}
