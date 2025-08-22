package com.localllm.localaichatapp.presentation.performance

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.localllm.localaichatapp.domain.model.Benchmark
import com.localllm.localaichatapp.domain.model.TaskType
import com.localllm.localaichatapp.presentation.theme.extendedColors
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    modelId: String,
    onBackClick: () -> Unit,
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(modelId) {
        viewModel.loadPerformanceData(modelId)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PerformanceTopBar(
            modelName = uiState.currentModel?.displayName ?: "Unknown Model",
            onBackClick = onBackClick,
            onRefreshClick = { viewModel.loadPerformanceData(modelId) }
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Performance Overview Cards
                item {
                    PerformanceOverviewSection(
                        averageTtft = uiState.overallMetrics.avgTimeToFirstToken,
                        averageTokensPerSecond = uiState.overallMetrics.avgTokensPerSecond,
                        totalSessions = uiState.overallMetrics.totalSessions,
                        averageLatency = uiState.overallMetrics.avgLatency
                    )
                }

                // Task Type Performance
                item {
                    TaskTypePerformanceSection(
                        taskPerformance = uiState.taskTypePerformances.associate { 
                            it.taskType to it.metrics.avgTokensPerSecond 
                        }
                    )
                }

                // Performance Chart
                item {
                    PerformanceChartSection(
                        recentBenchmarks = uiState.recentBenchmarks
                    )
                }

                // System Resources
                item {
                    SystemResourcesSection(
                        averageMemoryUsage = uiState.systemResources.memoryUsage,
                        averageCpuUsage = uiState.systemResources.cpuUsage,
                        batteryImpact = uiState.systemResources.batteryLevel
                    )
                }

                // Recent Benchmarks
                item {
                    RecentBenchmarksSection(
                        benchmarks = uiState.recentBenchmarks.take(10)
                    )
                }

                // Performance Tips
                item {
                    PerformanceTipsSection()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerformanceTopBar(
    modelName: String,
    onBackClick: () -> Unit,
    onRefreshClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Performance Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = modelName.ifBlank { "Loading..." },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.extendedColors.infoColor
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun PerformanceOverviewSection(
    averageTtft: Float,
    averageTokensPerSecond: Float,
    totalSessions: Int,
    averageLatency: Float
) {
    Column {
        Text(
            text = "Performance Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PerformanceMetricCard(
                    title = "Avg TTFT",
                    value = "${averageTtft.toInt()}ms",
                    icon = Icons.Default.Speed,
                    color = MaterialTheme.extendedColors.excellentPerformance
                )
            }
            item {
                PerformanceMetricCard(
                    title = "Tokens/sec",
                    value = String.format("%.1f", averageTokensPerSecond),
                    icon = Icons.Default.Analytics,
                    color = MaterialTheme.extendedColors.goodPerformance
                )
            }
            item {
                PerformanceMetricCard(
                    title = "Sessions",
                    value = totalSessions.toString(),
                    icon = Icons.Default.Chat,
                    color = MaterialTheme.extendedColors.infoColor
                )
            }
            item {
                PerformanceMetricCard(
                    title = "Avg Latency",
                    value = "${averageLatency.toInt()}ms",
                    icon = Icons.Default.Timer,
                    color = MaterialTheme.extendedColors.averagePerformance
                )
            }
        }
    }
}

@Composable
private fun PerformanceMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TaskTypePerformanceSection(
    taskPerformance: Map<TaskType, Float>
) {
    Column {
        Text(
            text = "Performance by Task Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                taskPerformance.forEach { (taskType, score) ->
                    TaskPerformanceItem(
                        taskType = taskType,
                        score = score
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskPerformanceItem(
    taskType: TaskType,
    score: Float
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    val color = when {
        score >= 80f -> MaterialTheme.extendedColors.excellentPerformance
        score >= 60f -> MaterialTheme.extendedColors.goodPerformance
        score >= 40f -> MaterialTheme.extendedColors.averagePerformance
        score >= 20f -> MaterialTheme.extendedColors.poorPerformance
        else -> MaterialTheme.extendedColors.badPerformance
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (taskType) {
                        TaskType.CHAT -> Icons.Default.Chat
                        TaskType.ASK_IMAGE -> Icons.Default.Image
                        TaskType.ASK_AUDIO -> Icons.Default.Mic
                        TaskType.PROMPT_LAB -> Icons.Default.Psychology
                    },
                    contentDescription = taskType.name,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = taskType.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Text(
                text = "${score.toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun PerformanceChartSection(
    recentBenchmarks: List<Benchmark>
) {
    Column {
        Text(
            text = "Performance Trend",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            if (recentBenchmarks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No performance data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                SimpleLineChart(
                    data = recentBenchmarks.map { it.tokensPerSecond },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun SimpleLineChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    
    Canvas(
        modifier = modifier
    ) {
        if (data.isEmpty()) return@Canvas
        
        val maxValue = data.maxOrNull() ?: 1f
        val minValue = data.minOrNull() ?: 0f
        val range = maxValue - minValue
        
        val stepX = size.width / (data.size - 1).coerceAtLeast(1)
        
        for (i in 0 until data.size - 1) {
            val x1 = i * stepX
            val y1 = size.height - ((data[i] - minValue) / range) * size.height
            val x2 = (i + 1) * stepX
            val y2 = size.height - ((data[i + 1] - minValue) / range) * size.height
            
            drawLine(
                color = primary,
                start = androidx.compose.ui.geometry.Offset(x1, y1),
                end = androidx.compose.ui.geometry.Offset(x2, y2),
                strokeWidth = 4.dp.toPx()
            )
        }
        
        // Draw data points
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range) * size.height
            
            drawCircle(
                color = primary,
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
}

@Composable
private fun SystemResourcesSection(
    averageMemoryUsage: Float,
    averageCpuUsage: Float,
    batteryImpact: Float
) {
    Column {
        Text(
            text = "System Resources",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResourceGaugeCard(
                title = "Memory",
                value = averageMemoryUsage,
                unit = "MB",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.extendedColors.infoColor
            )
            ResourceGaugeCard(
                title = "CPU",
                value = averageCpuUsage,
                unit = "%",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.extendedColors.warningColor
            )
            ResourceGaugeCard(
                title = "Battery",
                value = batteryImpact,
                unit = "%/hr",
                modifier = Modifier.weight(1f),
                color = MaterialTheme.extendedColors.errorColor
            )
        }
    }
}

@Composable
private fun ResourceGaugeCard(
    title: String,
    value: Float,
    unit: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CircularGauge(
                value = value,
                maxValue = if (unit == "MB") 200f else 100f,
                color = color,
                modifier = Modifier.size(60.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${value.toInt()} $unit",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CircularGauge(
    value: Float,
    maxValue: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 1000),
        label = "gauge"
    )
    
    Canvas(
        modifier = modifier
    ) {
        val strokeWidth = 6.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        
        // Background arc
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius,
            center = center,
            style = Stroke(strokeWidth)
        )
        
        // Progress arc
        val sweepAngle = (animatedValue / maxValue) * 360f
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(strokeWidth),
            topLeft = androidx.compose.ui.geometry.Offset(
                center.x - radius,
                center.y - radius
            ),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}

@Composable
private fun RecentBenchmarksSection(
    benchmarks: List<Benchmark>
) {
    Column {
        Text(
            text = "Recent Performance Data",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (benchmarks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No benchmark data available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            benchmarks.forEach { benchmark ->
                BenchmarkItem(benchmark = benchmark)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun BenchmarkItem(
    benchmark: Benchmark
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = benchmark.taskType.name.replace("_", " "),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${benchmark.tokensPerSecond} tokens/sec",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${benchmark.ttftMs}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${benchmark.memoryUsageMb.toInt()}MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PerformanceTipsSection() {
    Column {
        Text(
            text = "Performance Tips",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.extendedColors.infoColor.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                PerformanceTip(
                    icon = Icons.Default.Lightbulb,
                    tip = "Close other apps to free up memory for better AI performance"
                )
                PerformanceTip(
                    icon = Icons.Default.BatteryFull,
                    tip = "Use power saver mode for longer sessions with slightly reduced speed"
                )
                PerformanceTip(
                    icon = Icons.Default.Tune,
                    tip = "Adjust model parameters in settings for optimal speed vs quality balance"
                )
            }
        }
    }
}

@Composable
private fun PerformanceTip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tip: String
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.infoColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = tip,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}