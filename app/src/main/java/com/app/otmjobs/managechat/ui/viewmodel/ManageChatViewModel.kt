package com.app.otmjobs.managechat.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.traceErrorException
import com.app.otmjobs.managechat.data.model.ChatUsersListResponse
import com.app.otmjobs.managechat.data.repository.ManageChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONException
import java.util.concurrent.CancellationException

class ManageChatViewModel(private val manageChatRepository: ManageChatRepository) :
    ViewModel() {
    val chatUsersListResponse = MutableLiveData<ChatUsersListResponse>()

    fun getChatUserList(guard: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val guardBody: RequestBody = AppUtils.getRequestBody(guard)
            try {
                val response = manageChatRepository.getChatUserList(guardBody)
                withContext(Dispatchers.Main) {
                    chatUsersListResponse.value = response
                }
            } catch (e: JSONException) {
                traceErrorException(e)
            } catch (e: CancellationException) {
                traceErrorException(e)
            } catch (e: Exception) {
                traceErrorException(e)
            }
        }
    }
}