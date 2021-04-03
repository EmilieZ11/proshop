package uk.easys.easymarketing

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Model : ViewModel() {
    // User
    val userId: MutableLiveData<Long?> by lazy { MutableLiveData<Long?>() }
    val userFName: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val userLName: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val userMail: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val userTel: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }
    val userAvatar: MutableLiveData<String?> by lazy { MutableLiveData<String?>() }

    // Main
    val loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val tbMode: MutableLiveData<Main.ToolbarMode> by lazy { MutableLiveData<Main.ToolbarMode>() }
    // Don't put anything inside the parentheses
    val cartFactor: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }

    // Home
    val currentPage: MutableLiveData<Int> by lazy { MutableLiveData<Int>(0) }
}
