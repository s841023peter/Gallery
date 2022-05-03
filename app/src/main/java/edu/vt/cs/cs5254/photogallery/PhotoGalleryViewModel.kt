package edu.vt.cs.cs5254.photogallery

import androidx.lifecycle.ViewModel


class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData = FlickrFetchr.fetchPhotos()
}