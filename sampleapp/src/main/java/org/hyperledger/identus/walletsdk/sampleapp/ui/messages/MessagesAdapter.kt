package org.hyperledger.identus.walletsdk.sampleapp.ui.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.DelicateCoroutinesApi
import org.hyperledger.identus.walletsdk.domain.models.CredentialType
import org.hyperledger.identus.walletsdk.edgeagent.protocols.ProtocolType
import org.hyperledger.identus.walletsdk.sampleapp.R
import org.hyperledger.identus.walletsdk.ui.messages.MessagesFragment

class MessagesAdapter(
    private var data: MutableList<UiMessage> = mutableListOf(),
    private val validateListener: MessagesFragment.ValidateMessageListener
) : RecyclerView.Adapter<MessagesAdapter.MessageHolder>() {

    fun updateMessages(updatedMessages: List<UiMessage>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return data.size
            }

            override fun getNewListSize(): Int {
                return updatedMessages.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition].id == updatedMessages[newItemPosition].id && data[oldItemPosition].status == updatedMessages[newItemPosition].status
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition] == updatedMessages[newItemPosition] && data[oldItemPosition].status == updatedMessages[newItemPosition].status
            }
        })
        data.clear()
        data.addAll(updatedMessages)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateMessageStatus(updatedMessage: UiMessage) {
        val message = data.find { it.id == updatedMessage.id }
        val index = data.indexOf(message)
        data[index] = updatedMessage
        notifyItemChanged(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.placeholder_message, parent, false)
        return MessageHolder(view)
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        // Bind data to the views
        holder.bind(data[position], validateListener)
    }

    override fun getItemCount(): Int {
        // Return the size of your data list
        return data.size
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val type: TextView = itemView.findViewById(R.id.message_type)
        private val body: TextView = itemView.findViewById(R.id.message)
        private val validate: Button = itemView.findViewById(R.id.validate)
        private val validationError: TextView = itemView.findViewById(R.id.validation_error)

        @OptIn(DelicateCoroutinesApi::class)
        fun bind(message: UiMessage, validateListener: MessagesFragment.ValidateMessageListener) {
            type.text = message.piuri
            if (message.attachments.isNotEmpty()) {
                val attachmentDescriptor = message.attachments.first()
                if (message.piuri == ProtocolType.DidcommPresentation.value && attachmentDescriptor.format == CredentialType.PRESENTATION_EXCHANGE_SUBMISSION.type) {
                    validationError.visibility = View.GONE
                    validate.visibility = View.VISIBLE
                    validate.setOnClickListener { validateListener.validateMessage(message) }
                    message.status?.let { status ->
                        validationError.text = status
                        validationError.visibility = View.VISIBLE
                        validate.visibility = View.GONE
                    }
                }
            } else {
                validate.visibility = View.GONE
            }
        }
    }
}
