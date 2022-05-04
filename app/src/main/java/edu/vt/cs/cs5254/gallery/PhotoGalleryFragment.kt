package edu.vt.cs.cs5254.gallery

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs.cs5254.gallery.api.GalleryItem
import edu.vt.cs.cs5254.gallery.databinding.FragmentPhotoGalleryBinding

class PhotoGalleryFragment : Fragment() {
    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PhotoGalleryViewModel by viewModels()
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.loadPhotos()

        val responseHandler = Handler(Looper.getMainLooper())
        thumbnailDownloader =
            ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
                val drawable = BitmapDrawable(resources, bitmap)
                photoHolder.bindDrawable(drawable)
                viewModel.storeThumbnail(photoHolder.galleryItem.id, drawable)
            }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewLifecycleOwner.lifecycle.addObserver(
            thumbnailDownloader.viewLifecycleObserver
        )
        // Initialize the binding (UI part)
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.photoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        return view
    }

    // Observer (inside it, we set adapter)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.galleryItemLiveData.observe(
            viewLifecycleOwner
        ) { galleryItems ->
            binding.photoRecyclerView.adapter = PhotoAdapter(galleryItems)
        }
    }

    // option menu callbacks
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reload_photos -> {
                viewModel.reloadPhotos()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private inner class PhotoHolder(itemImageView: ImageView) :
        RecyclerView.ViewHolder(itemImageView), View.OnClickListener {
        lateinit var galleryItem: GalleryItem

        init {
            itemView.setOnClickListener(this)
        }

        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
        fun bindGalleryItem(item: GalleryItem) {
            galleryItem = item
        }

        override fun onClick(view: View) {
            val intent = PhotoPageActivity
                .newIntent(requireContext(), galleryItem.photoPageUri)
            startActivity(intent)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner
            .lifecycle.removeObserver(
                thumbnailDownloader
                    .viewLifecycleObserver
            )
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(
            thumbnailDownloader
                .fragmentLifecycleObserver
        )
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PhotoHolder {
            val view = layoutInflater.inflate(
                // loyoutInflater is a property of a Fragment, need to use inner class
                R.layout.list_item_gallery,
                parent,
                false
            ) as ImageView
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size
        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindGalleryItem(galleryItem)
            val placeholder: Drawable = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.placeholder
            ) ?: ColorDrawable()
            holder.bindDrawable(placeholder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}