package edu.vt.cs.cs5254.gallery

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData = FlickrFetchr.galleryItemsLiveData

    fun loadPhotos() = FlickrFetchr.fetchPhotos(false)

    fun reloadPhotos() = FlickrFetchr.fetchPhotos(true)

    fun storeThumbnail(id: String, drawable: Drawable) {
        FlickrFetchr.storeThumbnail(id, drawable)
    }
}