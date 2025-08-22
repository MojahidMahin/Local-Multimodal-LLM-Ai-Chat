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
        is ChatMessage.User -> UserMessageBubble(message = message, modifier = modifier)
        is ChatMessage.Assistant -> AssistantMessageBubble(message = message, modifier = modifier)
    }
}

@Composable
private fun UserMessageBubble(
    message: ChatMessage.User,
    modifier: Modifier = Modifier
) {
    val alignment = Alignment.CenterEnd
    val backgroundColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onPrimary
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 4.dp
                        )
                    )
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
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
private fun AssistantMessageBubble(
    message: ChatMessage.Assistant,
    modifier: Modifier = Modifier
) {
    val alignment = Alignment.CenterStart
    val backgroundColor = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 4.dp,
                            bottomEnd = 16.dp
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
                    
                    // Show typing indicator if content is empty (still streaming)
                    if (message.content.isEmpty()) {
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
private fun PreviewChatMessages() {
    MaterialTheme {
        Column {
            ChatMessageItem(
                message = ChatMessage.Assistant(
                    id = "1",
                    sessionId = "session1",
                    content = "Hello! How can I help you today?",
                    timestamp = System.currentTimeMillis()
                )
            )
            ChatMessageItem(
                message = ChatMessage.User(
                    id = "2",
                    sessionId = "session1",
                    content = "I need help with Android development",
                    timestamp = System.currentTimeMillis()
                )
            )
            ChatMessageItem(
                message = ChatMessage.Assistant(
                    id = "3",
                    sessionId = "session1",
                    content = "", // Empty content shows typing indicator
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}