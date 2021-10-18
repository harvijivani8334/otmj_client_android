package com.app.otmjobs.managechat.ui.activity


import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.imagepickers.models.FileWithPath
import com.app.imagepickers.utils.Constant
import com.app.imagepickers.utils.FileUtils
import com.app.imagepickers.utils.GlideUtil
import com.app.otmjobs.BuildConfig
import com.app.otmjobs.R
import com.app.otmjobs.common.callback.SelectAttachmentListener
import com.app.otmjobs.common.callback.SelectItemListener
import com.app.otmjobs.common.data.model.ModuleInfo
import com.app.otmjobs.common.ui.activity.BaseActivity
import com.app.otmjobs.common.ui.fragment.SelectAttachmentDialog
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.common.utils.AppUtils
import com.app.otmjobs.common.utils.LastSeenTime
import com.app.otmjobs.common.utils.PopupMenuHelper
import com.app.otmjobs.databinding.ActivityUserChatBinding
import com.app.otmjobs.databinding.RowChatLeftItemBinding
import com.app.otmjobs.databinding.RowChatRightItemBinding
import com.app.otmjobs.managechat.data.model.*
import com.app.utilities.callback.DialogButtonClickListener
import com.app.utilities.utils.AlertDialogHelper
import com.app.utilities.utils.StringHelper
import com.app.utilities.utils.ToastHelper
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.toolbar_main.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.parceler.Parcels
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : BaseActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks,
    SelectAttachmentListener, SelectItemListener, DialogButtonClickListener {
    private lateinit var db: FirebaseFirestore
    private lateinit var mContext: Context
    private var adapter: MessageAdapter? = null
    private lateinit var binding: ActivityUserChatBinding
    private var currentUser: String = ""
    private var friendId: String = ""
    private lateinit var channelInfo: ChannelInfo
    private var currentPhotoPath: String = ""
    private var imagePath: String = ""
    private var lastCompletelyVisibleItemPosition: Int = 0
    private var lastVisibleMessageId: String = ""
    private var newChatCount: Int = 0
    private var selectedMessageItem: Int = 0
    private var sendMessageType: Int = 0
    private var initialLoaded: Boolean = false
    private val users: HashMap<String, ChatUserInfo> = HashMap()
    private var statusInfo: StatusInfo? = null
    private var isMessageSelected = false
    private lateinit var menu: Menu
    private lateinit var listMenuItems: MutableList<ModuleInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_chat)
        mContext = this
        db = FirebaseFirestore.getInstance()
        statusInfo = StatusInfo()
        currentUser = AppUtils.getFirebaseUserId(mContext)
        binding.imgSend.setOnClickListener(this)
        binding.imgAttachment.setOnClickListener(this)
        binding.routMoveToBottom.setOnClickListener(this)
        binding.imgRemoveSendImage.setOnClickListener(this)
        binding.imgRemoveReplayView.setOnClickListener(this)
        binding.previewImage.imgClosePreviewImage.setOnClickListener(this)

        getIntentData()

//        GlobalScope.launch(Dispatchers.IO) {
        getAllUserDetails()
//        }

        /*   binding.scrollView.setOnScrollChangeListener(
               NestedScrollView.OnScrollChangeListener { _, scrollX, scrollY, _, oldScrollY ->
                   Log.e("test", "setOnScrollChangeListener:");
                   if (scrollY > oldScrollY) {
                       val totalItens = adapter!!.itemCount

                       val currentView =
                           binding.rvUserChat.findChildViewUnder(scrollX.toFloat(), scrollY.toFloat())
                       val childPosition = binding.rvUserChat.getChildAdapterPosition(currentView!!)

                       Log.e("test", "childPosition:" + childPosition);

   //                    if((totalItens/2) - (stepSize/2) <= childPosition && !isLoading){
   //                        isLoading = true
   //                        //skip , take
   //                        timelinePresenter.loadMore(totalItens, 5)
   //                    }
                   }
               })*/
    }

    private fun getIntentData() {
        if (intent != null && intent.extras != null) {
            showProgressDialog(mContext, "")
            channelInfo =
                Parcels.unwrap(intent.getParcelableExtra(AppConstants.IntentKey.CHANNEL_INFO))
            setupToolbar("", true)

            if (!StringHelper.isEmpty(channelInfo.roomName))
                binding.txtJobTitle.text = channelInfo.roomName

            val query = db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
                .collection(AppConstants.FCM_MESSAGES)
                .orderBy("timestamp")

            val options =
                FirestoreRecyclerOptions.Builder<MessageInfo>()
                    .setQuery(query, MessageInfo::class.java)
                    .build()

            val linearLayoutManager = LinearLayoutManager(mContext)
            /*  linearLayoutManager.reverseLayout = true
              linearLayoutManager.stackFromEnd = true*/
            adapter = MessageAdapter(options)
            binding.rvUserChat.layoutManager = linearLayoutManager
//            binding.rvUserChat.setHasFixedSize(true)
            binding.rvUserChat.adapter = adapter
            setScrollEvent()

            if (adapter != null)
                adapter!!.startListening()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgSend -> {
                when (sendMessageType) {
                    AppConstants.Type.MESSAGE_TYPE_TEXT -> sendMessage(null)
                    AppConstants.Type.MESSAGE_TYPE_IMAGE -> {
                        sendImage(getFileInfo(File(imagePath)), File(imagePath))
                        removeSendImage()
                    }
                    AppConstants.Type.MESSAGE_TYPE_REPLAY -> {
                        val info = adapter!!.getItem(selectedMessageItem)
                        val messageId = adapter!!.snapshots.getSnapshot(selectedMessageItem).id
                        val replayInfo = ReplayMessageInfo(
                            info.content,
                            info.sender_id,
                            messageId,
                            info.file
                        )
                        sendMessage(replayInfo)
                    }
                }
            }
            R.id.imgAttachment -> checkPermission()
            R.id.routMoveToBottom -> binding.rvUserChat.smoothScrollToPosition(adapter!!.itemCount - 1)
            R.id.imgRemoveSendImage -> removeSendImage()
            R.id.imgRemoveReplayView -> removeReplayView()
            R.id.imgClosePreviewImage -> closePreviewImage()
        }
    }

    private fun sendMessage(replayMessageInfo: ReplayMessageInfo?) {
        val messageText = binding.edtMessage.text.toString()
        if (!StringHelper.isEmpty(messageText)) {
            binding.edtMessage.text.clear()
            val message = MessageInfo()
            message.content = messageText
            message.sender_id = currentUser
            message.timestamp = Date()
            if (replayMessageInfo != null)
                message.replyMessage = replayMessageInfo
            db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
                .collection(AppConstants.FCM_MESSAGES)
                .add(message)
            removeReplayView()
        }
    }

    private fun sendImage(fileInfo: FileInfo, file: File) {
        val messageText = binding.edtMessage.text.toString()
        binding.edtMessage.text.clear()
        val message = MessageInfo()
        message.content = messageText
        message.sender_id = currentUser
        message.timestamp = Date()
        message.file = fileInfo

        db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
            .collection(AppConstants.FCM_MESSAGES)
            .add(message).addOnCompleteListener {
                if (it.isSuccessful) {
                    val doc: DocumentReference = it.result
                    Log.e("test", "ID:" + doc.id)
                    uploadImage(doc.id, fileInfo, file)
                }
            }
        sendMessageType = 0
        binding.edtMessage.text.clear()
    }

    private fun uploadImage(messageId: String, fileInfo: FileInfo, file: File) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("files/$currentUser/$messageId/${file.name}")
        val uri = Uri.fromFile(file)
        storageRef.putFile(uri).addOnCompleteListener {
            if (it.isSuccessful) {
                it.result.task.snapshot.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful) {
                        val fullUrl = "https:${it.result.encodedSchemeSpecificPart}"
                        fileInfo.url = fullUrl
                        db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
                            .collection(AppConstants.FCM_MESSAGES).document(messageId)
                            .update("file", fileInfo)
//                            .addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    Log.e("test", "isSuccessful")
//                                }
//                            }
                    }
                }
            }
        }
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        var rightItemBinding: RowChatRightItemBinding? = null
        var leftItemBinding: RowChatLeftItemBinding? = null

        constructor(binding: RowChatRightItemBinding) : super(binding.root) {
            rightItemBinding = binding
        }

        constructor(binding: RowChatLeftItemBinding) : super(binding.root) {
            leftItemBinding = binding
        }
    }

    inner class MessageAdapter internal constructor(options: FirestoreRecyclerOptions<MessageInfo>) :
        FirestoreRecyclerAdapter<MessageInfo, MyViewHolder>(options) {
        private var lastDate: String = ""

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding: ViewDataBinding
            return if (viewType == AppConstants.Type.TYPE_ME) {
                binding =
                    DataBindingUtil.inflate(inflater, R.layout.row_chat_right_item, parent, false)
                MyViewHolder(binding as RowChatRightItemBinding)
            } else {
                binding =
                    DataBindingUtil.inflate(inflater, R.layout.row_chat_left_item, parent, false)
                MyViewHolder(binding as RowChatLeftItemBinding)
            }
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int, info: MessageInfo) {
            val itemViewHolder = holder as MyViewHolder
            when (itemViewHolder.itemViewType) {
                AppConstants.Type.TYPE_ME -> {
                    val binding: RowChatRightItemBinding = holder.rightItemBinding!!
                    if (info.isSelected)
                        binding.routOverLayer.visibility = View.VISIBLE
                    else
                        binding.routOverLayer.visibility = View.GONE
                    setDeleteMessage(binding.parentView, info.ind_delete)
                    if (!StringHelper.isEmpty(info.content)) {
                        binding.txtMsg.visibility = View.VISIBLE
                        binding.txtMsg.text = info.content
                    } else {
                        binding.txtMsg.visibility = View.GONE
                    }
                    setTime(binding.txtTime, info.timestamp)
                    setDate(binding.txtHeaderDate, info.timestamp, position, info.ind_delete)
                    setSeen(binding.imgLastSeen, info.seen)

                    if (info.file != null) {
                        binding.imgChat.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.VISIBLE
                        if (!StringHelper.isEmpty(info.file!!.url))
                            setImage(
                                info.file!!.url!!,
                                binding.imgChat,
                                binding.progressBar
                            )
                    } else {
                        binding.imgChat.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }

                    setReplayViewRight(binding, info.replyMessage)

                    binding.parentView.setOnLongClickListener {
                        if (!isMessageSelected)
                            showMessageItemMenu(position, binding.imgMenuItem, true)
                        return@setOnLongClickListener true
                    }

                    binding.imgChat.setOnLongClickListener {
                        if (!isMessageSelected)
                            showMessageItemMenu(position, binding.imgMenuItem, true)
                        return@setOnLongClickListener true
                    }

                    binding.imgChat.setOnClickListener {
                        if (isMessageSelected) {
                            info.isSelected = !info.isSelected
                            if (getTotalSelectedItems() == 0) {
                                deSelectAllMessages()
                            } else {
                                notifyDataSetChanged()
                            }
                        } else {
                            showPreviewImage(info.file?.url)
                        }
                    }

                    binding.parentView.setOnClickListener {
                        if (isMessageSelected) {
                            info.isSelected = !info.isSelected
                            if (getTotalSelectedItems() == 0) {
                                deSelectAllMessages()
                            } else {
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
                AppConstants.Type.TYPE_FROM -> {
                    val binding: RowChatLeftItemBinding = holder.leftItemBinding!!
                    if (info.isSelected)
                        binding.routOverLayer.visibility = View.VISIBLE
                    else
                        binding.routOverLayer.visibility = View.GONE
                    setDeleteMessage(binding.parentView, info.ind_delete)
                    if (!StringHelper.isEmpty(info.content)) {
                        binding.txtMsg.visibility = View.VISIBLE
                        binding.txtMsg.text = info.content
                    } else {
                        binding.txtMsg.visibility = View.GONE
                    }
                    setTime(binding.txtTime, info.timestamp)
                    setDate(binding.txtHeaderDate, info.timestamp, position, info.ind_delete)
                    manageSeenMessage(snapshots.getSnapshot(position).id, info.seen)

                    if (info.file != null) {
                        binding.imgChat.visibility = View.VISIBLE
                        binding.progressBar.visibility = View.VISIBLE
                        if (!StringHelper.isEmpty(info.file!!.url))
                            setImage(
                                info.file!!.url!!,
                                binding.imgChat,
                                binding.progressBar
                            )
                    } else {
                        binding.imgChat.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                    }

                    setReplayViewLeft(binding, info.replyMessage)

                    binding.parentView.setOnLongClickListener {
                        if (!isMessageSelected)
                            showMessageItemMenu(position, binding.imgMenuItem, false)
                        return@setOnLongClickListener true
                    }

                    binding.imgChat.setOnLongClickListener {
                        if (!isMessageSelected)
                            showMessageItemMenu(position, binding.imgMenuItem, false)
                        return@setOnLongClickListener true
                    }

                    binding.imgChat.setOnClickListener {
                        if (isMessageSelected) {
                            info.isSelected = !info.isSelected
                            if (getTotalSelectedItems() == 0) {
                                deSelectAllMessages()
                            } else {
                                notifyDataSetChanged()
                            }
                        } else {
                            showPreviewImage(info.file?.url)
                        }
                    }

                    binding.parentView.setOnClickListener {
                        if (isMessageSelected) {
                            info.isSelected = !info.isSelected
                            if (getTotalSelectedItems() == 0) {
                                deSelectAllMessages()
                            } else {
                                notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (getItem(position).sender_id == currentUser) {
                AppConstants.Type.TYPE_ME
            } else {
                AppConstants.Type.TYPE_FROM
            }
        }

        override fun onDataChanged() {
            setLastVisibleItemIndex(adapter!!.snapshots)
            if (!initialLoaded) {
                initialLoaded = true
                binding.rvUserChat.layoutManager!!.scrollToPosition(itemCount - 1)
            }
        }

        override fun onChildChanged(
            type: ChangeEventType,
            snapshot: DocumentSnapshot,
            newIndex: Int,
            oldIndex: Int
        ) {
            super.onChildChanged(type, snapshot, newIndex, oldIndex)
            val info: MessageInfo = snapshot.toObject(MessageInfo::class.java)!!
            if (type === ChangeEventType.ADDED) {
                if (initialLoaded) {
                    if (lastCompletelyVisibleItemPosition < adapter!!.itemCount - 2
                        && info.sender_id != currentUser
                    ) {
                        newChatCount += 1
                        binding.txtMessageCount.visibility = View.VISIBLE
                        binding.txtMessageCount.text = newChatCount.toString()
                    } else {
                        binding.rvUserChat.smoothScrollToPosition(adapter!!.itemCount - 1)
                    }
                }

            }
        }

        private fun setTime(texView: TextView, date: Date?) {
            if (date != null) {
                texView.visibility = View.VISIBLE
                texView.text = AppUtils.getFirebaseTime(date)
            } else {
                texView.visibility = View.GONE
            }
        }

        private fun setDate(
            texView: TextView,
            date: Date?,
            position: Int,
            list: MutableList<String>?
        ) {
            if (!isDeleted(list)) {
                lastDate = AppUtils.checkFirebaseDate(date!!)
                if (adapter!!.snapshots.size > 0) {
                    if (position > 0) {
                        if (AppUtils.checkFirebaseDate(adapter!!.snapshots[position - 1].timestamp!!) != lastDate
                            || lastVisibleMessageId == adapter!!.snapshots.getSnapshot(position).id
                        ) {
                            texView.visibility = View.VISIBLE
                            texView.text = AppUtils.getFirebaseDate(date)
                        } else {
                            texView.visibility = View.GONE
                        }
                    } else {
                        texView.visibility = View.VISIBLE
                        texView.text = AppUtils.getFirebaseDate(date)
                    }
                } else {
                    texView.visibility = View.VISIBLE
                    texView.text = AppUtils.getFirebaseDate(date)
                }
            } else {
                texView.visibility = View.GONE
            }

        }

        private fun isDeleted(list: MutableList<String>?): Boolean {
            return (list != null && list.size > 0
                    && currentUser in list)
        }

        private fun setSeen(imageView: AppCompatImageView, seen: HashMap<String, Date?>?) {
            var isSeen = false
            if (seen != null) {
                for (i in seen.keys) {
                    if (i != currentUser) {
                        isSeen = true
                        break
                    }
                }
            }
            if (isSeen)
                imageView.setColorFilter(resources.getColor(R.color.colorAccent))
            else
                imageView.setColorFilter(resources.getColor(R.color.colorDarkDivider))
        }

        private fun setImage(url: String, imageView: AppCompatImageView, progressBar: ProgressBar) {
            GlideUtil.loadImageWithProgressBar(
                url, imageView, AppUtils.getEmptyGalleryDrawable(mContext), progressBar,
                Constant.ImageScaleType.CENTER_CROP, null
            )
        }

        private fun setReplayViewLeft(binding: RowChatLeftItemBinding, info: ReplayMessageInfo?) {
            if (info != null && !StringHelper.isEmpty(info.sender_id)) {
                binding.routReplayView.visibility = View.VISIBLE

                if (!StringHelper.isEmpty(info.content)) {
                    binding.txtReplay.visibility = View.VISIBLE
                    binding.txtReplay.text = info.content
                } else {
                    binding.txtReplay.visibility = View.GONE
                }

                if (info.file != null && !StringHelper.isEmpty(info.file?.url)) {
                    binding.imgReplay.visibility = View.VISIBLE
                    AppUtils.setImage(mContext, info.file?.url, binding.imgReplay)
                } else {
                    binding.imgReplay.visibility = View.GONE
                }
            } else {
                binding.routReplayView.visibility = View.GONE
            }
        }

        private fun setReplayViewRight(binding: RowChatRightItemBinding, info: ReplayMessageInfo?) {
            if (info != null && !StringHelper.isEmpty(info.sender_id)) {
                binding.routReplayView.visibility = View.VISIBLE

                if (!StringHelper.isEmpty(info.content)) {
                    binding.txtReplay.visibility = View.VISIBLE
                    binding.txtReplay.text = info.content
                } else {
                    binding.txtReplay.visibility = View.GONE
                }

                if (info.file != null && !StringHelper.isEmpty(info.file?.url)) {
                    binding.imgReplay.visibility = View.VISIBLE
                    AppUtils.setImage(mContext, info.file?.url, binding.imgReplay)
                } else {
                    binding.imgReplay.visibility = View.GONE
                }
            } else {
                binding.routReplayView.visibility = View.GONE
            }
        }

        private fun setDeleteMessage(view: View, list: MutableList<String>?) {
            if (list != null && list.size > 0
                && currentUser in list
            )
                view.visibility = View.GONE
            else
                view.visibility = View.VISIBLE
        }
    }

    private fun showMessageItemMenu(position: Int, v: View, isMe: Boolean) {
        selectedMessageItem = position

        listMenuItems = ArrayList()

        val copy = ModuleInfo()
        copy.id = AppConstants.Action.COPY_MESSAGE
        copy.name = mContext.getString(R.string.copy)
        listMenuItems.add(copy)

        val replay = ModuleInfo()
        replay.id = AppConstants.Action.REPLAY_MESSAGE
        replay.name = mContext.getString(R.string.replay)
        listMenuItems.add(replay)

        val delete = ModuleInfo()
        delete.id = AppConstants.Action.DELETE_MESSAGE
        delete.name = mContext.getString(R.string.delete)
        listMenuItems.add(delete)

        if (isMe) {
            val unSend = ModuleInfo()
            unSend.id = AppConstants.Action.UN_SEND_MESSAGE
            unSend.name = mContext.getString(R.string.unsend)
            listMenuItems.add(unSend)
        }

        val select = ModuleInfo()
        select.id = AppConstants.Action.SELECT_MESSAGE
        select.name = mContext.getString(R.string.select)
        listMenuItems.add(select)

        PopupMenuHelper.showPopupMenu(
            mContext,
            v,
            listMenuItems,
            AppConstants.Action.MESSAGE_ITEM_MENU,
            this
        )
    }

    override fun onSelectItem(position: Int, action: Int) {
        if (action == AppConstants.Action.MESSAGE_ITEM_MENU) {
            when (listMenuItems[position].id) {
                AppConstants.Action.COPY_MESSAGE -> copyContent()
                AppConstants.Action.REPLAY_MESSAGE -> displayReplayView()
                AppConstants.Action.DELETE_MESSAGE -> deleteMessage(selectedMessageItem)
                AppConstants.Action.UN_SEND_MESSAGE -> unSendMessage()
                AppConstants.Action.SELECT_MESSAGE -> selectMessage()
            }
        }
    }

    private fun showSelectAttachmentDialog() {
        val fm = (mContext as BaseActivity).supportFragmentManager
        val selectAttachmentDialog =
            SelectAttachmentDialog(this, this, 0, true, true)
        selectAttachmentDialog.show(fm, "selectAttachmentDialog")
    }

    private fun hasPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            this, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun checkPermission() {
        if (hasPermission()) {
            showSelectAttachmentDialog()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.msg_storage_permission),
                AppConstants.IntentKey.EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        showSelectAttachmentDialog()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onSelectAttachment(action: Int) {
        if (action == AppConstants.Type.SELECT_PHOTOS) {
            onSelectFromGallery()
        } else if (action == AppConstants.Type.SELECT_FROM_CAMERA) {
            onSelectFromCamera()
        }
    }

    private fun onSelectFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        selectPhotosRequest.launch(intent)
    }

    private fun onSelectFromCamera() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val fileWithPath: FileWithPath = AppUtils.createImageFile(
                "",
                AppConstants.Type.CAMERA,
                AppConstants.FileExtension.JPG
            )
            currentPhotoPath = fileWithPath.filePath
            val photoURI = FileProvider.getUriForFile(
                mContext, BuildConfig.APPLICATION_ID + ".provider",
                fileWithPath.file
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            selectFromCameraRequest.launch(takePictureIntent)
        } catch (e: Exception) {

        }
    }

    var selectPhotosRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent = result.data!!
                val realPath: String = FileUtils.getPath(mContext, data.data)!!
                Log.e("test", "realPath:$realPath")
                if (!StringHelper.isEmpty(realPath)) {
//                    sendImage(getFileInfo(File(realPath)), File(realPath))
                    displaySendImage(realPath)
                }
            }
        }

    var selectFromCameraRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val file = File(
                        FileUtils.getPath(mContext, Uri.parse(currentPhotoPath))!!
                    )
                    var realPath = ""
                    val fileWithPath: FileWithPath =
                        AppUtils.compressImage(file.absolutePath, File(file.absolutePath))!!

                    if (fileWithPath.uri != null)
                        realPath = FileUtils.getPath(
                            mContext,
                            Uri.parse(fileWithPath.filePath)
                        )!! else
                        file.path
                    Log.e("test", "realPath:$realPath")
//                    imagePath = realPath
//                    sendImage(getFileInfo(File(realPath)), File(realPath))
                    displaySendImage(realPath)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun displaySendImage(realPath: String) {
        removeReplayView()
        sendMessageType = AppConstants.Type.MESSAGE_TYPE_IMAGE
        imagePath = realPath
        binding.routSendImageView.visibility = View.VISIBLE
        binding.edtMessage.requestFocus()
        GlobalScope.launch {
            delay(200L)
            AppUtils.showKeyBoard(mContext)
        }

        GlideUtil.loadImageFromFile(
            binding.imgSendImage,
            File(realPath),
            AppUtils.getEmptyGalleryDrawable(mContext),
            AppUtils.getEmptyGalleryDrawable(mContext),
            Constant.ImageScaleType.FIT_CENTER,
            null
        )
    }

    private fun removeSendImage() {
        sendMessageType = 0
        imagePath = ""
        binding.routSendImageView.visibility = View.GONE
//        AppUtils.hideKeyBoard(mContext)
    }

    private fun getFileInfo(file: File): FileInfo {
        val size = file.length()
        val name = file.name.substringBefore(".")
        val extension = FileUtils.getExtension(file.absolutePath)!!.replace(".", "")
        val type = FileUtils.getMimeType(file)
        return FileInfo(name, extension, size, type, null)
    }

    private fun copyContent() {
        val info: MessageInfo = adapter!!.getItem(selectedMessageItem)
        if (!StringHelper.isEmpty(info.content)) {
            val clipboard: ClipboardManager =
                getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", info.content)
            clipboard.setPrimaryClip(clip)
            ToastHelper.normal(
                mContext,
                getString(R.string.msg_copied_text),
                Toast.LENGTH_SHORT,
                false
            )
        }
    }

    private fun displayReplayView() {
        removeSendImage()
        sendMessageType = AppConstants.Type.MESSAGE_TYPE_REPLAY
        binding.routReplayView.visibility = View.VISIBLE
        val info: MessageInfo = adapter!!.getItem(selectedMessageItem)
        if (info.file != null && !StringHelper.isEmpty(info.file!!.url)) {
            binding.imgReplayImage.visibility = View.VISIBLE
            AppUtils.setImage(mContext, info.file!!.url, binding.imgReplayImage)
        } else {
            binding.imgReplayImage.visibility = View.GONE
        }

        if (users.containsKey(info.sender_id))
            binding.txtUserName.text = users[info.sender_id]!!.name

        if (!StringHelper.isEmpty(info.content))
            binding.txtMessage.text = info.content
        else
            binding.txtMessage.text = ""

        binding.edtMessage.requestFocus()
        GlobalScope.launch {
            delay(200L)
            AppUtils.showKeyBoard(mContext)
        }
    }

    private fun removeReplayView() {
        sendMessageType = 0
        binding.routReplayView.visibility = View.GONE
//        AppUtils.hideKeyBoard(mContext)
    }

    private fun deleteMessage(position: Int) {
        val info = adapter!!.getItem(position)
        val messageId = adapter!!.snapshots.getSnapshot(position).id
        val users = info.ind_delete
        users.add(currentUser)
        db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
            .collection(AppConstants.FCM_MESSAGES).document(messageId)
            .update("ind_delete", users)
    }

    private fun unSendMessage() {
        val messageId = adapter!!.snapshots.getSnapshot(selectedMessageItem).id
        db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
            .collection(AppConstants.FCM_MESSAGES).document(messageId).delete()
    }

    private fun selectMessage() {
        menu.findItem(R.id.action_delete).isVisible = true
        isMessageSelected = true
        adapter!!.snapshots[selectedMessageItem].isSelected = true
        adapter!!.notifyDataSetChanged()
    }

    private fun deSelectMessage() {
        menu.findItem(R.id.action_delete).isVisible = false
        isMessageSelected = false
    }

    private fun deSelectAllMessages() {
        menu.findItem(R.id.action_delete).isVisible = false
        isMessageSelected = false
        for (i in adapter!!.snapshots.indices)
            adapter!!.snapshots[i].isSelected = false
        adapter!!.notifyDataSetChanged()
    }

    private fun getVisibleItemCount(messages: MutableList<MessageInfo>): Int {
        var count = 0
        for (message in messages) {
            if (message.ind_delete.size == 0
                || currentUser !in message.ind_delete
            )
                count += 1
        }
        return count
    }

    private fun setLastVisibleItemIndex(messages: MutableList<MessageInfo>) {
        for (i in messages.indices) {
            if (messages[i].ind_delete.size == 0
                || currentUser !in messages[i].ind_delete
            ) {
                lastVisibleMessageId = adapter!!.snapshots.getSnapshot(i).id
                Log.e("test", "lastVisibleMessageId:" + lastVisibleMessageId);
                Log.e("test", "content::" + messages[i].content);
                break
            }
        }
    }

    private fun getTotalSelectedItems(): Int {
        var totalItems = 0
        for (message in adapter!!.snapshots) {
            if (message.isSelected)
                totalItems += 1
        }
        return totalItems
    }

    private fun getAllUserDetails() {
//        val query =
//            db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!).get()
//        val data = query.await()
//        val info: ChannelInfo = data!!.toObject(ChannelInfo::class.java)!!
//        for (user in info.users) {
//            val userInfo: UserInfo = FirebaseUtils.getUserDetails(user)!!
//            users[user] = user
//        }

        val listUsers = AppUtils.getChatUserPreference(mContext)!!.info
        for (user in listUsers) {
            users[user._id] = user
        }

        if (users.size > 0)
            setUserData()

    }

    private fun displayTypingUser() {
        db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
            .addSnapshotListener { value, error ->
                val info: ChannelInfo = value!!.toObject(ChannelInfo::class.java)!!
                channelInfo.typingUsers = info.typingUsers
                var typingUser = ""
                for (user in info.typingUsers) {
                    if (user != currentUser && users.containsKey(user))
                        typingUser = users[user]!!.name

                }
                if (!StringHelper.isEmpty(typingUser)) {
//                    binding.txtWhoTyping.text =
//                        String.format(getString(R.string.display_typing_user), typingUser)
                    binding.toolbar.toolbar.txtLastSeen.visibility = View.VISIBLE
                    binding.toolbar.toolbar.txtLastSeen.text =
                        String.format(getString(R.string.display_typing_user), typingUser)
                } else {
                    setLastSeenData()
                }
            }

    }

    private fun manageTypingUser() {
        binding.edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edtMessage.text.isNotEmpty()) {
                    if (currentUser !in channelInfo.typingUsers)
                        channelInfo.typingUsers.add(currentUser)
                } else {
                    if (currentUser in channelInfo.typingUsers)
                        channelInfo.typingUsers.remove(currentUser)
                }
                db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
                    .update("typingUsers", channelInfo.typingUsers)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun manageSeenMessage(messageId: String, seen: HashMap<String, Date?>?) {
        if (seen == null || !seen.containsKey(currentUser)) {
            val data: HashMap<String, Date> = HashMap()
            data[currentUser] = Date()
            db.collection(AppConstants.FCM_ROOM).document(channelInfo.roomId!!)
                .collection(AppConstants.FCM_MESSAGES).document(messageId)
                .update("seen", data)
        }
    }

    private fun setScrollEvent() {
        binding.rvUserChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                lastCompletelyVisibleItemPosition =
                    layoutManager.findLastCompletelyVisibleItemPosition()
                if (lastCompletelyVisibleItemPosition < adapter!!.itemCount - 2
                    && getVisibleItemCount(adapter!!.snapshots) > 0
                ) {
                    binding.routMoveToBottom.visibility = View.VISIBLE
                } else {
                    newChatCount = 0
                    binding.txtMessageCount.text = newChatCount.toString()
                    binding.txtMessageCount.visibility = View.GONE
                    binding.routMoveToBottom.visibility = View.GONE
                }
            }
        })

        binding.rvUserChat.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom && lastCompletelyVisibleItemPosition >= adapter!!.itemCount - 2) {
                binding.rvUserChat.postDelayed({
                    if (adapter!!.itemCount - 1 >= 0)
                        binding.rvUserChat.smoothScrollToPosition(
                            adapter!!.itemCount - 1
                        )
                }, 100)
            }
        }
    }

    private fun setUserData() {
        if (channelInfo.users.isNotEmpty()) {
            for (id in channelInfo.users) {
                if (id != currentUser)
                    friendId = id
            }
            try {
                runOnUiThread {
                    setupToolbar("", true)
                    binding.routMainView.visibility = View.VISIBLE
                    hideProgressDialog()
                    binding.toolbar.toolbar.toolbarChatView.visibility = View.VISIBLE
                    if (users.containsKey(friendId)) {
                        binding.toolbar.toolbar.txtTitle.text = users[friendId]!!.name
                        val imageUrl = users[friendId]!!.image
                        binding.toolbar.toolbar.imgUser.visibility = View.VISIBLE
                        AppUtils.setUserImage(
                            applicationContext,
                            imageUrl,
                            binding.toolbar.toolbar.imgUser
                        )
                        AppUtils.setImage(
                            applicationContext,
                            imageUrl,
                            binding.imgJob
                        )
                    }

                    manageLastSeenStatus()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun manageLastSeenStatus() {
        FirebaseDatabase.getInstance().reference
            .child("status").child(friendId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val activeStatus: String? =
                        snapshot.child("state").value?.toString()
                    val lastSeen: Long? = snapshot.child("lastChanged").value as Long?

                    if (!StringHelper.isEmpty(activeStatus))
                        statusInfo!!.state = activeStatus!!

                    if (lastSeen != null)
                        statusInfo!!.lastChanged = lastSeen

                    setLastSeenData()

                    displayTypingUser()

                    manageTypingUser()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun setLastSeenData() {
        if (statusInfo != null && !StringHelper.isEmpty(statusInfo?.state)) {
            binding.toolbar.toolbar.txtLastSeen.visibility = View.VISIBLE
            if (statusInfo!!.state == "online") {
                binding.toolbar.toolbar.txtLastSeen.text = "Active Now"
                binding.toolbar.toolbar.imgOnlineStatus.visibility = View.VISIBLE
            } else {
                if (statusInfo!!.lastChanged != null)
                    binding.toolbar.toolbar.txtLastSeen.text =
                        LastSeenTime.getTimeAgo(statusInfo!!.lastChanged!!, mContext)
                binding.toolbar.toolbar.imgOnlineStatus.visibility = View.GONE
            }
        } else {
            binding.toolbar.toolbar.txtLastSeen.visibility = View.GONE
        }
    }

    private fun showPreviewImage(imageUrl: String?) {
        if (!StringHelper.isEmpty(imageUrl)) {
            binding.previewImage.routRootView.visibility = View.VISIBLE
            GlideUtil.loadImage(
                imageUrl,
                binding.previewImage.imgPreviewImage,
                AppUtils.getEmptyGalleryDrawable(mContext),
                AppUtils.getEmptyGalleryDrawable(mContext),
                Constant.ImageScaleType.FIT_CENTER,
                null
            )
        }
    }

    private fun closePreviewImage() {
        binding.previewImage.routRootView.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.chat_menu, menu)
        this.menu = menu
        AppUtils.setToolbarTextColor(
            menu.findItem(R.id.clearChat),
            resources.getString(R.string.clear_chat),
            resources.getColor(R.color.colorBlack)
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clearChat -> {
                AlertDialogHelper.showDialog(
                    mContext,
                    "",
                    getString(R.string.clear_chat_message),
                    getString(R.string.clear),
                    getString(R.string.cancel),
                    false,
                    this,
                    AppConstants.DialogIdentifier.CLEAR_CHAT
                )
            }
            R.id.action_delete -> {
                AlertDialogHelper.showDialog(
                    mContext,
                    "",
                    getString(R.string.delete_selected_messages),
                    getString(R.string.yes),
                    getString(R.string.no),
                    false,
                    this,
                    AppConstants.DialogIdentifier.DELETE_SELECTED_MESSAGE
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPositiveButtonClicked(dialogIdentifier: Int) {
        if (dialogIdentifier == AppConstants.DialogIdentifier.CLEAR_CHAT) {
            deSelectMessage()
            for (i in adapter!!.snapshots.indices) {
                deleteMessage(i)
            }
        } else if (dialogIdentifier == AppConstants.DialogIdentifier.DELETE_SELECTED_MESSAGE) {
            deSelectMessage()
            for (i in adapter!!.snapshots.indices) {
                if (adapter!!.snapshots[i].isSelected)
                    deleteMessage(i)
            }
        }
    }

    override fun onNegativeButtonClicked(dialogIdentifier: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null)
            adapter!!.startListening()
    }

    override fun onBackPressed() {
        if (isMessageSelected) {
            deSelectAllMessages()
        } else if (binding.previewImage.routRootView.visibility == View.VISIBLE)
            binding.previewImage.routRootView.visibility = View.GONE
        else {
            finish()
        }
    }

    /*override fun onStart() {
        super.onStart()
        Log.e("test", "onStart");
        if (adapter != null) {
            adapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }*/
}