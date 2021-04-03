package uk.easys.easymarketing.data

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class API {
    companion object {
        private var retrofit: Retrofit? = null

        fun client(): Retrofit? {
            val interceptor = HttpLoggingInterceptor()
                .apply { level = HttpLoggingInterceptor.Level.BODY }
            retrofit = Retrofit.Builder()
                .baseUrl("http://a.ifaco.org/tfa/easy/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().addInterceptor(interceptor).build())
                .build()
            return retrofit
        }
    }

    interface APIInterface {
        @GET("user.json")
        fun user(): Call<User?>?

        @GET("cart.json")
        fun cart(): Call<Cart?>?

        @GET("home.json")
        fun home(): Call<Page?>?

        @GET("cate.json")
        fun cate(): Call<List<Category>?>?

        @GET("subc.json")
        fun subc(): Call<List<SubCat>?>?
    }


    data class User(
        @SerializedName("id") var id: Long,
        @SerializedName("fname") var fname: String,
        @SerializedName("lname") var lname: String,
        @SerializedName("mail") var mail: String,
        @SerializedName("tel") var tel: String,
        @SerializedName("photo") var photo: String
    )

    data class Category(
        @SerializedName("id") var id: Long,
        @SerializedName("title") var title: String,
        @SerializedName("src") var src: String
    )

    data class SubCat(
        @SerializedName("id") var id: Long,
        @SerializedName("cat") var cat: Long,
        @SerializedName("title") var title: String,
        @SerializedName("list") var list: List<Product>,
    )

    data class Product(
        @SerializedName("id") var id: Long,
        @SerializedName("src") var src: String,
        @SerializedName("title") var title: String,
        @SerializedName("desc") var desc: String,
        @SerializedName("oldPrice") var oldPrice: String,
        @Nullable @SerializedName("newPrice") var newPrice: String,
        @Nullable @SerializedName("discount") var discount: String,
        @SerializedName("book") var book: Byte
    )

    data class Order(
        @SerializedName("id") var id: Long,
        @SerializedName("product") var product: Long,
        @SerializedName("number") var number: Int
    )

    data class Cart(
        @SerializedName("orders") var orders: List<Order>,
        @SerializedName("products") var products: List<Product>
    )
}
