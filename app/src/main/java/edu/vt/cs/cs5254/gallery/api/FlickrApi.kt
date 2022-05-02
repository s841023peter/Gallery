package edu.vt.cs.cs5254.gallery.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface FlickrApi {
    @GET(
        "https://api.flickr.com/services/rest/" +
                "?method=flickr.interestingness.getList" +
                "&api_key=71191d334a898817dc658859a9780e20" +
                "&format=json" +
                "&nojsoncallback=1" +
                "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}