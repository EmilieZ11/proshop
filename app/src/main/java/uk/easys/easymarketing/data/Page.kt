package uk.easys.easymarketing.data

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

class Page(
    @SerializedName("slider") var slider: Slider,
    @SerializedName("content") var content: Content
) {
    data class Slider(
        @SerializedName("list") var list: List<API.Product>
    )

    data class Content(
        @SerializedName("list") var list: List<Section>
    )

    data class Section(
        @Nullable @SerializedName("title") var title: String,
        @Nullable @SerializedName("icon") var icon: String,
        @SerializedName("type") var type: String,
        @Nullable @SerializedName("products") var products: List<API.Product>,
        @Nullable @SerializedName("categories") var categories: List<API.Category>,
        @Nullable @SerializedName("ads") var ads: List<Ad>
    )

    data class Ad(
        @SerializedName("id") var id: Long,
        @SerializedName("src") var src: String,
        @Nullable @SerializedName("link") var link: String
    )
}
