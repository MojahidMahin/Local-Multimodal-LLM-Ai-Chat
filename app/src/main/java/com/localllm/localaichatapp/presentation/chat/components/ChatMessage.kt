package com.localllm.localaichatapp.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localllm.localaichatapp.domain.model.*

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    when (message) {
        is TextMessage -> TextMessageBubble(message = message, modifier = modifier)
        is LoadingMessage -> LoadingMessageBubble(modifier = modifier)
        is ErrorMessage -> ErrorMessageBubble(message = message, modifier = modifier)
        is ImageMessage -> ImageMessageBubble(message = message, modifier = modifier)
    }
}

@Composable
private fun TextMessageBubble(
    message: TextMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == ChatSender.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = message.content,
                        color = textColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    
                    if (message.isStreaming) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TypingIndicator()
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun LoadingMessageBubble(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Thinking...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ErrorMessageBubble(
    message: ErrorMessage,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
                .padding(12.dp)
        ) {
            Text(
                text = message.error,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ImageMessageBubble(
    message: ImageMessage,
    modifier: Modifier = Modifier
) {
    val isUser = message.sender == ChatSender.USER
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Image placeholder - replace with actual image loading
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📷 Image",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            message.caption?.let { caption ->
                if (caption.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = caption,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = formatTimestamp(message.timestamp),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            var alpha by remember { mutableStateOf(0.3f) }
            
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(300L * (index + 1))
                    alpha = if (alpha == 0.3f) 1f else 0.3f
                }
            }
            
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        Color.White.copy(alpha = alpha),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}

@Preview
@Composable
private fun PreviewTextMessage() {
    MaterialTheme {
        Column {
            ChatMessageItem(
                message = TextMessage(
                    content = "Hello! How can I help you today?",
                    sender = ChatSender.AI
                )
            )
            ChatMessageItem(
                message = TextMessage(
                    content = "I need help with Android development",
                    sender = ChatSender.USER
                )
            )
            ChatMessageItem(
                message = LoadingMessage()
            )
        }
    }
}