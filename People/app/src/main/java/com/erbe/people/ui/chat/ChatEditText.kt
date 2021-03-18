package com.erbe.people.ui.chat

import android.content.ClipData
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.TextViewRichContentReceiverCompat
import com.erbe.people.R

typealias OnImageAddedListener = (contentUri: Uri, mimeType: String, label: String) -> Unit

private val SUPPORTED_MIME_TYPES = setOf(
    "image/jpeg",
    "image/jpg",
    "image/png",
    "image/gif"
)

/**
 * A custom EditText with the ability to handle copy & paste of texts and images. This also works
 * with a software keyboard that can insert images.
 */
class ChatEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var onImageAddedListener: OnImageAddedListener? = null

    init {
        richContentReceiverCompat = object : TextViewRichContentReceiverCompat() {
            override fun onReceive(
                textView: TextView,
                clip: ClipData,
                source: Int,
                flags: Int
            ): Boolean {
                val mimeType = SUPPORTED_MIME_TYPES.find { clip.description.hasMimeType(it) }
                return if (mimeType != null && clip.itemCount > 0) {
                    onImageAddedListener?.invoke(
                        clip.getItemAt(0).uri,
                        mimeType,
                        clip.description.label.toString()
                    )
                    true
                } else {
                    super.onReceive(textView, clip, source, flags)
                }
            }

            override fun getSupportedMimeTypes(): Set<String> {
                return SUPPORTED_MIME_TYPES + super.getSupportedMimeTypes()
            }
        }
    }

    /**
     * Sets a listener to be called when a new image is added. This might be coming from copy &
     * paste or a software keyboard inserting an image.
     */
    fun setOnImageAddedListener(listener: OnImageAddedListener?) {
        onImageAddedListener = listener
    }
}